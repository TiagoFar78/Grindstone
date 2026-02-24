package io.github.tiagofar78.grindstone.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;

public class TeamLayoutSolverTest {

    // Solve test

    @Test
    public void testSimpleExactFit() {
        List<List<String>> parties = List.of(
                List.of("A", "B"),
                List.of("C", "D")
        );

        List<List<String>> result = TeamLayoutSolver.solve(parties, 2, 2);

        Assert.assertEquals(result.size(), 2);

        Assert.assertEquals(result.get(0).size(), 2);
        Assert.assertEquals(result.get(1).size(), 2);
    }

    @Test
    public void testSplitLargePartyAcrossTeams() {
        List<List<String>> parties = List.of(
                List.of("A", "B", "C", "D")
        );

        List<List<String>> result = TeamLayoutSolver.solve(parties, 2, 2);

        Assert.assertEquals(result.get(0).size(), 2);
        Assert.assertEquals(result.get(1).size(), 2);
    }

    @Test
    public void testMultiplePartiesBalanced() {
        List<List<String>> parties = List.of(
                List.of("A", "B"),
                List.of("C"),
                List.of("D"),
                List.of("E")
        );

        List<List<String>> result = TeamLayoutSolver.solve(parties, 2, 3);

        int totalPlayers = result.stream().mapToInt(List::size).sum();
        Assert.assertEquals(totalPlayers, 5);

        Assert.assertTrue(result.get(0).size() <= 3);
        Assert.assertTrue(result.get(1).size() <= 3);
    }

    @Test
    public void testLargestPartyPlacedFirst() {
        List<List<String>> parties = List.of(
                List.of("A"),
                List.of("B"),
                List.of("C", "D"),
                List.of("E", "F")
        );

        List<List<String>> result = TeamLayoutSolver.solve(parties, 2, 3);

        Assert.assertTrue(result.stream().allMatch(t -> t.size() == 3));
    }

    // Can fit test

    @Test
    public void testEmptyLobby() {
        boolean result = TeamLayoutSolver.canFitParty(1, 2, 2, List.of());

        Assert.assertTrue(result);
    }

    @Test
    public void testExactFitAfterExistingParties() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A", "B"),
                List.of("C")
        );
        boolean result = TeamLayoutSolver.canFitParty(2, 2, 3, currentTeams);

        Assert.assertTrue(result);
    }

    @Test
    public void testNoSpaceLeftFullLobby() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A", "B")
        );

        boolean result = TeamLayoutSolver.canFitParty(1, 1, 2, currentTeams);

        Assert.assertFalse(result);
    }

    @Test
    public void testNoSpaceLeftAlmostFullLobby() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A", "B")
        );

        boolean result = TeamLayoutSolver.canFitParty(2, 1, 3, currentTeams);

        Assert.assertFalse(result);
    }

    @Test
    public void testOversizedPartyDoesNotFit() {
        boolean result = TeamLayoutSolver.canFitParty(5, 2, 2, List.of());

        Assert.assertFalse(result);
    }

    @Test
    public void testGreedyOrderMatters() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A"),
                List.of("B"),
                List.of("C", "D")
        );
        boolean result = TeamLayoutSolver.canFitParty(2, 2, 3, currentTeams);

        Assert.assertTrue(result);
    }

    @Test
    public void testMultipleComplexDistribution() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A", "B"),
                List.of("C", "D"),
                List.of("E"),
                List.of("F")
        );
        boolean result = TeamLayoutSolver.canFitParty(3, 3, 3, currentTeams);

        Assert.assertTrue(result);
    }

    @Test
    public void testTotalCapacityWithOversizedParty() {
        Collection<Collection<String>> currentTeams = List.of(
                List.of("A", "B"),
                List.of("C")
        );
        boolean result = TeamLayoutSolver.canFitParty(4, 2, 3, currentTeams);

        Assert.assertFalse(result);
    }

}
