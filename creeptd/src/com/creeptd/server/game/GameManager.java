/**
CreepTD is an online multiplayer towerdefense game
formerly created under the name CreepSmash as a project
at the Hochschule fuer Technik Stuttgart (University of Applied Science)

CreepTD (Since version 0.7.0+) Copyright (C) 2011 by
 * Daniel Wirtz, virtunity media
http://www.creeptd.com

CreepSmash (Till version 0.6.0) Copyright (C) 2008 by
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
package com.creeptd.server.game;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.server.GameDescription;
import com.creeptd.common.messages.server.GamesMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.server.Lobby;
import com.creeptd.server.client.Client;

public class GameManager implements GameObserverInterface {

    private static Logger logger = Logger.getLogger(GameManager.class);
    private static List<Game> games = new LinkedList<Game>();
    private static final GameManager INSTANCE = new GameManager();

    private GameManager() {
    }

    public static void add(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("'newClient' was null");
        }
        synchronized (games) {
            if (games.contains(game)) {
                return;
            }
            games.add(game);
        }
        game.addObserver(INSTANCE);
        Lobby.sendAll(getGamesMessage());
        logger.info("game added: " + game);
    }

    public static void remove(Game game) {
        synchronized (games) {
            if (games.contains(game)) {
                games.remove(game);
            }
        }
        Lobby.sendAll(getGamesMessage());
        logger.info("game removed: " + game);
    }

    /**
     * Find the game with the given id.
     * @param gameId the gameId to look for.
     * @return the game, of null if it could not be found.
     */
    public static Game find(int gameId) {
        synchronized (games) {
            for (Game game : games) {
                if (game.getGameId() == gameId) {
                    return game;
                }
            }
        }
        return null;
    }

    /**
     * Creates a GAMES message with the current list of games.
     * @return the message
     */
    public static GamesMessage getGamesMessage() {
        Set<GameDescription> gameDescriptions = new HashSet<GameDescription>();
        synchronized (games) {
            for (Game game : games) {
                gameDescriptions.add(game.getGameDescription());
            }
        }
        return new GamesMessage(gameDescriptions);
    }

    public static boolean sendDirectMessage(Client sender, String receiverName, String message) {
        synchronized (games) {
            for (Game game : games) {
                List<PlayerInGame> players = game.getPlayers();
                synchronized (players) {
                    for (PlayerInGame player : players) {
                        if (player.getClient().getPlayerModel().getName().equalsIgnoreCase(receiverName)) {
                            if (player.isConnected()) {
                                player.getClient().send(new ServerChatMessage("Server", message));
                                sender.send(new ServerChatMessage("Server", message));
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean kickClient(String username, Client adminClient) {
        /* TODO synchronized(games) {
        for (Game game : games) {
        for (PlayerInGame player : game.getClients()) {
        if (player.getClient().getPlayerModel().getName().equals(username)) {
        logger.info("Kick Player inGame: " + player.getClient().getPlayerModel().getName());
        game.sendAll(new ServerChatMessage("Server",
        "<span style=\"color:red;\">"
        + player.getClient().getPlayerModel().getName()
        + " was kicked by <b>"
        + adminClient.getPlayerModel().getName()
        + "</b></span>"));
        game.sendAll(new ServerChatMessage("Server", player
        .getClient().getPlayerModel().getName()
        + " has left..."));
        //game.removeClient(player.getClient());

        if (game.getClients().isEmpty()) {
        game.shutdown();
        //game.gameTerminated();
        } else {
        game.sendAll(new PlayerQuitMessage(username,
        "Kick", player.getClient().getClientID()));
        game.gamePlayersChanged();
        }

        player.getClient().disconnect();

        adminClient
        .send(new ServerChatMessage("Server",
        "<span style=\"color:red;\">"
        + player.getClient().getPlayerModel().getName()
        + " was kicked by <b>"
        + adminClient.getPlayerModel().getName()
        + "</b></span>"));
        return true;
        }
        }
        }
        } */
        return false;
    }

    /**
     * A game's players changed.
     * @param game the game that changed.
     */
    @Override
    public void gamePlayersChanged(Game game) {
        Lobby.sendAll(getGamesMessage());
    }

    /**
     * A game's state changed.
     * @param game the game that changed.
     */
    @Override
    public void gameStateChanged(Game game) {
        Lobby.sendAll(getGamesMessage());
    }
}
