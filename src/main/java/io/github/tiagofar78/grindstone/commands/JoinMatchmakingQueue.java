package io.github.tiagofar78.grindstone.commands;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.queue.EnqueueResult;
import io.github.tiagofar78.grindstone.queue.MatchmakingQueue;
import io.github.tiagofar78.grindstone.queue.QueuesManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        EnqueueResult result = QueuesManager.enqueue(sender.getName(), queue, map);
        // TODO send messages based on result
        // TODO in case of success mention sender name

        return true;
    }

}
