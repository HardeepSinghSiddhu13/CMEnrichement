package com.samsung.nmt.cmenrichment.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonParser {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static ObjectNode fromJsonStr(String jsonString) {

        try {
            return mapper.readValue(jsonString, ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing json", e);
        }
    }

    public static ObjectNode createObjectNode() {
        return mapper.createObjectNode();
    }

    public static ObjectNode fromMap(Map<String, String> map) {
        return mapper.convertValue(map, ObjectNode.class);
    }

    public static String toJsonString(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String fromJsonNode(ObjectNode jsonNode) {
        try {
            return mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] fromJsonNodeFRomByte(ObjectNode jsonNode) {
        try {
            return mapper.writeValueAsBytes(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
