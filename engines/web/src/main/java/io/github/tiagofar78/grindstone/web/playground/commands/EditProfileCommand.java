package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.LoginService;
import io.github.tiagofar78.grindstone.web.network.OutboundMessages;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class EditProfileCommand implements PlaygroundCommand {

    private static final int MAX_NAME_LENGTH = 16;

    @Override
    public void execute(Playground playground, WebPlayer player, JsonNode message) {
        if (player.getState() != PlayerState.IN_LOBBY) {
            player.sendMessage(OutboundMessages.buildErrorToast("Cannot edit profile while in a game."));
            return;
        }

        String name = message.path("name").asText("").trim();
        if (name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            player.sendMessage(
                    OutboundMessages.buildLoginPage(
                            "Nickname must have between 1 and " + MAX_NAME_LENGTH + " characters."
                    )
            );
            return;
        }

        if (playground.isNameTaken(player, name)) {
            player.sendMessage(OutboundMessages.buildLoginPage("That nickname is already in use."));
            return;
        }

        String rawEmoji = message.path("emoji").asText("");
        String emoji = LoginService.AVAILABLE_EMOJIS.contains(rawEmoji)
                ? rawEmoji
                : LoginService.AVAILABLE_EMOJIS.get(
                        ThreadLocalRandom.current().nextInt(LoginService.AVAILABLE_EMOJIS.size())
                );

        player.setName(name);
        player.setEmoji(emoji);

        playground.refreshMenus();
    }

}
