package com.nrkei.microservices.car_rental_offer;

import com.google.gson.internal.StringMap;
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

public class SolutionCollector implements River.PacketListener {

  Map<UUID, Double> solutionsMap = new HashMap<>();

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
    final River river = new River(rapidsConnection);
    river.require("solution");       // Reject packet unless it has key1 and key2
    river.interestedIn("solution");       // Reject packet unless it has key1 and key2
    river.forbid("best_solution");       // Reject packet unless it has key1 and key2
    river.register(new SolutionCollector());         // Hook up to the river to start receiving traffic
  }

  private static Packet packet(Packet packet) {
    Map<String, Object> solution = new HashMap<>();
    Random rand = new Random();
    rand.setSeed(System.currentTimeMillis());
    solution.put("additional_revenue", abs(rand.nextInt()) % 30);
    solution.put("likelyhood", rand.nextDouble());
    solution.put("title", "a discount car");
    packet.put("solution", solution);

    return packet;
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    StringMap solution = (StringMap) packet.get("solution");
    UUID uuid = (UUID) packet.get("transaction_id");
    Double additionalRevenue = (Double) solution.get("additional_revenue");
    Double likelyhood = (Double) solution.get("likelyhood");

    Double factor = additionalRevenue * likelyhood;
        /*  1. high likelyhood, high revenue
            2. equal revenue, higher likelyhood
            3. equal likelyhood, higher revenue
            4. high likelyhood, low revenue
            5. low likelyhood, high revenue
         */
    if (!solutionsMap.containsKey(uuid) || solutionsMap.get(uuid) < factor) {
      solutionsMap.put(uuid, factor);
      packet.put("best_solution", solution);
      connection.publish(packet.toJson());
    }

  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    // nothing
  }

  private Map<String, Object> selectBestSolution(Map<String, Object> challenger, Map<String, Object> champion) {
    return (double) champion.get("likelyhood") > (double) challenger.get("likelyhood") ? champion : challenger;
  }
}
//
//  List<Map<String, Object>> possibleSolutions = new ArrayList<>();
//  Map<String, Object> newSolution = (Map<String, Object>) packet;
//
//    solutions.add(newSolution);
//        //solutions.stream().filter(s -> s.get("transaction_id") == solution.get("transaction_id"));
//
//        for (Map<String, Object> solution : solutions) {
//    if (solution.get("transaction_id") == newSolution.get("transaction_id")) {
//    possibleSolutions.add(solution);
//    }
//    }
//
//    possibleSolutions.stream()
//    .map(s -> (Map<String, Object>) s.get("solution"))
//    .reduce( (champion, challenger) ->  selectBestSolution(challenger, champion));