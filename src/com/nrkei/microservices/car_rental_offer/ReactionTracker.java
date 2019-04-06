package com.nrkei.microservices.car_rental_offer;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;

public class ReactionTracker implements River.PacketListener {

  private Map<String, Map<String, Double>> users = new HashMap<>();

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("reaction_tracker", host, port);
    final River river = new River(rapidsConnection);

    river.require("clicked");
    river.interestedIn("user_id");
    river.forbid("solution");
    river.forbid("user_reaction");
    river.register(new ReactionTracker());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String userId = (String) packet.get("user_id");
    String solutionID = (String) packet.get("clicked");

    if (!users.containsKey(userId)) {

      if (!users.get(userId).containsKey(solutionID)) {
        users.get(userId).put(solutionID, 1.0);
      } else {
        users.get(userId).put(solutionID, users.get(userId).get(solutionID) + 1);
      }
    }

    Map<String, Object> reactionTracker = new HashMap<>();
    HashMap<String, Object> attributes = new HashMap<>();
    attributes.put("user_likelyhood", users.get(userId).get(solutionID) / users.get(userId).values().stream().reduce(0.0, Double::sum));
    attributes.put("solution_id", solutionID);
    reactionTracker.put("user_tracker", attributes);
    connection.publish(new Packet(reactionTracker).toJson());
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }
}


