package com.nrkei.microservices.car_rental_offer;
/* 
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.*;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

import java.util.HashMap;

// Understands the requirement for advertising on a site
public class Need {

    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];

        final RapidsConnection rapidsConnection = new RabbitMqRapids("car_rental_need_java", host, port);
        publish(rapidsConnection);
    }

    public static void publish(RapidsConnection rapidsConnection) {
        try {
            while (true) {
                String jsonMessage = needPacket().toJson();
                System.out.println(String.format(" [<] %s", jsonMessage));
                rapidsConnection.publish(jsonMessage);
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not publish message:", e);
        }
    }

    private static Packet needPacket() {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("need", "car_rental_offer");
        return new Packet(jsonMap);
    }
}
