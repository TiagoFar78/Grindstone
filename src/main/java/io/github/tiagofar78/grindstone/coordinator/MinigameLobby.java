package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;
import io.github.tiagofar78.grindstone.party.Party;
import io.github.tiagofar78.grindstone.util.TeamLayoutSolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class MinigameLobby {

    private MinigameFactory factory;
    private MinigameSettings settings;
    private List<MinigameMap> availableMaps;
    private List<Party> parties;

    public MinigameLobby(
            MinigameFactory factory,
            MinigameSettings settings,
            List<MinigameMap> availableMaps,
            Party firstParty
    ) {
        this.factory = factory;
        this.settings = settings;
        this.availableMaps = availableMaps;
        parties = new ArrayList<>();
        parties.add(firstParty);
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

        parties.add(party);
        // TODO send new player message

        if (isMatchFound()) {
            initMinigame();
        }

        return true;
    }

    public boolean remove(Party party) {
        if (parties.remove(party)) {
            // TODO send left message
            return true;
        }

        return false;
    }

    public boolean isEmpty() {
        return parties.isEmpty();
    }

    private boolean canFitParty(Party party) {
        Collection<Collection<String>> parties = this.parties.stream().map(p -> p.getMembers()).toList();
        return TeamLayoutSolver.canFitParty(party.size(), settings.maxTeams(), settings.maxPlayersPerTeam(), parties);
    }

    private boolean isMatchFound() {
        return getTotalPlayers() >= settings.maxTeams() * settings.maxPlayersPerTeam();
    }

    private void initMinigame() {
        // TODO send match found message
        // TODO 1s scheduler

        Coordinator.createMinigame(factory, settings, getMap(), parties);
    }

    public MinigameMap getMap() {
        int i = new Random().nextInt(availableMaps.size());
        return availableMaps.get(i);
    }

    private int getTotalPlayers() {
        int sum = 0;
        for (Party party : parties) {
            sum += party.size();
        }

        return sum;
    }

}
