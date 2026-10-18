package io.github.tiagofar78.grindstone.web.playground.commands;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import io.github.tiagofar78.grindstone.web.PlayerState;
import io.github.tiagofar78.grindstone.web.WebGame;
import io.github.tiagofar78.grindstone.web.WebPlayer;
import io.github.tiagofar78.grindstone.web.playground.AvailableGame;
import io.github.tiagofar78.grindstone.web.playground.GameSession;
import io.github.tiagofar78.grindstone.web.playground.OutboundMessages;
import io.github.tiagofar78.grindstone.web.playground.Playground;

public class StartGameCommand implements PlaygroundCommand {

    @Override
    public void execute(Playground playground, WebPlayer sender, JsonNode message) {
        String gameName = message.path("game").asText("");
        AvailableGame type = playground.findAvailableGame(gameName);
        if (type == null) {
            sender.sendMessage(OutboundMessages.buildErrorToast("Unknown game '" + gameName + "'."));
            return;
        }

        List<List<WebPlayer>> teams = new ArrayList<>();
        Set<WebPlayer> seen = new LinkedHashSet<>();

        List<WebPlayer> registry = playground.getPlayerRegistry();

        for (JsonNode teamNode : message.path("teams")) {
            List<WebPlayer> team = new ArrayList<>();
            for (JsonNode idxNode : teamNode) {
                int idx = idxNode.isInt() ? idxNode.asInt() : -1;
                if (idx < 0 || idx >= registry.size()) {
                    sender.sendMessage(OutboundMessages.buildErrorToast("Invalid player."));
                    return;
                }

                WebPlayer p = registry.get(idx);
                if (p.getState() != PlayerState.IN_LOBBY) {
                    sender.sendMessage(OutboundMessages.buildErrorToast(p.getName() + " is already in a game."));
                    return;
                }

                if (!seen.add(p)) {
                    sender.sendMessage(OutboundMessages.buildErrorToast(p.getName() + " was listed more than once."));
                    return;
                }

                team.add(p);
            }
            if (team.isEmpty()) {
                sender.sendMessage(OutboundMessages.buildErrorToast("Empty team."));
                return;
            }
            teams.add(team);
        }

        if (teams.isEmpty()) {
            sender.sendMessage(OutboundMessages.buildErrorToast("Choose the players first."));
            return;
        }

        String teamsFormationProblem = type.teamsChecker().apply(teams);
        if (teamsFormationProblem != null) {
            sender.sendMessage(OutboundMessages.buildErrorToast(teamsFormationProblem));
            return;
        }

        WebGame newGame = type.factory().apply(teams);
        GameSession session = playground.createSession(newGame, teams);
        for (WebPlayer p : seen) {
            p.setState(PlayerState.IN_GAME);
            p.setCurrentGame(session);
            // p.setCorePlayer is done inside the type.factory().apply() just because it is easier
            // if I already had the core player available at this time it would be preferable to
            // do that here.
        }

        playground.refreshMenus();
    }

}
