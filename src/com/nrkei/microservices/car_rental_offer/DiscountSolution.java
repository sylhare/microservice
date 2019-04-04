package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;

public class DiscountSolution implements River.PacketListener {

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
    final River river = new River(rapidsConnection);

    river.requireValue("need", "car_rental_offer");  // listen for this offer
    river.forbid("solution");  // listen for this offer
    river.register(new DiscountSolution());
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
    rand.setSeed(System.currentTimeMillis());
    solution.put("additional_revenue", abs(rand.nextInt()) % 30);
    solution.put("likelyhood", rand.nextDouble() );
    solution.put("title", "Discount car" );
    packet.put("solution", solution);

    return packet;
  }

  //  }
  //    return packet.put(solution);
  //    solution.put("solution",  attributes);
  //    attributes.put("likelyhood", 2.0);
  //    attributes.put("additional_revenue", "a discount car" );
  //    attributes.put("title", "a discount car" );
  //    Map<String, Object> attributes = new HashMap<>();
  //    Map<String, Object> solution = new HashMap<>();
  //  private Packet randomSolutionPacket(Packet packet) {

}