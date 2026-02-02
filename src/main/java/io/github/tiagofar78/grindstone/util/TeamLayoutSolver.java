package io.github.tiagofar78.grindstone.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public final class TeamLayoutSolver {

    private TeamLayoutSolver() {
    }

    public static <T> List<List<T>> solve(
            Collection<List<T>> parties,
            int maxTeams,
            int maxPlayersPerTeam
    ) {
        List<List<T>> teams = new ArrayList<>();
        for (int i = 0; i < maxTeams; i++) {
            teams.add(new ArrayList<>());
        }

        List<List<T>> ordered = parties.stream().sorted(Comparator.comparingInt(p -> -p.size())).toList();
        for (Collection<T> party : ordered) {
            placeParty(party, teams, maxPlayersPerTeam);
        }

        return teams;
    }

    private static <T> void placeParty(Collection<T> party, List<List<T>> teams, int maxPerTeam) {
        if (party.size() <= maxPerTeam) {
            for (List<T> team : teams) {
                if (team.size() + party.size() <= maxPerTeam) {
                    team.addAll(party);
                    return;
                }
            }

            throw new IllegalStateException("Party of size " + party.size() + " cannot fit into any team");
        }

        List<T> remaining = new ArrayList<>(party);
        for (List<T> team : teams) {
            int slots = Math.min(maxPerTeam - team.size(), remaining.size());
            for (int i = 0; i < slots; i++) {
                team.add(remaining.remove(remaining.size() - 1));
            }

            if (remaining.size() == 0) {
                return;
            }
        }

        throw new IllegalStateException("Party of size " + party.size() + " cannot fit into any team");
    }

    public static <T> boolean canFitParty(
            int partySize,
            int maxTeams,
            int maxPerTeam,
            Collection<Collection<T>> parties
    ) {
        int[] slots = new int[maxTeams];
        Arrays.fill(slots, maxPerTeam);

        List<Integer> ordered = parties.stream().map(p -> p.size()).sorted((a, b) -> -Integer.compare(a, b)).toList();
        for (int size : ordered) {
            place(size, slots, maxPerTeam);
        }

        return place(partySize, slots, maxPerTeam);
    }

    private static boolean place(int size, int[] slots, int maxPerTeam) {
        if (size <= maxPerTeam) {
            for (int i = 0; i < slots.length; i++) {
                if (slots[i] >= size) {
                    slots[i] -= size;
                    return true;
                }
            }

            return false;
        }

        int remaining = size;
        for (int i = 0; i < slots.length && remaining > 0; i++) {
            int used = Math.min(slots[i], remaining);
            slots[i] -= used;
            remaining -= used;
        }

        return remaining == 0;
    }

}
