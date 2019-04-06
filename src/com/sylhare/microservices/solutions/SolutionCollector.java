package com.sylhare.microservices.solutions;

import com.google.gson.internal.StringMap;
import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.abs;

public class SolutionCollector implements River.PacketListener {

  private static Map<UUID, Double> solutionsMap = new HashMap<>();

  public static void main(String[] args) throws InterruptedException {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("solution_collector", host, port);
    final River river = new River(rapidsConnection);
    river.require("solution");
    river.interestedIn("solution");
    river.forbid("best_solution");
    river.register(new SolutionCollector());
    cleanUp();
  }

  /**
   * Defining the best solution
   *
   * 1. high likelyhood, high revenue
   * 2. equal revenue, higher likelyhood
   * 3. equal likelyhood, higher revenue
   * 4. high likelyhood, low revenue
   * 5. low likelyhood, high revenue
   */
  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    StringMap receivedSolution = (StringMap) packet.get("solution");
    UUID uuid = (UUID) packet.get("transaction_id");
    Double additionalRevenue = (Double) receivedSolution.get("additional_revenue");
    Double likelyhood = (Double) receivedSolution.get("likelyhood");

    Double factor = additionalRevenue * likelyhood;

    if (!solutionsMap.containsKey(uuid) || solutionsMap.get(uuid) < factor) {
      solutionsMap.put(uuid, factor);
      packet.put("best_solution", receivedSolution);
      connection.publish(packet.toJson());
    }
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    // nothing
  }

  private static void cleanUp() throws InterruptedException {
    while(true) {
      Thread.sleep(10000);
      solutionsMap = new HashMap<>();
    }
  }
}