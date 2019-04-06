package com.sylhare.microservices;

import com.nrkei.microservices.rapids_rivers.Packet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {

  public static String enrichWithSolution(Packet packet, Double likelyhood, int revenue, String solutionID) {
    Map<String, Object> solution = new HashMap<>();
    solution.put("additional_revenue", revenue);
    solution.put("likelyhood", likelyhood);
    solution.put("title", "Discount car");
    solution.put("solution_id", solutionID);
    packet.put("solution", solution);

    return packet.toJson();
  }

}
