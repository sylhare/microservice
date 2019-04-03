package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;

public class DiscountSolution implements River.PacketListener {

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
    final River river = new River(rapidsConnection);

    river.requireValue("need", "car_rental_offer");  // listen for this offer
    river.register(new DiscountSolution());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String jsonMessage = solutionPacket().toJson();
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
//    System.out.println(String.format(" [x] %s", errors));
  }

  private static Packet solutionPacket() {
    HashMap<String, Object> jsonMap = new HashMap<>();
    jsonMap.put("solution", "car from discount");
    jsonMap.put("value", "1");
    return new Packet(jsonMap);
  }
}