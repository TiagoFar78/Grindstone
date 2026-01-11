package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.coordinator.Coordinator;
import io.github.tiagofar78.grindstone.party.Party;
import io.github.tiagofar78.grindstone.party.PartyService;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveMatchmakingQueue implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are allowed to join matchmaking queues");
            return true;
        }

        String playerName = sender.getName();
        Party party = PartyService.getParty(playerName);
        if (!party.isLeader(playerName)) {
            // TODO send not leader message
            return true;
        }

        if (!Coordinator.isInQueue(party)) {
            // TODO send not in queue message
            return true;
        }

        Coordinator.dequeue(party);
        // TODO send message to party

        return true;
    }

}
