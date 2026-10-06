package io.github.tiagofar78.grindstone.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MatchLobby<P> {

    private Set<P> players = new HashSet<>();
    private Set<P> spectators = new HashSet<>();

    public boolean isInLobby(P player) {
        return players.contains(player) || spectators.contains(player);
    }

//  >------------------------{ Player }------------------------<

    public Collection<P> getPlayers() {
        return players;
    }

    public void addPlayer(P player) {
        players.add(player);
    }

    public void removePlayer(P player) {
        players.add(player);
    }

//  >----------------------{ Spectator }----------------------<

    public Collection<P> getSpectators() {
        return spectators;
    }

    public void addSpectator(P player) {
        spectators.add(player);
    }

    public void removeSpectator(P player) {
        spectators.remove(player);
    }

}
