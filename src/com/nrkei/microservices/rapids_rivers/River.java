package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import java.util.ArrayList;
import java.util.List;

// Understands a stream of valid JSON packets meeting certain criteria
// Implements GOF Observer pattern to trigger listeners with packets and/or problems
// Implements GOF Command pattern for validations
public class River implements RapidsConnection.MessageListener {

    private final RapidsConnection rapidsConnection;
    private final List<PacketListener> listeners = new ArrayList<>();
    private final List<Validation> validations = new ArrayList<>();

    public River(RapidsConnection rapidsConnection) {
        this.rapidsConnection = rapidsConnection;
        rapidsConnection.register(this);
    }

    public void register(PacketListener listener) {
        listeners.add(listener);
    }

    @Override
    public void message(RapidsConnection sendPort, String message) {
        PacketProblems problems = new PacketProblems(message);
        Packet packet = new Packet(message, problems);
        for (Validation v : validations) v.validate(packet);
        if (problems.hasErrors())
            onError(sendPort, problems);
        else
            packet(sendPort, packet, problems);
    }

    private void packet(RapidsConnection sendPort, Packet packet, PacketProblems warnings) {
        for (PacketListener l : listeners) l.packet(sendPort, packet, warnings);
    }

    private void onError(RapidsConnection sendPort, PacketProblems errors) {
        for (PacketListener l : listeners) l.onError(sendPort, errors);
    }

    public River require(String... jsonKeys) {
        validations.add(new RequiredKeys(jsonKeys));
        return this;
    }

    public River forbid(String... jsonKeys) {
        validations.add(new ForbiddenKeys(jsonKeys));
        return this;
    }

    public River interestedIn(String... jsonKeys) {
        validations.add(new InterestingKeys(jsonKeys));
        return this;
    }

    public River requireValue(String jsonKey, String expectedValue) {
        validations.add(new RequiredValue(jsonKey, expectedValue));
        return this;
    }

    public interface PacketListener {
        void packet(RapidsConnection connection, Packet packet, PacketProblems warnings);
        void onError(RapidsConnection connection, PacketProblems errors);
    }

    private interface Validation {
        void validate(Packet packet);
    }

    private class RequiredKeys implements Validation {
        private final String[] requiredKeys;
        RequiredKeys(String... requiredKeys) {
            this.requiredKeys = requiredKeys;
        }
        @Override public void validate(Packet packet) {
            packet.require(requiredKeys);
        }
    }

    private class ForbiddenKeys implements Validation {
        private final String[] forbiddenKeys;
        ForbiddenKeys(String... forbiddenKeys) { this.forbiddenKeys = forbiddenKeys;}
        @Override public void validate(Packet packet) { packet.forbid(forbiddenKeys); }
    }

    private class InterestingKeys implements Validation {
        private final String[] forbiddenKeys;
        InterestingKeys(String... forbiddenKeys) { this.forbiddenKeys = forbiddenKeys;}
        @Override public void validate(Packet packet) { packet.interestedIn(forbiddenKeys); }
    }

    private class RequiredValue implements Validation {
        private final String requiredKey;
        private final String requiredValue;
        RequiredValue(String requiredKey, String requiredValue) {
            this.requiredKey = requiredKey;
            this.requiredValue = requiredValue;
        }
        @Override public void validate(Packet packet) { packet.requireValue(requiredKey, requiredValue); }
    }

}
