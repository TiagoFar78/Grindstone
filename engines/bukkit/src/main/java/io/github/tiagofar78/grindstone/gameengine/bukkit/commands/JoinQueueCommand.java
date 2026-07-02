package io.github.tiagofar78.grindstone.gameengine.bukkit.commands;

public class JoinQueueCommand { /* implements CommandExecutor {

    private MatchmakingQueue queue;
    private MapFactory map;

    public JoinQueueCommand(MatchmakingQueue queue) {
        this(queue, null);
    }

    public JoinQueueCommand(MatchmakingQueue queue, MapFactory map) {
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
        BukkitPlayer.sendMessage(sender, ((Player) sender).locale(), result.getMessageKey());

        return true;
    }
    */

}
