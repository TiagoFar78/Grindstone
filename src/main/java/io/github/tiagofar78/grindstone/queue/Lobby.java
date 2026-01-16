package io.github.tiagofar78.grindstone.queue;

import io.github.tiagofar78.grindstone.GrindstoneConfig;
import io.github.tiagofar78.grindstone.bukkit.Scheduler;
import io.github.tiagofar78.grindstone.game.GamesManager;
import io.github.tiagofar78.grindstone.game.MinigameFactory;
import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;
import io.github.tiagofar78.grindstone.util.TeamLayoutSolver;

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Lobby {

    private LobbyBasedQueue queue;

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MinigameMap> availableMaps;
    private List<Party> parties;

    private int startCooldown = -1;
    private BukkitTask task = null;

    public Lobby(
            LobbyBasedQueue queue,
            MinigameFactory factory,
            MinigameSettings settings,
            List<MinigameMap> availableMaps,
            Party firstParty
    ) {
        this.queue = queue;
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = availableMaps;
        parties = new ArrayList<>();
        parties.add(firstParty.copy());
        updateTimer();
    }

    private void assignMap(MinigameMap map) {
        availableMaps = new ArrayList<>();
        availableMaps.add(map);
    }

    public boolean add(Party party, MinigameMap map) {
        int maxPlayers = settings.maxTeams() * settings.maxPlayersPerTeam();
        if (getTotalPlayers() + party.size() > maxPlayers) {
            return false;
        }

        if (party.size() <= settings.maxPlayersPerTeam() && !canFitParty(party)) {
            return false;
        }

        if (map != null) {
            if (!availableMaps.contains(map)) {
                return false;
            }

            assignMap(map);
        }

        parties.add(party.copy());
        // TODO send new player message

        updateTimer();
        return true;
    }

    private int getTotalPlayers() {
        int sum = 0;
        for (Party party : parties) {
            sum += party.size();
        }

        return sum;
    }

    private boolean canFitParty(Party party) {
        Collection<Collection<String>> parties = this.parties.stream().map(p -> p.getMembers()).toList();
        return TeamLayoutSolver.canFitParty(party.size(), settings.maxTeams(), settings.maxPlayersPerTeam(), parties);
    }

    public boolean remove(Party party) {
        Party storedParty = findParty(party);
        if (storedParty == null) {
            return false;
        }

        parties.remove(storedParty);
        // TODO send left message
        return true;
    }

    private Party findParty(Party current) {
        for (Party party : parties) {
            if (party.wasCopiedFrom(current)) {
                return party;
            }
        }

        return null;
    }

    public boolean isEmpty() {
        return parties.isEmpty();
    }

    private void initMinigame() {
        List<Collection<String>> partiesMembers = parties.stream().map(p -> p.getMembers()).toList();
        GamesManager.createMinigame(factory, settings, getMap(), partiesMembers, false);
        QueuesManager.transferedToGame(parties);
        queue.transferedToGame(this);
    }

    public MinigameMap getMap() {
        int i = new Random().nextInt(availableMaps.size());
        return availableMaps.get(i);
    }

    private void updateTimer() {
        int maxPlayers = settings.maxPlayersPerTeam() * settings.maxTeams();
        CountdownStage currStage = CountdownStage.getStage(getTotalPlayers(), settings.minPlayers(), maxPlayers);
        int cooldown = currStage.getCooldown();

        if (cooldown == -1) {
            if (startCooldown != -1) {
                // TODO send message to players saying the timer has reset
            }

            startCooldown = -1;
            task.cancel();
            return;
        }

        if (startCooldown == -1) {
            startCooldown = cooldown + 1;
        }

        if (cooldown < startCooldown) {
            startCooldown = cooldown;
            task.cancel();
            runTimer(startCooldown);
            return;
        }
    }

    private void runTimer(int secondsLeft) {
        if (secondsLeft == 0) {
            initMinigame();
            return;
        }

        if (secondsLeft % GrindstoneConfig.getInstance().gameCountdownAnnouncementsInterval == 0) {
            // TODO send countdown message
        }

        task = Scheduler.runLater(1, new Runnable() {

            @Override
            public void run() {
                runTimer(secondsLeft - 1);
            }
        });
    }

    public void delete() {
        task.cancel();
    }

    private enum CountdownStage {

        NONE(-1),
        MIN(GrindstoneConfig.getInstance().lobbyQueueMinPlayersCooldown),
        HALF(GrindstoneConfig.getInstance().lobbyQueueHalfPlayersCooldown),
        FULL(GrindstoneConfig.getInstance().lobbyQueueFullPlayersCooldown);

        private int stageCooldown;

        CountdownStage(int startCooldown) {
            this.stageCooldown = startCooldown;
        }

        public int getCooldown() {
            return stageCooldown;
        }

        public static CountdownStage getStage(int currPlayers, int minPlayers, int maxPlayers) {
            if (currPlayers < minPlayers) {
                return CountdownStage.NONE;
            }

            int half = (minPlayers + maxPlayers) / 2;
            if (currPlayers < half) {
                return CountdownStage.MIN;
            }

            if (currPlayers < maxPlayers) {
                return CountdownStage.HALF;
            }

            return CountdownStage.FULL;
        }

    }

}
