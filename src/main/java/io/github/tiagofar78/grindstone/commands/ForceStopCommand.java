package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.Minigame;
import io.github.tiagofar78.grindstone.game.MinigamePlayer;
import io.github.tiagofar78.grindstone.game.MinigameTeam;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ForceStopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            // TODO Send no perm message
            return true;
        }

        String name = sender.getName();
        if (GamesManager.isInGame(name)) {
            GamesManager.getGame(name).forceStop();
            // TODO Send forcestop success message
            return true;
        }

        if (args.length == 0) {
            Set<Minigame> games = GamesManager.getUniqueMinigames();
            if (games.size() == 0) {
                // TODO Send no games running at this moment message
                return true;
            }

            // TODO Send choose the name of a player in a game to cancel it message
            sender.sendMessage(getGamesMessage(games));

            return true;
        }

        String targetName = args[0];
        if (GamesManager.isInGame(targetName)) {
            GamesManager.getGame(targetName).forceStop();
            // TODO Send forcestop success message
            return true;
        }

        // TODO Send targetName not registered in game message
        return true;
    }

    private String[] getGamesMessage(Set<Minigame> games) {
        List<String> message = new ArrayList<>();

        String messageSeparator = "=".repeat(15);
        String gameSeparator = "-".repeat(15);

        message.add("");
        message.add(messageSeparator);

        for (Minigame game : games) {
            for (MinigameTeam<MinigamePlayer> team : game.getTeams()) {
                String teamLine = "- " + team.getColor() + team.getName() + ": ";
                for (MinigamePlayer player : team.getMembers()) {
                    teamLine += player + ", ";
                }
                teamLine = teamLine.substring(0, teamLine.length() - 2);

                message.add(teamLine);
            }

            message.add(gameSeparator);
        }

        message.remove(message.size() - 1);
        message.add(messageSeparator);
        message.add("");

        return message.toArray(new String[0]);
    }

}
