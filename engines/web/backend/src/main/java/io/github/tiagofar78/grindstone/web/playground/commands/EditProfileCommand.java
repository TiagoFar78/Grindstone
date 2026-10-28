package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.concurrent.ThreadLocalRandom;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.network.messages.Outbound;
import io.github.tiagofar78.grindstone.web.playground.LoginService;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class EditProfileCommand implements PlaygroundCommand<Inbound.EditProfile> {

    private static final int MAX_NAME_LENGTH = 16;

    @Override
    public Class<Inbound.EditProfile> inputType() {
        return Inbound.EditProfile.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer player, Inbound.EditProfile data) {
        if (player.getState() != PlayerState.IN_LOBBY) {
            player.sendMessage(new Outbound.Error("Cannot edit profile while in a game."));
            return true;
        }

        String name = data.name();
        if (name == null || name.isEmpty() || name.length() > MAX_NAME_LENGTH) {
            player.sendMessage(new Outbound.Error("Nickname must have between 1 and " + MAX_NAME_LENGTH + " characters."));
            return true;
        }

        if (playground.isNameTaken(player, name)) {
            player.sendMessage(new Outbound.Error("That nickname is already in use."));
            return true;
        }

        String rawEmoji = data.emoji();
        String emoji = rawEmoji != null && LoginService.AVAILABLE_EMOJIS.contains(rawEmoji)
                ? rawEmoji
                : LoginService.AVAILABLE_EMOJIS.get(
                        ThreadLocalRandom.current().nextInt(LoginService.AVAILABLE_EMOJIS.size())
                );

        player.setName(name);
        player.setEmoji(emoji);

        playground.refreshMenus();
        return true;
    }

}
