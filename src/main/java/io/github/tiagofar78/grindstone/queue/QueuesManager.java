package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.GrindstoneConfig;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MapFactory;
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

    private static Map<Party, MatchmakingQueue> partyQueue = new HashMap<>();

    public static boolean isInQueue(Party party) {
        return partyQueue.containsKey(party);
    }

    public static EnqueueResult enqueue(String playerName, MatchmakingQueue queue, MapFactory map) {
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

        return dequeue(party);
    }

    public static DequeueResult dequeue(Party party) {
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

    public static void transferedToGame(List<Party> parties) {
        for (Party party : parties) {
            partyQueue.remove(party);
        }
    }

}
