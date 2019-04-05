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

public class PremiumSolution implements River.PacketListener {

  private static String solutionID = UUID.randomUUID().toString();

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
    String jsonMessage = solutionPacket(packet).toJson();
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }

  private Packet solutionPacket(Packet packet) {
    Map<String, Object> solution = new HashMap<>();
    Random rand = new Random();
    rand.setSeed(System.currentTimeMillis());
    solution.put("additional_revenue", abs(rand.nextInt()) % 50);
    solution.put("likelyhood", rand.nextDouble());
    solution.put("title", "Premium car");
    solution.put("solution_id", solutionID);
    packet.put("solution", solution);

    return packet;
  }

}
