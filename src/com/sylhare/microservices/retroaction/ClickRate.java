package com.sylhare.microservices.retroaction;

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;
import java.util.Map;

public class ClickRate implements River.PacketListener {

  private Map<String, Double> clickedSolutions = new HashMap<>();
  private int numberOfClicks = 0;

  public static void main(String[] args) {
    String host = args[0];
    String port = args[1];

    final RapidsConnection rapidsConnection = new RabbitMqRapids("click_rate", host, port);
    final River river = new River(rapidsConnection);

    river.require("clicked");
    river.forbid("solution");
    river.forbid("user_reaction");
    river.register(new ClickRate());
  }

  @Override
  public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
    String solutionID = (String) packet.get("clicked");
    numberOfClicks++;

    if (!clickedSolutions.containsKey(solutionID)) {
      clickedSolutions.put(solutionID, 1.0);
    } else {
      clickedSolutions.put(solutionID, clickedSolutions.get(solutionID) + 1);
    }

    Map<String, Object> clickrate = new HashMap<>();
    HashMap<String, Object> attributes = new HashMap<>();
    attributes.put("ratio", clickedSolutions.get(solutionID) / numberOfClicks);
    attributes.put("solution_id", solutionID);
    clickrate.put("click_rate", attributes);
    connection.publish(new Packet(clickrate).toJson());
  }

  @Override
  public void onError(RapidsConnection connection, PacketProblems errors) {
    //System.out.println(String.format(" [x] %s", errors));
  }
}


