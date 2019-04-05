package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

public class MembershipSolution implements River.PacketListener {

  private static String memberTier = "member_tier";
  private static String solutionID = UUID.randomUUID().toString();

    public static void main(String[] args) {
      String host = args[0];
      String port = args[1];

      final RapidsConnection rapidsConnection = new RabbitMqRapids("membership_solution", host, port);
      final River river = new River(rapidsConnection);

      river.requireValue("need", "car_rental_offer");  // listen for this offer
      river.require(memberTier);  // listen for this offer
      river.forbid("solution");  // listen for this offer
      river.register(new MembershipSolution());
    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
      String jsonMessage = solutionPacket(packet).toJson();
      System.out.println(String.format(" [<] %s", jsonMessage));
      connection.publish(jsonMessage);
    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
      //System.out.println(String.format(" [x] %s", errors));
    }

    private Packet solutionPacket(Packet packet) {
      Map<String,Object> solution = new HashMap<>();
      Random rand = new Random();
      String packetMemberTier = (String) packet.get(memberTier);
      Integer revenueFromMember = 5;
      Double likelyhoodFromMember = 0.5;

      if (packetMemberTier.equals("silver")) {
        revenueFromMember += 10;
        likelyhoodFromMember = 0.3;
      }

      if (packetMemberTier.equals("gold")) {
        revenueFromMember += 30;
        likelyhoodFromMember = 0.5;
      }

      if (packetMemberTier.equals("platinum")) {
        revenueFromMember += 50;
        likelyhoodFromMember = 0.9;
      }

      rand.setSeed(System.currentTimeMillis());
      solution.put("additional_revenue", abs(rand.nextInt()) % (revenueFromMember));
      solution.put("likelyhood", rand.nextDouble() * likelyhoodFromMember);
      solution.put("title", "Member car" );
      solution.put("solution_id", solutionID);
      packet.put("solution", solution);

      return packet;
    }

  }