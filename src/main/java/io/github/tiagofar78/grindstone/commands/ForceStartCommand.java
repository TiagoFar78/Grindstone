package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.game.ForcestartResult;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ForceStartCommand implements CommandExecutor {

    public static final String COMMAND_LABEL = "forcestart";

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MinigameMap> availableMaps;

    public ForceStartCommand(MinigameFactory factory, MinigameSettings settings, MinigameMap map) {
        this(factory, settings, singleMapList(map));
    }

    public ForceStartCommand(MinigameFactory factory, MinigameSettings settings, List<MinigameMap> maps) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = maps;
    }

    private static List<MinigameMap> singleMapList(MinigameMap map) {
        List<MinigameMap> maps = new ArrayList<>();
        maps.add(map);
        return maps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            // TODO send no permission message
            return true;
        }

        List<Collection<String>> teams = new ArrayList<>();
        for (String team : args) {
            List<String> members = new ArrayList<>();
            for (String member : team.split(",")) {
                if (BukkitPlayer.isOnline(member)) {
                    // TODO Send player not online message
                    return true;
                }

                members.add(member);
            }
            teams.add(members);
        }

        int mapIndex = new Random().nextInt(availableMaps.size());
        ForcestartResult result = GamesManager.forceStart(factory, settings, availableMaps.get(mapIndex), teams);
        // TODO Send message according to result

        return true;
    }

}
