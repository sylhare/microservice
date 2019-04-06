package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;
import com.sylhare.microservices.Message;

import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

public class DiscountSolution implements River.PacketListener {

  private static String SOLUTION_ID = UUID.randomUUID().toString();
  private int baseRevenue = new Random().nextInt();
  private Double baseLikelyhhod = new Random().nextDouble();

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("discount_solution", host, port);
    final River river = new River(rapidsConnection);

    river.requireValue("need", "car_rental_offer");  // listen for this offer
    river.forbid("solution");  // listen for this offer
    river.register(new DiscountSolution());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String jsonMessage = Message.enrichWithSolution(packet, baseLikelyhhod,abs(baseRevenue) % 30, SOLUTION_ID);
    System.out.println(String.format(" [<] %s", jsonMessage));
    connection.publish(jsonMessage);
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }

}