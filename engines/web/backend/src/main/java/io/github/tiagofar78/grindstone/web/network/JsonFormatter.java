package io.github.tiagofar78.grindstone.web.network;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.tiagofar78.grindstone.web.network.messages.WebMessage;

public class JsonFormatter {

    private static final ObjectMapper MAPPER = create();

    private static final ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    protected static JsonNode toJsonNode(String raw) {
        try {
            return MAPPER.readTree(raw);
        }
        catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String toJson(WebMessage message) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("type", typeOf(message));
        node.setAll((ObjectNode) MAPPER.valueToTree(message));
        return node.toString();
    }

    private static String typeOf(WebMessage message) {
        String name = message.getClass().getSimpleName();
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
    
}
