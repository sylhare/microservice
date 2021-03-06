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

public class PremiumSolution implements River.PacketListener {

  private static String SOLUTION_ID = UUID.randomUUID().toString();
  private int baseRevenue = new Random().nextInt();
  private Double baseLikelihood = new Random().nextDouble();

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("premium_solution", host, port);
    final River river = new River(rapidsConnection);

    river.requireValue("need", "car_rental_offer");  // listen for this offer
    river.forbid("solution");  // listen for this offer
    river.register(new PremiumSolution());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String jsonMessage = NeedPacket.enrichWithSolution(packet, baseLikelihood, abs(baseRevenue) % 50, SOLUTION_ID);
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }

}
