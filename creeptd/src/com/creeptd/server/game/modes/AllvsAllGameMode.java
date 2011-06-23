/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.creeptd.server.game.modes;

import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.LinkedList;
import java.util.List;

/**
 * All vs All game mode.
 * 
 * @author Daniel
 */
public class AllvsAllGameMode extends GameMode {
    
    protected AllvsAllGameMode(Game game) {
        super(game);
    }

    /**
     * Receivers are all alive players but oneself.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findReceivers(PlayerInGame sender) {
        List<PlayerInGame> receivers = new LinkedList<PlayerInGame>();
        for (PlayerInGame p : this.getGame().getPlayers()) {
            if (!p.isGameOver() && !p.equals(sender)) {
                receivers.add(p);
            }
        }
        return receivers;
    }

    /**
     * Transfer is the player, where the creep escaped, himself.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findTransfers(PlayerInGame sender, PlayerInGame from) {
        List<PlayerInGame> transfers = new LinkedList<PlayerInGame>();
        if (!from.isGameOver()) {
            transfers.add(from);
        }
        return transfers;
    }

    // All other methods are default behaviour
}
