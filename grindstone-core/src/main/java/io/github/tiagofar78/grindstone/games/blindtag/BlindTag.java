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
import io.github.tiagofar78.grindstone.games.blindtag.map.TeleportCell;
import io.github.tiagofar78.grindstone.games.blindtag.phases.PlayPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.RevealPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.SubmitGuessPhase;
import io.github.tiagofar78.grindstone.games.blindtag.player.BTPlayer;

import java.util.ArrayList;
import java.util.Collection;
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

    private int turnIndex = 0;

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

        List<Cell> available = new ArrayList<>(map.getCells().stream()
                .filter(c -> !(c instanceof TeleportCell))
                .toList());
        Collections.shuffle(available, random);

        for (int i = 0; i < players.size(); i++) {
            players.get(i).setCurrentCell(available.get(i % available.size()));
        }
    }

    @Override
    public Phase getFirstPhase() {
        return new PlayPhase(this);
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

    public int getTurnIndex() {
        return turnIndex;
    }

    // >-----------------------{ Turn loop }-----------------------<

    public void startTurns() {
        turnIndex = 0;
        onTurnStart(players.get(turnIndex));
    }

    public void endTurn() {
        turnIndex++;

        if (turnIndex % players.size() == 0) {
            for (BTPlayer p : players) {
                if (p != tagger) {
                    p.addScore(ROTATION_SCORE);
                }
            }

            onRotationEnd();

            if (hasWinner()) {
                startNextPhase(new SubmitGuessPhase(this));
                return;
            }
        }

        onTurnStart(players.get(turnIndex % players.size()));
    }

    private boolean hasWinner() {
        for (BTPlayer p : players) {
            if (p.getScore() >= WIN_SCORE) {
                return true;
            }
        }

        return false;
    }

    // >--------------------{ Winner tracking }--------------------<

    public List<BTPlayer> computeWinners() {
        int maxScore = players.stream()
                .mapToInt(BTPlayer::getScore)
                .max()
                .orElse(0);
        return players.stream()
                .filter(p -> p.getScore() == maxScore)
                .toList();
    }

    // >--------------------{ Player Actions }--------------------<

    public void move(BTPlayer player, Direction direction) {
        Cell current = player.getCurrentCell();
        Arrow arrow = current.getArrow(direction);
        if (arrow == null) {
            return;
        }

        moveAndApplyEffects(player, arrow.destination());
    }

    public void useItem(BTPlayer player, Item item) {
        item.use(this, player);
        player.removeItem(item);
    }

    public void submitGuess(BTPlayer player, Cell[][] guessedGrid) {
        int bonus = map.similarityScore(guessedGrid);
        player.addScore(bonus);

        boolean allSubmitted = players.stream().allMatch(p -> p.getGuessedMap() != null);
        if (allSubmitted) {
            startNextPhase();
        }
    }

    // >--------------------{ Game Actions }--------------------<

    public void moveAndApplyEffects(BTPlayer player, Cell destination) {
        player.setCurrentCell(destination);

        destination.triggerTraps(player);
        runTagChecks(player, destination);
        destination.applyEffect(this, player);
    }

    private void runTagChecks(BTPlayer mover, Cell cell) {
        boolean changed = true;
        while (changed) {
            changed = false;

            if (tagger == mover) {
                for (BTPlayer other : players) {
                    if (other != mover && other.getCurrentCell() == cell) {
                        performTag(mover, other);
                        changed = true;
                        break;
                    }
                }
            } else {
                if (tagger.getCurrentCell() == cell) {
                    performTag(tagger, mover);
                    changed = true;
                }
            }
        }
    }

    private void performTag(BTPlayer currentTagger, BTPlayer newTagger) {
        setTagger(newTagger);
        currentTagger.setCurrentCell(map.getRandomNonTeleportCell(random));
        onTagEvent(newTagger, currentTagger);
    }

    // >-------------------{ Messages }-------------------<

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
        List<BTPlayer> winners = computeWinners();
        for (BTPlayer p : players) {
            if (winners.contains(p)) {
                p.sendMessage("BlindTag.victory", MessagesChannel.TITLE);
            } else {
                p.sendMessage("BlindTag.defeat", MessagesChannel.TITLE);
            }
        }

        onGameSummary(players, winners);
        onDisplayGuesses(players);
    }

    // >--------------{ Abstract Rendering Hooks }--------------<

    public abstract void onTurnStart(BTPlayer current);

    public abstract void onRotationEnd();

    public abstract void onTagEvent(BTPlayer newTagger, BTPlayer teleported);

    public abstract void onTrapTriggered(BTPlayer victim, int trapsCount);

    public abstract void onBlankCell(BTPlayer player);

    public abstract void onGoldGained(BTPlayer player, int amount);

    public abstract void onTeleported(BTPlayer player, Cell destination);

    public abstract void openShop(BTPlayer player);

    public abstract void onPathReveal(BTPlayer player, List<Direction> path);

    public abstract void onCompassUsed(BTPlayer player, Cell current, Collection<Arrow> adjacent);

    public abstract void onHorseUsed(BTPlayer player);

    public abstract void onTrapPlaced(BTPlayer player, Cell cell);

    public abstract void onClonePlaced(BTPlayer player, Cell cell);

    public abstract void onSixthSenseUsed(BTPlayer player, Direction hint);

    public abstract void onHawkEyeUsed(BTPlayer player, List<Direction> path);

    public abstract BTPlayer onWitchTargetNeeded(BTPlayer caster, List<BTPlayer> targets);

    public abstract void onNeedyUsed(BTPlayer player);

    public abstract void onSubmitGuessStarted(List<BTPlayer> players);

    public abstract void onRevealPhaseStarted(List<BTPlayer> players);

    public abstract void onNoMovesAvailable(BTPlayer player);

    public abstract void onUseItemsPhase(BTPlayer player);

    public abstract void onGameSummary(List<BTPlayer> players, List<BTPlayer> winners);

    public abstract void onDisplayGuesses(List<BTPlayer> players);
}
