package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

public class Membership implements River.PacketListener {


    public static void main(String[] args) {
      String host = args[0];
      String port = args[1];

      final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
      final River river = new River(rapidsConnection);

      river.requireValue("need", "car_rental_offer");
      river.require("user_id");
      river.forbid("solution", "member_tier");

      river.register(new com.nrkei.microservices.car_rental_offer.Monitor());
    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
      memberPacket(packet);
      connection.publish(packet.toJson());

    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
      //System.out.println(String.format(" [x] %s", errors));
  }

  private Packet memberPacket(Packet packet) {
    Double userId = (Double)packet.get("user_id");
    String tier = "standard";

    if(userId % 2 == 0){
      tier = "silver";
    }
    else if(userId % 3 == 0){
      tier = "gold";
    }
    else if(userId % 5 == 0){
      tier = "platinum";
    }

    packet.put("member_tier", tier);
    return packet;
  }

}
