/**
Creep Smash, a multiplayer towerdefence game
created as a project at the Hochschule fuer
Technik Stuttgart (University of Applied Science)
http://www.hft-stuttgart.de

Copyright (C) 2008 by
 * Andreas Wittig
 * Bernd Hietler
 * Christoph Fritz
 * Fabian Kessel
 * Levin Fritz
 * Nikolaj Langner
 * Philipp Schulte-Hubbert
 * Robert Rapczynski
 * Ron Trautsch
 * Sven Supper
http://creepsmash.sf.net/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/
package com.creeptd.server.game.modes;

import com.creeptd.common.Constants;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A default game mode.
 *
 * New game modes may override the methods provided by DefaultGameMode.
 * 
 * @author Daniel
 */
public abstract class GameMode {
    public static final int LOCATION_UNKNOWN = -1;
    public static final int LOCATION_TOPLEFT = 0;
    public static final int LOCATION_TOPRIGHT = 1;
    public static final int LOCATION_BOTTOMRIGHT = 2;
    public static final int LOCATION_BOTTOMLEFT = 3;

    /** Logging functionality */
    protected static final Logger logger = Logger.getLogger(GameMode.class);
    
    /**
     * Get GameMode instance for specified game.
     *
     * @param game The underlying game
     * @return The GameMode instance
     */
    public static final GameMode forGame(Game game) {
        Constants.Mode mode = game.getMode();
        if (mode.equals(Constants.Mode.TEAM2VS2)) {
            return new Team2vs2GameMode(game);
        } else if (mode.equals(Constants.Mode.SENDRANDOM)) {
            return new SendRandomGameMode(game);
        } else if (mode.equals(Constants.Mode.SENDNEXT)) {
            return new SendNextGameMode(game);
        } else if (mode.equals(Constants.Mode.ALLVSALL)) {
            return new AllvsAllGameMode(game);
        } else {
            logger.error("Unknown game mode: "+game.getMode());
            return null;
        }
    }

    /** The underlying game */
    private Game game;

    /**
     * Create the game mode.
     *
     * @param contexts The game contexts
     */
    protected GameMode(Game game) {
        this.game = game;
    }

    /**
     * Get the underlying game.
     *
     * @return The game
     */
    protected final Game getGame() {
        return this.game;
    }
    
    /**
     * Find a player's location (index).
     * 
     * @param player The player's to search for
     * @return The index or -1 if not in game
     */
    protected final int findPlayerLocation(PlayerInGame player) {
        List<PlayerInGame> players = this.game.getPlayers();
        for (int i=0; i < players.size(); i++) {
            if (players.get(i).equals(player)) {
                return i; // LOCATION == index
            }
        }
        logger.warn("findPlayerLocation returned unknown location, player "+player+" is not in this game");
        return LOCATION_UNKNOWN;
    }

    /**
     * Find player by location  index.
     *
     * @param location The location
     * @return The player at this location
     */
    protected final PlayerInGame findPlayerByLocation(int location) {
        return this.getGame().getPlayers().get(location);
    }

    /**
     * Find the next player in clockwise oder.
     *
     * @param player The next player
     */
    protected final PlayerInGame findNextPlayer(PlayerInGame player) {
        int index = findPlayerLocation(player);
        int num = this.game.numPlayers();
        return this.game.getPlayers().get(++index % num);
    }

    /**
     * Find the next alive player in clockwise order.
     *
     * @param player The player to search from
     * @return Next alive player or null if none
     */
    protected final PlayerInGame findNextAlivePlayer(PlayerInGame player) {
        PlayerInGame current = player;
        PlayerInGame next = null;
        int c = 0;
        do {
            next = findNextPlayer(current);
            current = next;
            if (++c >= this.game.numPlayers()) {
                break; // No alive player found
            }
        } while (next.isGameOver());
        return next;
    }

    /**
     * Find the creep receivers.
     *
     * Default behaviour is to send to the next alive player in clockwise order,
     * but oneself.
     *
     * New modes may override this.
     *
     * @param sender The creep's sender
     * @return List of receiving players
     */
    public List<PlayerInGame> findReceivers(PlayerInGame sender) {
        return findTransfers(sender, sender);
    }

    /**
     * Find the creep transfers.
     *
     * Default behaviour is to get the next alive context in clockwise order,
     * but oneself.
     *
     * New modes may override this.
     *
     * @param sender The creep's sender
     * @param from The player the creep escaped at
     * @return List of receiving players
     */
    public List<PlayerInGame> findTransfers(PlayerInGame sender, PlayerInGame from) {
        PlayerInGame next = findNextAlivePlayer(from);
        List<PlayerInGame> transfers = new LinkedList<PlayerInGame>();
        if (next.equals(sender)) {
            next = findNextAlivePlayer(sender); // Sender cannot be receiver
        }
        if (next.equals(sender)) {
            return transfers; // No more players alive, empty list
        }
        transfers.add(next);
        return transfers;
    }

    /**
     * Check if this player is a winner.
     *
     * Default behaviour is to test if the player's position is 1.
     *
     * @param player The player to test
     * @return true if winner, else false
     */
    public boolean isWinner(PlayerInGame player) {
        return player.getGameOverPosition() == 1;
    }

    /**
     * Check if a players is dead.
     *
     * @param player The player to test
     * @return true if dead, else false
     */
    public boolean isDead(PlayerInGame player) {
        return player.getLifes() <= 0;
    }

    /**
     * Check if game is over.
     *
     * Default behaviour is to test if less than 2 players are alive.
     *
     * @return true if over, else false
     */
    public boolean isGameOver() {
        int deadCount = 0;
        for (PlayerInGame p : game.getPlayers()) {
            if (p.isGameOver()) deadCount++;
        }
        return deadCount >= this.game.numPlayers()-1;
    }

    /**
     * Method is called, when the game starts
     */
    public void onGameStart() {
    }

    /**
     * Method is called every round tick.
     */
    public void onTick(long tickNumber) {
    }

    /**
     * Method is called, when a new round begins and income is calculated. The
     * first call happends directly after onGameStart.
     */
    public void onNewIncome() {
    }

    /**
     * Method is called when a player loses a life.
     *
     * @param player The player losing the life
     */
    public void onLifeTaken(PlayerInGame player) {
    }

    /**
     * Method is called when a player is game over.
     *
     * @param The player being game over
     */
    public void onPlayerGameOver(PlayerInGame player) {
    }

    /**
     * Method is called, when the game is over
     */
    public void onGameOver() {
    }
}
