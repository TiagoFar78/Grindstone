package io.github.tiagofar78.grindstone.games.turnbasedduel;

import java.util.List;

import io.github.tiagofar78.grindstone.game.Game;
import io.github.tiagofar78.grindstone.game.GameDependencies;
import io.github.tiagofar78.grindstone.game.MessagesChannel;
import io.github.tiagofar78.grindstone.game.Player;
import io.github.tiagofar78.grindstone.game.phases.Phase;

public abstract class TurnBasedDuel extends Game {
    
    private Player[] players;
    private Player winner;

    public TurnBasedDuel(GameDependencies dependencies, Player p1, Player p2) {
        super(dependencies, List.of(p1, p2));
        players = new Player[] { p1, p2 };
    }
    
    public Player getPlayer(int index) {
        return players[index];
    }
    
    public Player getOtherPlayer(Player p) {
        return players[0] == p ? players[1] : players[0];
    }
    
    public void setWinner(int playerIndex) {
        winner = players[playerIndex];
    }

    @Override
    public void removePlayerFromGame(Player player) {
        winner = getOtherPlayer(player);
        startNextPhase();
    }

    @Override
    public Phase getFirstPhase() {
        return new TurnBasedDuelPhase(this);
    }

    @Override
    public void sendLoadingMessage() {
        // Empty, it loads instantly, no need to send loading message
    }

    @Override
    public void sendPlayerLeftMessage(Player player) {
        player.sendMessage(Messages.YOU_LEFT, MessagesChannel.TITLE);
        getOtherPlayer(player).sendMessage(Messages.PLAYER_LEFT, MessagesChannel.CHAT, player);
    }

    @Override
    public void sendVictoryMessage() {
        if (winner != null) {
            winner.sendMessage(Messages.VICTORY, MessagesChannel.TITLE);
        }
    }

    @Override
    public void sendDefeatOrDrawMessage() {
        if (winner == null) {
            for (Player p : players) {
                p.sendMessage(Messages.DRAW, MessagesChannel.TITLE);
            }
        }
        
        getOtherPlayer(winner).sendMessage(Messages.DEFEAT, MessagesChannel.TITLE);
    }

}
