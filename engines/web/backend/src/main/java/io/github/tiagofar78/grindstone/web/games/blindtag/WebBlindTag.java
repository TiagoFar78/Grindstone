package io.github.tiagofar78.grindstone.web.games.blindtag;

import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.Team;
import io.github.tiagofar78.grindstone.games.blindtag.BTPlayer;
import io.github.tiagofar78.grindstone.games.blindtag.BlindTag;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.player.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.Playground;
import io.github.tiagofar78.grindstone.web.playground.commands.Commands;
import io.github.tiagofar78.grindstone.web.playground.commands.GameCommand;
import io.github.tiagofar78.grindstone.web.services.WebServices;

public class WebBlindTag extends BlindTag implements WebGame {

    public static final String NAME = "BlindTag";

    private final Commands<GameCommand<BlindTag, BTPlayer, ?>> commands = new Commands<>();

    public WebBlindTag(List<Team<BTPlayer>> teams, Random random) {
        GameDependencies dependencies = new GameDependencies(WebServices.scheduler);
        super(dependencies, new WebBTBridge(), teams);
        // TODO register commands
    }

    public WebBlindTag(List<Team<BTPlayer>> teams) {
        this(teams, new Random());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean executeCommand(String cmdLabel, Playground playground, WebPlayer player, JsonNode node) {
        return commands.execute(cmdLabel, playground, player, node);
    }

    @Override
    public void disconnect(WebPlayer player) {
        removePlayerFromGame((BTPlayer) player.getCorePlayer());
    }

}
