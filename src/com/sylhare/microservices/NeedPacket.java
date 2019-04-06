package com.sylhare.microservices;

import com.nrkei.microservices.rapids_rivers.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.abs;

public class NeedPacket {

  private NeedPacket() {}

  public static String enrichWithSolution(Packet needPacket, Double likelyhood, int revenue, String solutionID) {
    Map<String, Object> solution = new HashMap<>();
    solution.put("additional_revenue", revenue);
    solution.put("likelihood", likelyhood);
    solution.put("title", "Discount car");
    solution.put("solution_id", solutionID);
    needPacket.put("solution", solution);

    return needPacket.toJson();
  }

  public static String message() {
    HashMap<String, Object> needMessage = new HashMap<>();

    needMessage.put("need", "car_rental_offer");
    needMessage.put("transaction_id", UUID.randomUUID().toString());

    randomizeMembership(needMessage);
    return new Packet(needMessage).toJson();
  }

  private static void randomizeMembership(final HashMap<String, Object> needMessage) {
    if (new Random().nextBoolean()) needMessage.put("user_id", abs(new Random().nextInt() / 10000));
  }

}
