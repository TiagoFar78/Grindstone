package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.Grindstone;
import io.github.tiagofar78.grindstone.GrindstoneConfig;
import io.github.tiagofar78.grindstone.commands.JoinMatchmakingQueue;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;
import io.github.tiagofar78.grindstone.party.PartyService;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class QueuesManager {

    private QueuesManager() {
    };

    public static void registerGamemode(
            MinigameFactory factory,
            MinigameSettings gamemode,
            List<MinigameMap> availableMaps,
            String commandLabel
    ) {
        MatchmakingQueue queue = new LobbyBasedQueue(factory, gamemode, availableMaps);
        registerGamemode(availableMaps, commandLabel, queue);
    }

    public static void registerGamemode(List<MinigameMap> availableMaps, String commandLabel, MatchmakingQueue queue) {
        if (commandLabel.contains(" ") || commandLabel.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not register gamemode with command label \"" + commandLabel + "\". A command cannot contain spaces."
            );
        }

        Grindstone instance = Grindstone.getInstance();
        instance.getCommand(commandLabel).setExecutor(new JoinMatchmakingQueue(queue));

        for (MinigameMap map : availableMaps) {
            String label = commandLabel + "_" + map.getName().toLowerCase();
            instance.getCommand(label).setExecutor(new JoinMatchmakingQueue(queue, map));
        }
    }

    private static Map<Party, MatchmakingQueue> partyQueue = new HashMap<>();

    public static boolean isInQueue(Party party) {
        return partyQueue.containsKey(party);
    }

    public static EnqueueResult enqueue(String playerName, MatchmakingQueue queue, MinigameMap map) {
        Party party = PartyService.getParty(playerName);
        if (!party.canQueueUp(playerName)) {
            return EnqueueResult.NOT_ALLOWED_BY_PARTY;
        }

        if (isInQueue(party)) {
            return EnqueueResult.ALREADY_IN_QUEUE;
        }

        if (isAny(GamesManager::isInOngoingGame, party.getMembers())) {
            return EnqueueResult.STILL_IN_GAME;
        }

        GrindstoneConfig config = GrindstoneConfig.getInstance();
        if (!config.canQueueAfterDisconnect && isAny(GamesManager::leftOngoingGame, party.getMembers())) {
            return EnqueueResult.DISCONNECTED_FROM_ONGOING_GAME;
        }

        if (party.size() > queue.maxPartySize()) {
            return EnqueueResult.PARTY_TOO_LARGE;
        }

        partyQueue.put(party, queue);
        queue.enqueue(party, map);
        return EnqueueResult.SUCCESS;
    }

    public static DequeueResult dequeue(String playerName) {
        Party party = PartyService.getParty(playerName);
        if (!party.canQueueUp(playerName)) {
            return DequeueResult.NOT_ALLOWED_BY_PARTY;
        }

        if (!isInQueue(party)) {
            return DequeueResult.NOT_IN_QUEUE;
        }

        MatchmakingQueue queue = partyQueue.remove(party);
        queue.dequeue(party);
        return DequeueResult.SUCCESS;
    }

    private static boolean isAny(Function<String, Boolean> f, Collection<String> members) {
        return members.stream().anyMatch(m -> f.apply(m));
    }

}
