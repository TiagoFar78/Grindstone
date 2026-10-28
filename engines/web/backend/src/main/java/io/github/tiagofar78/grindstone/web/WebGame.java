package io.github.tiagofar78.grindstone.web;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.playground.Playground;

public interface WebGame {

    String getName();

    boolean executeCommand(String cmdLabel, Playground playground, WebPlayer player, JsonNode node);

    void disconnect(WebPlayer player);

}
