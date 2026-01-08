package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.coordinator.Coordinator;
import io.github.tiagofar78.grindstone.coordinator.MatchmakingQueue;
import io.github.tiagofar78.grindstone.game.MinigameMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinMatchmakingQueue implements CommandExecutor {

    private MatchmakingQueue queue;
    private MinigameMap map;

    public JoinMatchmakingQueue(MatchmakingQueue queue) {
        this(queue, null);
    }

    public JoinMatchmakingQueue(MatchmakingQueue queue, MinigameMap map) {
        this.queue = queue;
        this.map = map;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are allowed to join matchmaking queues");
            return true;
        }

        List<String> party = getParty((Player) sender);
        // TODO check if is party leader

        if (Coordinator.isInQueue(party.toString())) { // TODO
            // TODO Send already in a queue message
            return true;
        }

        Coordinator.enqueue(party.toString(), queue, map); // TODO

        return true;
    }

    private List<String> getParty(Player player) { // TODO
        List<String> party = new ArrayList<>();
        party.add(player.getName());
        return party;
    }

}
