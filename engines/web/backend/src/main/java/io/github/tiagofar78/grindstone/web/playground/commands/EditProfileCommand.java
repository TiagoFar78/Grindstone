package io.github.tiagofar78.grindstone.web.playground.commands;

import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.network.messages.Inbound;
import io.github.tiagofar78.grindstone.web.playground.PlayerEmoji;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class EditProfileCommand implements PlaygroundCommand<Inbound.EditProfile> {

    @Override
    public Class<Inbound.EditProfile> inputType() {
        return Inbound.EditProfile.class;
    }

    @Override
    public boolean execute(Playground playground, WebPlayer player, Inbound.EditProfile data) {
        String emoji = data.emoji();
        if (!PlayerEmoji.emojiExists(emoji)) {
            player.setEmoji(PlayerEmoji.random().code());
        }

        player.setEmoji(emoji);

        playground.refreshMenus();
        return true;
    }

}
