package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.bukkit.BukkitPlayer;
import io.github.tiagofar78.grindstone.queue.DequeueResult;
import io.github.tiagofar78.grindstone.queue.QueuesManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveQueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are allowed to join matchmaking queues");
            return true;
        }

        DequeueResult result = QueuesManager.dequeue(sender.getName());
        BukkitPlayer.sendMessage(sender, ((Player) sender).locale(), result.getMessageKey());

        return true;
    }

}
