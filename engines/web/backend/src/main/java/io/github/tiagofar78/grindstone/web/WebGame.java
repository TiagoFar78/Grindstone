package io.github.tiagofar78.grindstone.web;

import com.fasterxml.jackson.databind.JsonNode;

public interface WebGame {

    String getName();

    boolean executeCommand(String cmdLabel, WebPlayer player, JsonNode node);

    void disconnect(WebPlayer player);

}
