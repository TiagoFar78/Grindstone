package io.github.tiagofar78.grindstone.games.blindtag;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Team;
import io.github.tiagofar78.grindstone.game.phases.Phase;
import io.github.tiagofar78.grindstone.games.blindtag.items.Item;
import io.github.tiagofar78.grindstone.games.blindtag.map.Arrow;
import io.github.tiagofar78.grindstone.games.blindtag.map.BTMap;
import io.github.tiagofar78.grindstone.games.blindtag.map.Cell;
import io.github.tiagofar78.grindstone.games.blindtag.map.Direction;
import io.github.tiagofar78.grindstone.games.blindtag.map.MapGenerator;
import io.github.tiagofar78.grindstone.games.blindtag.phases.PlayPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.RevealPhase;
import io.github.tiagofar78.grindstone.games.blindtag.phases.SubmitGuessPhase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlindTag extends Game<BTBridge, BTPlayer, Team<BTPlayer>> {

    public static final int PLAYER_COUNT = 3;
    public static final int WIN_SCORE = 200;
    public static final int ROTATION_SCORE = 10;

    private BTMap map;
    private List<BTPlayer> players;
    private BTPlayer tagger;

    private int turnIndex = 0;

    private final Random random;

    protected BlindTag(GameDependencies dependencies, BTBridge bridge, List<Team<BTPlayer>> teams, Random random) {
        super(dependencies, bridge, teams);
        this.random = random;
    }

    protected BlindTag(GameDependencies dependencies, BTBridge bridge, List<Team<BTPlayer>> teams) {
        this(dependencies, bridge, teams, new Random());
    }

    // >-----------------------{ Lifecycle }-----------------------<

    @Override
    public void load() {
        map = MapGenerator.generate(random);

        players = new ArrayList<>();
        for (BTPlayer p : getLobby().getPlayers()) {
            BTPlayer btp = (BTPlayer) p;
            players.add(btp);
            btp.setCurrentCell(map.getRandomNonTeleportCell(random));
        }

        tagger = players.get(0);
    }

    @Override
    public Phase<BlindTag> getFirstPhase() {
        return new PlayPhase(this);
    }

    @Override
    public void removePlayerFromGame(BTPlayer player) {
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
        gameEngine.onTurnStart(this, players.get(turnIndex));
    }

    public void endTurn() {
        turnIndex++;

        if (turnIndex % players.size() == 0) {
            for (BTPlayer p : players) {
                if (p != tagger) {
                    p.addScore(ROTATION_SCORE);
                }
            }

            gameEngine.onRotationEnd(this);

            if (hasWinner()) {
                startNextPhase(new SubmitGuessPhase(this));
                return;
            }
        }

        gameEngine.onTurnStart(this, players.get(turnIndex % players.size()));
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
        int maxScore = players.stream().mapToInt(BTPlayer::getScore).max().orElse(0);
        return players.stream().filter(p -> p.getScore() == maxScore).toList();
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

        destination.triggerTraps(this, player);
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
        gameEngine.onTag(this, newTagger, currentTagger);
    }

    // >-------------------{ Messages }-------------------<

    @Override
    public void sendLoadingMessage() {
        // Empty, it loads instantly, no need to send loading message
    }

    public void sendPlayerLeftMessage(BTPlayer player) {
        player.sendMessage("BlindTag.you_left", MessagesChannel.TITLE);
        for (BTPlayer p : getLobby().getPlayers()) {
            if (p != player) {
                p.sendMessage("BlindTag.player_left", MessagesChannel.CHAT, player);
            }
        }
    }

}
