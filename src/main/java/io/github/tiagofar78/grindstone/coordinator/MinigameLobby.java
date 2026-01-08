package io.github.tiagofar78.grindstone.coordinator;

import io.github.tiagofar78.grindstone.game.MinigameMap;
import io.github.tiagofar78.grindstone.game.MinigameSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MinigameLobby {

    private List<List<String>> parties;
    private List<MinigameMap> availableMaps;

    public MinigameLobby(String firstParty, List<MinigameMap> availableMaps) {
        parties = new ArrayList<>();
        List<String> party = new ArrayList<>(); // TODO remove and add first party to parties
        parties.add(party);
        this.availableMaps = availableMaps;
    }

    private void assingMap(MinigameMap map) {
        availableMaps = new ArrayList<>();
        availableMaps.add(map);
    }

    public boolean add(String party, MinigameMap map, MinigameSettings settings) { // TODO take care because of parties
        return false; // TODO return true if it is possible, use assign map if it is necessary
    }

    public boolean isFull(MinigameSettings settings) {
        return true; // TODO
    }

    public List<List<String>> getParties() {
        return parties;
    }

    public MinigameMap getMap() {
        int i = new Random().nextInt(availableMaps.size());
        return availableMaps.get(i);
    }

}
