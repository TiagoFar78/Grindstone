package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.game.ForcestartResult;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MapFactory;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameSettings;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ForceStartCommand implements CommandExecutor {

    public static final String COMMAND_LABEL = "forcestart";

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MapFactory> availableMaps;

    public ForceStartCommand(MinigameFactory factory, MinigameSettings settings, MapFactory map) {
        this(factory, settings, singleMapList(map));
    }

    public ForceStartCommand(MinigameFactory factory, MinigameSettings settings, List<MapFactory> maps) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = maps;
    }

    private static List<MapFactory> singleMapList(MapFactory map) {
        List<MapFactory> maps = new ArrayList<>();
        maps.add(map);
        return maps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Locale locale = sender instanceof Player ? ((Player) sender).locale() : Locale.ENGLISH;
        if (!sender.hasPermission(Grindstone.ADMIN_PERMISSION)) {
            BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestart.not_allowed");
            return true;
        }

        List<Collection<String>> teams = new ArrayList<>();
        for (String team : args) {
            List<String> members = new ArrayList<>();
            for (String member : team.split(",")) {
                if (BukkitPlayer.isOnline(member)) {
                    BukkitPlayer.sendMessage(sender, locale, "grindstone.forcestart.player_not_online", member);
                    return true;
                }

                members.add(member);
            }
            teams.add(members);
        }

        int mapIndex = new Random().nextInt(availableMaps.size());
        ForcestartResult result = GamesManager.forceStart(factory, settings, availableMaps.get(mapIndex), teams);
        BukkitPlayer.sendMessage(sender, locale, result.getMessageKey());

        return true;
    }

}
