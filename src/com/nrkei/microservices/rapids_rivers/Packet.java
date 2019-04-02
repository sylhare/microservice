package com.nrkei.microservices.rapids_rivers;
/*
 * Copyright (c) 2016 by Fred George
 * May be used freely except for training; license required for training.
 * @author Fred George
 */

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.*;

// Understands a specific JSON-formatted message
public class Packet {

    final static String READ_COUNT = "system_read_count";

    private Map<String, Object> jsonHash;
    private final Map<String, Object> recognizedKeys = new HashMap<>();
    private final PacketProblems problems;

    public Packet(Map<String, Object> jsonHash) {
        problems = null;  // TODO: Placeholder until this constructor is removed
        this.jsonHash = jsonHash;
        if (!jsonHash.containsKey(READ_COUNT)) jsonHash.put(READ_COUNT, -1.0);
        jsonHash.put(READ_COUNT, ((Double)jsonHash.get(READ_COUNT)).intValue() + 1);
    }

    Packet(String message, PacketProblems problems) {
        this.problems = problems;
        Gson jsonEngine = new Gson();
        try {
            jsonHash = jsonEngine.fromJson(message, HashMap.class);
            if (!jsonHash.containsKey(READ_COUNT)) jsonHash.put(READ_COUNT, -1.0);
            jsonHash.put(READ_COUNT, ((Double)jsonHash.get(READ_COUNT)).intValue() + 1);
        }
        catch(JsonSyntaxException e) {
            problems.severeError("Invalid JSON format per Gson library");
        }
        catch(Exception e) {
            problems.severeError("Unknown failure. Message is: " + e.toString());
        }
    }

    void addAccessor(String requiredJsonKey) {
        if (!recognizedKeys.containsKey(requiredJsonKey))
            recognizedKeys.put(requiredJsonKey, jsonHash.get(requiredJsonKey));
    }

    void require(String... requiredJsonKeys) {
        for (String key : requiredJsonKeys) {
            if (hasKey(key) && !isKeyEmpty(key)) { addAccessor(key); continue; }
            problems.error("Missing required key '" + key + "'");
        }
    }

    void forbid(String... forbiddenJsonKeys) {
        for (String key : forbiddenJsonKeys) {
            if (isKeyMissing(key)) { addAccessor(key); continue; }
            problems.error("Forbidden key '" + key + "' already defined");
        }
    }

    void interestedIn(String... interestingJsonKeys) {
        for (String key : interestingJsonKeys) addAccessor(key);
    }

    void requireValue(String requiredKey, Object requiredValue) {
        if (isKeyMissing(requiredKey) || !jsonHash.get(requiredKey).equals(requiredValue)) {
            problems.error("Required key '"
                    + requiredKey
                    + "' does not have required value '"
                    + requiredValue
                    + "'");
            return;
        }
        addAccessor(requiredKey);
    }

    public Object get(String key) {
        return recognizedKeys.get(key);
    }

    public void put(String key, Object value) {
        if (!recognizedKeys.containsKey(key))
            throw new IllegalArgumentException(
                    "Manipulated keys must be declared as required, forbidden, or interesting");
        recognizedKeys.put(key, value);
    }

    public String toJson() {
        Map<String, Object> updatedHash = new HashMap<>(jsonHash);
        for (String key : recognizedKeys.keySet())
            updatedHash.put(key, recognizedKeys.get(key));
        return new Gson().toJson(updatedHash);
    }

    public List getList(String solutionsKey) {
        return (List)get(solutionsKey);
    }

    private boolean hasKey(String key) {
        // TODO: May need expansion for deeper keys...
        return jsonHash.containsKey(key);
    }

    private boolean isKeyMissing(String forbiddenJsonKey) {
        // TODO: May need expansion for deeper keys...
        return !hasKey(forbiddenJsonKey) || isKeyEmpty(forbiddenJsonKey);
    }

    private boolean isKeyEmpty(String jsonKey) {
        Object value = jsonHash.get(jsonKey);
        if (value instanceof String && ((String)value).isEmpty()) return true;
        if (!(value instanceof Collection)) return false;
        return ((Collection)value).isEmpty();
    }
}

