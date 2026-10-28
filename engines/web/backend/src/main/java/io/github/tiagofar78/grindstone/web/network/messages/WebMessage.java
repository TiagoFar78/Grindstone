package io.github.tiagofar78.grindstone.web.network.messages;

public sealed interface WebMessage permits Inbound, Outbound {}
