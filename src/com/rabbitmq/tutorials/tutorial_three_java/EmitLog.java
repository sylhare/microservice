package com.rabbitmq.tutorials.tutorial_three_java;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.util.HashMap;

public class EmitLog {

    private static final String EXCHANGE_NAME = "rapids";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.79");    // Set IP of RabbitMQ instance (localhost default)
        factory.setPort(5673);              // Set port of RabbitMQ instance (5672 default)
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout", false, true, new HashMap<String, Object>());

        String message = getMessage(argv);

        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }

    private static String getMessage(String[] strings){
        if (strings.length < 1) return "info: Hello World!";
        return joinStrings(strings, " ");
    }

    private static String joinStrings(String[] strings, String delimiter) {
        int length = strings.length;
        if (length == 0) return "";
        StringBuilder words = new StringBuilder(strings[0]);
        for (int i = 1; i < length; i++) {
            words.append(delimiter).append(strings[i]);
        }
        return words.toString();
    }
}
