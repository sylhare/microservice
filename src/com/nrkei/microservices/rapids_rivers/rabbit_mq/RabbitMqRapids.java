package com.nrkei.microservices.rapids_rivers.rabbit_mq;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.nrkei.microservices.rapids_rivers.RapidsConnection;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

// Understands an event bus implemented with RabbitMQ in pub/sub mode (fanout)
public class RabbitMqRapids extends RapidsConnection implements AutoCloseable {

    // See RabbitMQ pub/sub documentation: https://www.rabbitmq.com/tutorials/tutorial-three-python.html
    private static final String RABBIT_MQ_PUB_SUB = "fanout";
    private static final String EXCHANGE_NAME = "rapids";

    private final ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final String queueName;

    public RabbitMqRapids(String serviceName, String host, String port) {
        queueName = serviceName + "_" + UUID.randomUUID().toString();
        factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(Integer.parseInt(port));
    }

    @Override
    public void register(MessageListener listener) {
        if (channel == null) connect();
        if (listeners.isEmpty()) {
            configureQueue();
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            consumeMessages(consumer(channel));
        }
        super.register(listener);
    }

    @Override
    public void publish(String message) {
        if (channel == null) connect();
        try {
            channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException("UnsupportedEncodingException on message extraction", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException when sending a message", e);
        }
    }

    private void connect() {
        establishConnectivity();
        declareExchange();
    }

    private void establishConnectivity() {
        connection = connection();
        channel = channel();
    }

    private Connection connection() {
        try {
            return factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException on creating Connection", e);
        }
    }

    private Channel channel() {
        try {
            return connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException in creating Channel", e);
        }
    }

    private void declareExchange() {
        try {
            // Configure for durable, auto-delete
            channel.exchangeDeclare(EXCHANGE_NAME, RABBIT_MQ_PUB_SUB, true, true, new HashMap<String, Object>());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException declaring Exchange", e);
        }
    }

    private void configureQueue() {
        declareQueue();
        bindQueueToExchange();
    }

    private AMQP.Queue.DeclareOk declareQueue() {
        try {
            // Configured for non-durable, auto-delete, and exclusive
            return channel.queueDeclare(queueName, false, true, true, new HashMap<String, Object>());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException declaring Queue", e);
        }
    }

    private void bindQueueToExchange() {
        try {
            channel.queueBind(queueName, EXCHANGE_NAME, "");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException binding Queue to Exchange", e);
        }
    }

    private String consumeMessages(Consumer consumer) {
        try {
            return channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException while consuming messages", e);
        }
    }

    private DefaultConsumer consumer(final Channel channel) {
        final RapidsConnection sendPort = this;
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                final String message = new String(body, "UTF-8");
//                System.out.println(" [x] Received '" + message + "'");
                for (MessageListener listener : listeners) listener.message(sendPort, message);
            }
        };
    }

    public void close() {
        try {
            if (channel != null) channel.close();
            if (connection != null) connection.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("IOException on close", e);
        }
    }

}
