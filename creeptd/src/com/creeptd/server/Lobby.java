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
package com.creeptd.server;

import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayersMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.GameManager;
import com.creeptd.server.model.BlackList;
import com.creeptd.server.model.Player;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The game lobby.
 *
 * Manages player who are connected but currently not in a game.
 */
public class Lobby {

    /** Logging functionality */
    private static Logger logger = Logger.getLogger(Lobby.class);

    /** List of clients in lobby (sort key => Client)) */
    private static final SortedMap<String,Client> allClients = new TreeMap<String,Client>();

    /** Create the lobby */
    private Lobby() {
    }
    
    /**
     * Join the lobby.
     *
     * @param newClient The client who wants to join the lobby.
     */
    public static void add(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Argument 'client' was null");
        }
        // Initialize client list
        String clientKey = client.getPlayerModel().getName().toLowerCase();
        synchronized (allClients) {
            if (allClients.containsKey(clientKey)) {
                return;
            }
            allClients.put(clientKey, client);
        }
        client.send(GameManager.getGamesMessage());
        PlayersMessage pm = new PlayersMessage();
        pm.addPlayer("join", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
        sendAllExcept(pm, client); // The client will REFRESH
        logger.info("Client enters the lobby: " + client);
    }

    public static void setIngame(Client client, boolean inGame) {
        PlayersMessage pm = new PlayersMessage();
        pm.addPlayer((inGame) ? "ingame" : "outgame", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
        sendAll(pm);
    }

    /**
     * Remove a client from the lobby.
     *
     * @param client The leaving client
     */
    public static void remove(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Argument 'client' was null");
        }
        String clientKey = client.getPlayerName().toLowerCase();
        synchronized (allClients) {
            if (!allClients.containsKey(clientKey)) {
                return;
            }
            allClients.remove(clientKey);
        }
        PlayersMessage pm = new PlayersMessage();
        pm.addPlayer("leave", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
        sendAll(pm);
        logger.info("Client left the lobby: " + client);
    }

    /**
     * Find client by player name.
     * 
     * @param name The player's name
     */
    public static Client findClientByName(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("Argument 'name' was null or empty");
        }
        String clientKey = name.toLowerCase();
        return allClients.get(clientKey);
    }

    /**
     * Send a message to all clients in the lobby.
     *
     * @param message The message to send
     */
    public static void sendAll(ServerMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null");
        }
        synchronized (allClients) {
            for (Client c : allClients.values()) {
                if (!c.isInGame()) {
                    c.send(message);
                }
            }
        }
    }

    /**
     * Send a message to all clients in the lobby except to the specified
     * client.
     *
     * @param message The message to send
     */
    public static void sendAllExcept(ServerMessage message, Client client) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null");
        }
        synchronized (allClients) {
            for (Client c : allClients.values()) {
                if (!c.equals(client) && !c.isInGame()) {
                    c.send(message);
                }
            }
        }
    }

    /**
     * Send a message to all clients in the game.
     *
     * @param message The message to send
     */
    public static void sendGlobal(ServerMessage message) {
        if (message == null) {
            throw new IllegalArgumentException("'message' was null");
        }
        synchronized (allClients) {
            for (Client c : allClients.values()) {
                c.send(message);
            }
        }
    }

    /**
     * Create a PLAYERS message with the current list of clients.
     *
     * @return The PLAYERS message
     */
    public static PlayersMessage getCompletePlayersMessage() {
        List<Client> clientsToRemove = new LinkedList<Client>();
        PlayersMessage pm = new PlayersMessage();
        synchronized (allClients) {
            for (Client client : allClients.values()) {
                if (client.doCheck()) {
                    pm.addPlayer("add", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
                    if (client.isInGame()) {
                        pm.addPlayer("ingame", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
                    }
                } else {
                    logger.info("There has been a problem with client "+client+" (leaves lobby now)");
                    clientsToRemove.add(client);
                    pm.addPlayer("remove", client.getId(), client.getPlayerName(), client.getPlayerModel().getPoints(), client.getPlayerModel().getSkill());
                }
            }
        }
        for (Client client : clientsToRemove) {
            allClients.remove(client.getPlayerModel().getName().toLowerCase());
        }
        return pm;
    }

    /**
     * Send a direct message to the client in the lobby.
     *
     * @param sender The message's sender
     * @param receiverName The receiver's name
     * @param message The message to send
     * @return boolean true on sent, else false (user not known)
     */
    public static boolean sendDirectMessage(Client sender, String receiverName, String message) {
        String clientKey = receiverName.toLowerCase();
        synchronized (allClients) {
            Client receiver = allClients.get(clientKey);
            if (receiver != null) {
                ServerMessage sm = new ServerChatMessage("Server", message);
                receiver.send(sm);
                sender.send(sm);
                return true;
            }
        }
        return false;
    }

    /**
     * Kick (Ban/Blacklist) a player.
     *
     * @param player The player's name.
     * @param adminClient Moderator Client
     * @param banUser Flag if the user is to be banned
     * @param banUserAndMac Flag if the user's MAC is to be banned
     * @return boolean true if kicked, else false
     */
    public static boolean kickClient(Player player, Client adminClient, boolean doBanUser, boolean doBanMac) {
        boolean kicked = GameManager.kickClient(player.getName(), adminClient); // Try to kick from game first
        if (kicked == false) {
            String clientKey = player.getName().toLowerCase();
            synchronized (allClients) {
                Client client = allClients.get(clientKey);
                if (client != null) {
                    logger.info("Player kicked: "+client);
                    client.disconnect();
                    kicked = true;
                    sendAll(new ServerChatMessage("Server", "<b>" + client.getPlayerName() + "</b> has been kicked by <b>" + adminClient.getPlayerName() + "</b>"));
                }
            }
        }
        if (!kicked) {
            adminClient.send(new ServerChatMessage("Server", "<b>"+player.getName()+"</b> is not online!"));
            return kicked;
        }
        if (doBanUser == true) {
            player.setBlocked(true);
            try {
                EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();
                entityTransaction.begin();
                entityManager.merge(player);
                entityManager.flush();
                entityTransaction.commit();
                adminClient.send(new ServerChatMessage("Server", "<b>" + player.getName() + "</b> (Account) has been banned!"));
                logger.debug("Player " + player.getName() + " / " + player.getMac() + " has been banned");
            } catch (Throwable t) {
                logger.error("Error while banning player " + player.getName() + " / " + player.getMac(), t);
            }
        }
        if (doBanMac == true) {
            adminClient.send(new ServerChatMessage("Server", "<b>" + player.getName() + "</b> (Mac) has been blacklisted!"));
            try {
                EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
                EntityTransaction entityTransaction = entityManager.getTransaction();
                entityTransaction.begin();
                BlackList blacklist = new BlackList();
                blacklist.setData(player.getMac());
                entityManager.persist(blacklist);
                entityManager.flush();
                entityTransaction.commit();
                logger.debug("Player " + player.getName() + " / " + player.getMac() + " has been blacklisted (MAC)");
            } catch (Throwable t) {
                logger.error("Error while blacklisting MAC of player " + player.getName() + " / " + player.getMac(), t);
            }
        }
        return kicked;
    }

    /**
     * Unban a client.
     *
     * @param player The player
     * @param adminClient The administrator
     * @return boolean return true of unbaned, else false
     */
    public static boolean unbanClient(Player player, Client adminClient) {
        boolean retrun = false;
        try {
            EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            BlackList blacklist = entityManager.find(BlackList.class, player.getMac());
            if (blacklist != null) {
                entityManager.remove(blacklist);
                adminClient.send(new ServerChatMessage("Server", "<b>" + player.getName() + "</b> (Mac) has been removed from the blacklist!"));
                retrun = true;
            }
            entityTransaction.commit();
        } catch (Throwable t) {
            logger.error("Error while unblacklisting user " + player.getName() + " / " + player.getMac(), t);
        }

        try {
            player.setBlocked(false);
            EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            entityManager.merge(player);
            entityManager.flush();
            entityTransaction.commit();
            adminClient.send(new ServerChatMessage("Server", "<b>" + player.getName() + "</b> (User) has been unbanned!</span>"));
            logger.debug("Unbanned user " + player.getName() + " / " + player.getMac());
        } catch (Throwable t) {
            logger.error("Error while unbanning user " + player.getName() + " / " + player.getMac(), t);
        }
        return retrun;
    }
}
