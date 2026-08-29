package io.github.tiagofar78.grindstone.games.blindtag;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.map.MapGenerator;
import io.github.tiagofar78.grindstone.games.blindtag.phases.PlayPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.RevealPhase;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class BlindTag extends Game {

    public static final int PLAYER_COUNT = 3;
    public static final int WIN_SCORE = 200;
    public static final int ROTATION_SCORE = 10;

    private BTMap map;
    private List<BTPlayer> players;
    private BTPlayer tagger;
    private PlayPhase activePlayPhase;

    private final Random random;

    protected BlindTag(GameDependencies dependencies, List<Player> players, Random random) {
        super(dependencies, players);
        this.random = random;
    }

    protected BlindTag(GameDependencies dependencies, List<Player> players) {
        this(dependencies, players, new Random());
    }

    // >-----------------------{ Lifecycle }-----------------------<

    @Override
    public void load() {
        map = MapGenerator.generate(random);

        players = new ArrayList<>();
        for (Player p : getLobby().getPlayers()) {
            players.add((BTPlayer) p);
        }

        tagger = players.get(random.nextInt(players.size()));
        tagger.setCurrentCell(map.getRandomBlankCell(random));

        for (BTPlayer player : players) {
            if (player == tagger) {
                continue;
            }

            Cell currCell = map.getRandomBlankCell(random);
            while (currCell == tagger.getCurrentCell()) {
                currCell = map.getRandomBlankCell(random);
            }

            player.setCurrentCell(currCell);
        }
    }

    @Override
    public Phase getFirstPhase() {
        activePlayPhase = new PlayPhase(this);
        return activePlayPhase;
    }

    @Override
    public void removePlayerFromGame(Player player) {
        if (players == null) {
            return;
        }
        players.remove(player);

        if (players.size() == 1) {
            startNextPhase(new RevealPhase(this));
        }
    }

    // >----------------------{ Accessors }----------------------<

    public BTMap getMap() {
        return map;
    }

    public List<BTPlayer> getBTPlayers() {
        return Collections.unmodifiableList(players);
    }

    public BTPlayer getTagger() {
        return tagger;
    }

    public void setTagger(BTPlayer tagger) {
        this.tagger = tagger;
    }

    public Random getRandom() {
        return random;
    }

    // >--------------------{ Winner tracking }--------------------<

    public List<BTPlayer> computeWinner() {
        int maxScore = players.stream()
                .mapToInt(BTPlayer::getScore)
                .max()
                .orElse(0);
        winners = btPlayers.stream()
                .filter(p -> p.getScore() == maxScore)
                .toList();
    }

    // >--------------------{ Game Actions }--------------------<

    public void move(BTPlayer player, Direction direction) {
        Cell current = player.getCurrentCell();
        Arrow arrow = current.getArrow(direction);
        if (arrow == null) {
            return;
        }

        Cell destination = arrow.destination();
        player.setCurrentCell(destination);

        destination.triggerTraps(player);
        runTagChecks(player, destination);
    }

    private void runTagChecks(BTPlayer mover, Cell cell) {
        boolean changed = true;
        while (changed) {
            changed = false;

            if (tagger == mover) {
                // Tagger moved: look for a victim on the same cell
                for (BTPlayer other : players) {
                    if (other != mover && other.getCurrentCell() == cell) {
                        performTag(mover, other);
                        changed = true;
                        break; // restart loop â€” mover is no longer the tagger
                    }
                }
            } else {
                // Non-tagger moved: check if the tagger is on the same cell
                if (tagger.getCurrentCell() == cell) {
                    performTag(tagger, mover);
                    changed = true;
                    // mover is now the tagger and is at spawn; loop re-checks spawn cell
                    // but no other player should be there, so it will exit cleanly
                }
            }
        }
    }

    /**
     * Transfers the tag from {@code currentTagger} to {@code newTagger},
     * then teleports {@code currentTagger} to the spawn cell.
     */
    private void performTag(BTPlayer currentTagger, BTPlayer newTagger) {
        setTagger(newTagger);
        currentTagger.moveTo(map.getSpawnCell());
        onTagEvent(newTagger, currentTagger);
    }

    /**
     * Applies this turn's cell effect for {@code player} at their current cell.
     * Called by TurnEngine after trap/tag checks resolve.
     */
    public void applyCellEffect(BTPlayer player) {
        player.getCurrentCell().applyEffect(this, player);
    }

    /**
     * Buys {@code item} from the shop for {@code player}.
     * Returns {@code true} if purchase succeeded.
     * Implemented in Task 8.
     */
    public boolean buyItem(BTPlayer player, Item item) {
        return false;
    }

    /**
     * Uses {@code item} from {@code player}'s inventory: calls
     * {@code item.use(this, player)} then removes the item from their slots.
     * Implemented in Task 8.
     */
    public void useItem(BTPlayer player, Item item) {
        // Implemented in Task 8
    }

    // >-------------------{ Messages }-------------------<

    @Override
    public void sendLoadingMessage() {
        // Fast load â€” nothing to announce
    }

    @Override
    public void sendPlayerLeftMessage(Player player) {
        player.sendMessage("BlindTag.you_left", MessagesChannel.TITLE);
        for (Player p : getLobby().getPlayers()) {
            if (p != player) {
                p.sendMessage("BlindTag.player_left", MessagesChannel.CHAT, player);
            }
        }
    }

    @Override
    public void sendVictoryMessage() {
        for (BTPlayer winner : getWinners()) {
            winner.sendMessage("BlindTag.victory", MessagesChannel.TITLE);
        }
    }

    @Override
    public void sendDefeatOrDrawMessage() {
        List<BTPlayer> winners = getWinners();
        for (BTPlayer p : btPlayers) {
            if (!winners.contains(p)) {
                p.sendMessage("BlindTag.defeat", MessagesChannel.TITLE);
            }
        }
    }

    // >--------------{ Abstract Rendering Hooks }--------------<

    /** Called at the start of each player's turn. */
    public abstract void onTurnStart(BTPlayer current);

    /** Called after all 3 players complete a rotation. */
    public abstract void onRotationEnd();

    /** Called when a tag event occurs. */
    public abstract void onTagEvent(BTPlayer newTagger, BTPlayer teleportedToSpawn);

    /** Called when trap(s) are triggered. */
    public abstract void onTrapTriggered(BTPlayer victim, int trapsTriggered);

    /** Called when the player lands on a blank cell. */
    public abstract void onBlankCell(BTPlayer player);

    /** Called when a player gains gold from a gold cell. */
    public abstract void onGoldGained(BTPlayer player, int amount);

    /** Called when a player is teleported by a TeleportCell. */
    public abstract void onTeleported(BTPlayer player, Cell destination);

    /** Called when the shop should be opened for a player. */
    public abstract void openShop(BTPlayer player);

    /** Called when a path should be revealed to a player (unlucky wheel / HawkEye). */
    public abstract void onPathReveal(BTPlayer player, List<Cell> path);

    /** Called when Compass is used. */
    public abstract void onCompassUsed(BTPlayer player, Cell current, List<Cell> adjacent);

    /** Called when Horse is used. */
    public abstract void onHorseUsed(BTPlayer player);

    /** Called when a Trap is placed. */
    public abstract void onTrapPlaced(BTPlayer player, Cell cell);

    /** Called when a Clone is placed. */
    public abstract void onClonePlaced(BTPlayer player, Cell cell);

    /** Called when SixthSense is used. */
    public abstract void onSixthSenseUsed(BTPlayer player, Direction hint);

    /** Called when HawkEye is used. */
    public abstract void onHawkEyeUsed(BTPlayer player, List<Cell> path);

    /** Called when Witch needs a target. Returns the chosen target player. */
    public abstract BTPlayer onWitchTargetNeeded(BTPlayer caster, List<BTPlayer> targets);

    /** Called when Needy is used. */
    public abstract void onNeedyUsed(BTPlayer player);

    /** Called at the start of the submit-guess phase. */
    public abstract void onSubmitGuessStarted(List<BTPlayer> players);

    /** Called at the start of the reveal phase. */
    public abstract void onRevealPhaseStarted(List<BTPlayer> players);

    /** Called when a player has no valid moves available. */
    public abstract void onNoMovesAvailable(BTPlayer player);

    /** Called when items can be used by the current player. */
    public abstract void onUseItemsPhase(BTPlayer player);
}
