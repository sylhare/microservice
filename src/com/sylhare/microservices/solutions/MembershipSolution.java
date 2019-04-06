package com.sylhare.microservices.solutions;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;
import com.sylhare.microservices.NeedPacket;

import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

public class MembershipSolution implements River.PacketListener {

  private static String SOLUTION_ID = UUID.randomUUID().toString();
  private int baseRevenue = new Random().nextInt();
  private Double baseLikelyhood = new Random().nextDouble();

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("membership_solution", host, port);
    final River river = new River(rapidsConnection);

    river.requireValue("need", "car_rental_offer");
    river.require("member_tier");
    river.forbid("solution");
    river.register(new MembershipSolution());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String jsonMessage = enrichWithMemberPrice(packet);
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }

  private String enrichWithMemberPrice(Packet packet) {
    String packetMemberTier = (String) packet.get("member_tier");
    Integer revenueFromMember = 30;
    Double likelyhoodFromMember = 0.3;

    if (packetMemberTier.equals("silver")) {
      revenueFromMember += 30;
      likelyhoodFromMember = 0.4;
    }

    if (packetMemberTier.equals("gold")) {
      revenueFromMember += 20;
      likelyhoodFromMember = 0.5;
    }

    if (packetMemberTier.equals("platinum")) {
      revenueFromMember += 10;
      likelyhoodFromMember = 0.6;
    }

    return NeedPacket.enrichWithSolution(packet,
        baseLikelyhood * likelyhoodFromMember, abs(baseRevenue) % revenueFromMember,
        SOLUTION_ID);
  }


}