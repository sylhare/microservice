package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Click implements River.PacketListener {

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("reaction_tracker", host, port);
    final River river = new River(rapidsConnection);

    river.require("solution_id");
    river.register(new Click());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    if (new Random().nextBoolean() && new Random().nextBoolean()) {
      Map<String, Object> clicked = new HashMap<>();
      clicked.put("clicked", packet.get("solution_id"));
      clicked.put("user_id", packet.get("user_id"));
      connection.publish(new Packet(clicked).toJson());
    }
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }
}


