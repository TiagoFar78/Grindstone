package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.coordinator.Coordinator;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LeaveMatchmakingQueue implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are allowed to join matchmaking queues");
            return true;
        }

        List<String> party = getParty((Player) sender);
        // TODO check if is party leader

        if (!Coordinator.isInQueue(party.toString())) { // TODO
            // TODO send not in queue message
            return true;
        }

        Coordinator.dequeue(party.toString()); // TODO
        // TODO send message to party

        return true;
    }

    private List<String> getParty(Player player) { // TODO
        List<String> party = new ArrayList<>();
        party.add(player.getName());
        return party;
    }

}
