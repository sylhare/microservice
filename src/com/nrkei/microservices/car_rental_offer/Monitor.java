package com.nrkei.microservices.car_rental_offer;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.Packet;
import com.nrkei.microservices.rapids_rivers.PacketProblems;
import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.nrkei.microservices.rapids_rivers.River;
import com.nrkei.microservices.rapids_rivers.rabbit_mq.RabbitMqRapids;

// Understands the messages on an event bus
public class Monitor implements River.PacketListener {

    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];

        final RapidsConnection rapidsConnection = new RabbitMqRapids("monitor_in_java", host, port);
        final River river = new River(rapidsConnection);
        // See RiverTest for various functions River supports to aid in filtering, like:
        //river.requireValue("key", "value");  // Reject packet unless it has key:value pair
        //river.require("key1", "key2");       // Reject packet unless it has key1 and key2
        //river.forbid("key1", "key2");        // Reject packet if it does have key1 or key2
        //river.interestedIn("key1", "key2");  // Allows key1 and key2 to be queried and set in a packet
        river.register(new Monitor());         // Hook up to the river to start receiving traffic
    }

    @Override
    public void packet(RapidsConnection connection, Packet packet, PacketProblems warnings) {
        System.out.println(String.format(" [*] %s", warnings));
    }

    @Override
    public void onError(RapidsConnection connection, PacketProblems errors) {
        System.out.println(String.format(" [x] %s", errors));
    }
}
