package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MatchLobby {

    private Set<Player> players = new HashSet<>();
    private Set<Player> spectators = new HashSet<>();

    public boolean isInLobby(Player player) {
        return players.contains(player) || spectators.contains(player);
    }

//  >------------------------{ Player }------------------------<
    
    public Collection<Player> getPlayers() {
        return players;
    }
    
    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.add(player);
    }

//  >----------------------{ Spectator }----------------------<
    
    public Collection<Player> getSpectators() {
        return spectators;
    }

    public void addSpectator(Player player) {
        spectators.add(player);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player);
    }

}
