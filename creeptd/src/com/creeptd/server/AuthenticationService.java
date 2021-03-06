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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.creeptd.common.Constants;
import com.creeptd.common.messages.client.LoginRequestMessage;
import com.creeptd.common.messages.client.RegistrationRequestMessage;
import com.creeptd.common.messages.client.UpdateDataRequestMessage;
import com.creeptd.server.client.Client;
import com.creeptd.server.model.BlackList;
import com.creeptd.server.model.Player;

/**
 * Service for player-management. Persists player-data to the database.
 * 
 * @author andreas
 * 
 */
public class AuthenticationService {

    private static Logger logger = Logger.getLogger(AuthenticationService.class);
    private final static HashSet<Client> loggedIn = new HashSet<Client>();
    private static final String QUERY_PLAYERS_ORDERBY_POINTS = "SELECT player FROM Player AS player ORDER BY player.points DESC";
    private static final String QUERY_PLAYERS_ORDERBY_SKILL = "SELECT player FROM Player AS player ORDER BY player.skill DESC";
    private static final String QUERY_BANLIST = "SELECT * FROM BlackList WHERE data IN ";

    private AuthenticationService() {
    }

    /**
     * Validate username.
     *
     * @param name The user's name
     * @return true if valid, else false
     */
    private static boolean checkUsername(String name) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        if (name.length() > 16) return false;
        for (int i=0; i<name.length(); i++) {
            if (chars.indexOf(name.charAt(i)) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param playerName the name of the player
     * @param password the password of the player
     * @param email the email of the player
     * @return ok if successful, username if the username is in use, failed for
     *         other errors.
     */
    public static Constants.ResponseType create(RegistrationRequestMessage registrationRequestMessage) {
        if (!checkUsername(registrationRequestMessage.getUsername())) {
            logger.warn("Registration failed (invalid username): "+registrationRequestMessage.getUsername());
            return Constants.ResponseType.failed;
        }
        if (AuthenticationService.getPlayer(registrationRequestMessage.getUsername()) != null) {
            logger.error("Registration failed (username used)");
            return Constants.ResponseType.username;
        }
        if (registrationRequestMessage.getUsername().equalsIgnoreCase("Server")) {
            logger.error("Registration failed (username not allowed)");
            return Constants.ResponseType.username;
        }
        try {
            EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();

            Player player = new Player();
            player.setName(registrationRequestMessage.getUsername());
            player.setAndEncodePassword(registrationRequestMessage.getPassword());
            player.setEmail(registrationRequestMessage.getEmail());
            player.setPoints(0);
            player.setSkill(1000);

            entityManager.persist(player);
            entityManager.flush();
            entityTransaction.commit();
            logger.info("New player registered: " + player.getName());
        } catch (Exception e) {
            logger.error("Registration failed");
            logger.debug(e.getLocalizedMessage());
            return Constants.ResponseType.failed;

        }
        return Constants.ResponseType.ok;
    }

    /**
     * @param playerName
     *            the name of the player to update
     * @param oldPassword
     *            the old password
     * @param password
     *            the new password
     * @param mail
     *            the new mailadress
     * @return the response type for ResponseMessage
     */
    public static Constants.ResponseType update(Client client, UpdateDataRequestMessage updateDataRequestMessage) {
        boolean update = false;
        Constants.ResponseType responseType = Constants.ResponseType.failed;
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        Player player = entityManager.find(Player.class, client.getPlayerModel().getName());
        if (player != null) {
            String oldPassword = updateDataRequestMessage.getOldPassword();
            String password = updateDataRequestMessage.getPassword();
            String mail = updateDataRequestMessage.getEmail();
            if ((oldPassword != null) && (oldPassword.length() > 0) && (password != null) && (password.length() > 0)) {
                if (player.getPassword().equals(oldPassword)) {
                    player.setPassword(password);
                    update = true;
                }
            }
            if ((mail != null) && (mail.length() > 0)) {
                player.setEmail(mail);
                update = true;
            } else {
                logger.error("Password not changed because old password was wrong");
            }

            if (update) {
                entityManager.merge(player);
                entityManager.flush();
                responseType = Constants.ResponseType.ok;
                logger.info("Registartion data for client " + client + " changed.");
            }
        } else {
            logger.error("player with client " + client + " was not found.");
        }
        entityTransaction.commit();
        return responseType;
    }

    /**
     * @param playerName the name of the player
     * @return the response type
     */
    public static Constants.ResponseType delete(Client client) {
        Constants.ResponseType responseType = Constants.ResponseType.failed;
        /* if (client.getPlayerModel() != null) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        entityManager.remove(entityManager.find(Player.class, client.getPlayerModel().getName()));
        entityManager.flush();
        entityTransaction.commit();
        responseType = Constants.ResponseType.ok;
        } */
        return responseType;
    }

    /**
     * @param playerName
     *            the player's name
     * @param password
     *            the password
     * @return the lobby, of null if the login failed
     */
    public static boolean login(Client client, LoginRequestMessage loginRequestMessage) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        Player player = entityManager.find(Player.class, loginRequestMessage.getUsername());
        if ((player != null)) {
            synchronized (loggedIn) {
                for (Client loggedinClient : loggedIn) {
                    if (loggedinClient.getPlayerModel().getName().equalsIgnoreCase(player.getName())) {
                        loggedinClient.disconnect();
                        loggedIn.remove(loggedinClient);
                        logger.info(player.getName() + " is already logged in: Closed old connection!");
                        break;
                    }
                }
            }
            if (player.getPassword().equals(loginRequestMessage.getPassword()) && !player.isBlocked()) {
                synchronized (loggedIn) {
                    loggedIn.add(client);
                }
                logger.info(player.getName() + " logged in successfully");

                EntityTransaction entityTransaction = entityManager.getTransaction();
                entityTransaction.begin();
                player.setLastlogin(System.currentTimeMillis() / 1000L);
                player.setIp(client.getIPAddress());
                player.setMac(loginRequestMessage.getUid());
                entityManager.merge(player);
                entityManager.flush();
                entityTransaction.commit();

                client.setPlayerModel(player);
                return true;
            }
        }
        logger.info("Login failed for " + loginRequestMessage.getUsername());
        return false;
    }

    /**
     * Log out.
     *
     * @param playerName The name of the player who wants to log out.
     */
    public static void logout(Client client) {
        synchronized (loggedIn) {
            if (loggedIn.remove(client)) {
                logger.info(client + " logged out");
            }
        }
    }

    /**
     * @param firstResult The index of the first result returned.
     * @return 30 players sorted by skill/points starting with firstResult.
     */
    public static Set<Player> getPlayers(String sortBy, int firstResult) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        Query query;
        if (sortBy == null || sortBy.equals("points")) {
            query = entityManager.createQuery(QUERY_PLAYERS_ORDERBY_POINTS);
        } else {
            query = entityManager.createQuery(QUERY_PLAYERS_ORDERBY_SKILL);
        }
        query.setMaxResults(30);
        query.setFirstResult(firstResult);

        List<?> resultList = query.getResultList();

        Set<Player> players = new HashSet<Player>();
        if ((resultList != null) && (resultList.size() > 0)) {
            for (Object o : resultList) {
                Player player = (Player) o;
                players.add(player);
            }
        }
        return players;
    }

    /**
     * @param playerName The name of a player
     * @return The player with playerName
     */
    public static Player getPlayer(String playerName) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        Player player = null;
        try {
            player = entityManager.find(Player.class, playerName.toLowerCase());
        } catch (PersistenceException e) {
            logger.error("DB Connection lost ?");
        }
        return player;
    }

    public static boolean isBanned(Client client, LoginRequestMessage loginRequestMessage) {
        EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
        String queryString = QUERY_BANLIST + "('" + client.getIPAddress() + "', '" + loginRequestMessage.getUid() + "')";
        Query query = entityManager.createNativeQuery(queryString,
                BlackList.class);
        List<?> resultList = query.getResultList();
        if ((resultList != null) && (resultList.size() > 0)) {
            return true;
        }
        return false;
    }
}
