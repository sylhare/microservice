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

public class SolutionCollector  implements River.PacketListener {

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
    final River river = new River(rapidsConnection);
    river.require("solution");       // Reject packet unless it has key1 and key2
    river.register(new SolutionCollector());         // Hook up to the river to start receiving traffic
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String jsonMessage = packet(packet).toJson();
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    // nothing
  }

  private static Packet packet(Packet packet) {
    Map<String,Object> solution = new HashMap<>();
    Random rand = new Random();
    rand.setSeed(System.currentTimeMillis());
    solution.put("additional_revenue", abs(rand.nextInt()) % 30);
    solution.put("likelyhood", rand.nextDouble() );
    solution.put("title", "a discount car" );
    packet.put("solution", solution);

    return packet;
  }
}