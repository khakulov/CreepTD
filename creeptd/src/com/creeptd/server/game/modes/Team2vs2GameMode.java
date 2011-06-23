/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.creeptd.server.game.modes;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.GameContext.BoardLocation;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.LinkedList;
import java.util.List;

/**
 * Team 2vs2 Game mode.
 *
 * @author Daniel
 */
public class Team2vs2GameMode extends GameMode {

    protected Team2vs2GameMode(Game game) {
        super(game);
    }

    /**
     * Send to first opponent player.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findReceivers(PlayerInGame sender) {
        int location = findPlayerLocation(sender);
        PlayerInGame receiver = null;
        if (location == LOCATION_TOPLEFT || location == LOCATION_TOPRIGHT) {
            receiver = findPlayerByLocation(LOCATION_BOTTOMRIGHT);
        } else {
            receiver = findPlayerByLocation(LOCATION_TOPLEFT);
        }
        List<PlayerInGame> receivers = new LinkedList<PlayerInGame>();
        receivers.add(receiver);
        return receivers;
    }

    /**
     * Transfer to team mate.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findTransfers(PlayerInGame sender, PlayerInGame from) {
        int location = findPlayerLocation(from);
        PlayerInGame transfer = null;
        if (location == LOCATION_TOPRIGHT) {
            transfer = this.getGame().getPlayers().get(LOCATION_TOPLEFT);
        } else if (location == LOCATION_BOTTOMLEFT) {
            transfer = this.getGame().getPlayers().get(LOCATION_BOTTOMRIGHT);
        } else {
            transfer = findNextPlayer(from);
        }
        List<PlayerInGame> transfers = new LinkedList<PlayerInGame>();
        transfers.add(transfer);
        return transfers;
    }

    /**
     * Game is over when both top or bottom players are dead.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        if (findPlayerByLocation(LOCATION_TOPLEFT).isGameOver() && findPlayerByLocation(LOCATION_TOPRIGHT).isGameOver()) {
            return true;
        }
        if (findPlayerByLocation(LOCATION_BOTTOMLEFT).isGameOver() && findPlayerByLocation(LOCATION_BOTTOMRIGHT).isGameOver()) {
            return true;
        }
        return false;
    }

    /**
     * Check if the player is in the winning team.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean isWinner(PlayerInGame player) {
        // Game is not over, so there is no winner
        if (!this.isGameOver()) return false;
        // Else test if player is in the winning team
        int location = findPlayerLocation(player);
        int mate_location = -1;
        switch (location) {
            case LOCATION_TOPLEFT:
                mate_location = LOCATION_TOPRIGHT;
                break;
            case LOCATION_TOPRIGHT:
                mate_location = LOCATION_TOPLEFT;
                break;
            case LOCATION_BOTTOMLEFT:
                mate_location = LOCATION_BOTTOMRIGHT;
                break;
            case LOCATION_BOTTOMRIGHT:
                mate_location = LOCATION_BOTTOMLEFT;
                break;
            default:
                logger.error("Cannot determine mate position, player location is invalid: "+location);
        }
        return super.isWinner(player) || super.isWinner(findPlayerByLocation(mate_location));
    }
}
