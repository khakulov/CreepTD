/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.creeptd.server.game.modes;

import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Send to random game mode.
 *
 * @author Daniel
 */
public class SendRandomGameMode extends GameMode {

    protected SendRandomGameMode(Game game) {
        super(game);
    }

    /**
     * Receiver is an alive random player but oneself.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findReceivers(PlayerInGame sender) {
        List<PlayerInGame> players = new LinkedList<PlayerInGame>(this.getGame().getPlayers());
        List<PlayerInGame> receivers = new LinkedList<PlayerInGame>();
        do {
            PlayerInGame randomPlayer = players.get(new Random().nextInt(players.size()));
            if (!randomPlayer.equals(sender) && !randomPlayer.isGameOver()) {
                receivers.add(randomPlayer);
                break;
            }
            players.remove(randomPlayer);
        } while (players.size() > 0);
        return receivers;
    }

    /**
     * Transfer to alive random player.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findTransfers(PlayerInGame sender, PlayerInGame from) {
        return findReceivers(sender); // Includes "from"
    }

    // All other methods are default behaviour
}
