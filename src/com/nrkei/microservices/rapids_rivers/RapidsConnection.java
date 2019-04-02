package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import java.util.ArrayList;
import java.util.List;

// Understands accessing a stream of messages
public abstract class RapidsConnection {

    protected final List<MessageListener> listeners = new ArrayList<>();

    public void register(MessageListener listener) {
        listeners.add(listener);
    }

    public abstract void publish(String message);

    public interface MessageListener {
        void message(RapidsConnection sendPort, String message);
    }
}
