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

import java.util.Hashtable;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PlayersMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.GameManager;
import com.creeptd.server.model.BlackList;
import com.creeptd.server.model.Player;
import java.util.ArrayList;

/**
 * The lobby manages games and clients who are authenticated but not currently
 * playing.
 */
public class Lobby {
	private static Logger logger = Logger.getLogger(Lobby.class);
	private static List<Client> clients = new LinkedList<Client>();

	private Lobby() {}

	/**
	 * Join the lobby.
	 * @param newClient the client who wants to join the lobby.
	 */
	public static void add(Client client) {
		if (client == null)
			throw new IllegalArgumentException("'newClient' was null");
		synchronized(clients) {
			if (clients.contains(client)) return;
                        clients.add(client);
		}
		client.send(GameManager.getGamesMessage());
		sendAll(getPlayersMessage());
		logger.info("client enter the lobby: " + client);
	}

	/**
	 * Allows a client to leave the lobby.
	 * @param client the client. Must not be null.
	 */
	public static void remove(Client client) {
		if (client == null)
			throw new IllegalArgumentException("'client' was null");
		synchronized(clients) {
			if (!clients.contains(client))
				return;
			clients.remove(client);
		}
		sendAll(getPlayersMessage());
		logger.info("client left the lobby: " + client);
	}

	public static Client find(String username) {
		if ((username==null) || username.equals(""))
			throw new IllegalArgumentException("'username' was null or empty");
		synchronized(clients) {
			for (Client client : clients) {
				if (client.getPlayerModel().getName().equalsIgnoreCase(username)) {
					return client;
				}
			}
		}
		return null;
	}

	/**
	 * Send a message to all clients in the lobby.
	 * @param message the message to send.
	 */
	public static void sendAll(ServerMessage message) {
		if (message == null)
			throw new IllegalArgumentException("'message' was null");
		synchronized(clients) {
			for (Client client : clients) {
				client.send(message);
			}
		}
	}

	/**
	 * Creates a PLAYERS message with the current list of clients.
	 * @return the message
	 */
	public static PlayersMessage getPlayersMessage() {
		Hashtable<String, List<Integer>> playerNames = new Hashtable<String, List<Integer>>();
		List<Client> clientsToRemove = new LinkedList<Client>();
		synchronized(clients) {
			for (Client client : clients) {
				if (client.check()) {
                                   List l = new ArrayList();
                                   l.add(client.getPlayerModel().getExperience());
                                   l.add(client.getPlayerModel().getElopoints());
					playerNames.put(client.getPlayerModel().getName(), l);
				} else {
					logger.info("PlayersMessage/leaveLobby: " + client);
					clientsToRemove.add(client);
				}
			}
			for (Client client : clientsToRemove) {
				clients.remove(client);
			}
		}
		return new PlayersMessage(playerNames);
	}

	/**
	 * 
	 * @param sender
	 * @param receiverName
	 * @param message
	 * @return boolean
	 */
	public static boolean sendDirectMessage(Client sender, String receiverName, String message) {
		synchronized(clients) {
			for (Client client : clients) {
				if (client.getPlayerModel().getName().equalsIgnoreCase(receiverName)) {
					client.send(new ServerChatMessage("Server", message));
					sender.send(new ServerChatMessage("Server", message));
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Find the Player and Kick/Ban from Server
	 * @param player the name to look for.
	 * @param adminClient Moderator Client
	 * @param banUser the user account
	 * @param banUserAndMac the user account and add MAC to the Blacklist
	 * @return boolean
	 * @author contex
	 */
	public static boolean kickClient(Player player, Client adminClient, boolean banUser, boolean banUserAndMac) {
		boolean kik = GameManager.kickClient(player.getName(), adminClient);
		if (kik == false) {
			Client kickedClient = null;
			synchronized(clients) {
				for (Client client : clients) {
					if (client.getPlayerModel().getName().equalsIgnoreCase(player.getName())) {
						kickedClient = client;
						break;
					}
				}
			}
			if (kickedClient != null) {
				logger.info("Kick Player inLobby: " + kickedClient);
				sendAll(new ServerChatMessage("Server", "<span style=\"color:red;\">"
								+ kickedClient.getPlayerModel().getName()
								+ " was kicked by <b>"
								+ adminClient.getPlayerModel().getName() + "</b></span>"));
				kickedClient.disconnect();
				kik = true;
			}
		}
		if (kik == false) {
			adminClient.send(new ServerChatMessage("Server",
					"<span style=\"color:red;\"> " + player.getName() + " is not Online!</span>"));
		} 
		if (banUser == true) {
				player.setBlocked(true);
				try {
				EntityManager entityManager = 
					PersistenceManager.getInstance().getEntityManager();
				EntityTransaction entityTransaction = entityManager
					.getTransaction();
				entityTransaction.begin();	
				entityManager.merge(player);
				entityManager.flush();
				entityTransaction.commit();	
				adminClient.send(new ServerChatMessage("Server","<span style=\"color:red;\">"
													+ player.getName()
													+ " (Account) has been banned!</span>"));
				
				logger.debug("Block for User " + player.getName() + " / "
						+ player.getMac() + " saved.");
			} catch (Throwable t) {
				logger.error("error while saving block for User "
						+ player.getName() + " / " + player.getMac() + " ",	t);
			}
		}
		if (banUserAndMac == true) {
				adminClient.send(new ServerChatMessage("Server","<span style=\"color:red;\">"
						+ player.getName()
						+ " (Mac) has been blacklisted!</span>"));
				
				try {
					EntityManager entityManager = PersistenceManager
							.getInstance().getEntityManager();
					EntityTransaction entityTransaction = entityManager
							.getTransaction();
					entityTransaction.begin();
					BlackList blacklist = new BlackList();
					blacklist.setData(player.getMac());
					entityManager.persist(blacklist);
					entityManager.flush();
					entityTransaction.commit();

					logger.debug("Block for MAC " + player.getName() + " / "
							+ player.getMac() + " saved.");
				} catch (Throwable t) {
					logger.error("error while saving block for MAC "
							+ player.getName() + " / " + player.getMac() + " ",	t);
				}
		}
		return kik;
	}
	/**
	 * Find the Player and unBan and Remove from Blacklist
	 * @param player the name to look for.
	 * @param adminClient Moderator Client
	 * @return boolean
	 * @author Contex
	 */
	public static boolean unBanClient(Player player, Client adminClient) {
		boolean retrun = false;
		try {
			EntityManager entityManager = PersistenceManager.getInstance().getEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();
			BlackList blacklist = entityManager.find(BlackList.class, player.getMac());

			if (blacklist != null) {
				entityManager.remove(blacklist);
				adminClient.send(new ServerChatMessage("Server","<span style=\"color:red;\">"
						+ player.getName()
						+ " (Mac) has been unbanned !</span>"));
				retrun = true;
			}
			entityTransaction.commit();
		} catch (Throwable t) {
			logger.error("error while remove block for User "
					+ player.getName() + " / " + player.getMac() + " ",	t);
		}
		
		try {
			player.setBlocked(false);
			EntityManager entityManager = 
				PersistenceManager.getInstance().getEntityManager();
			EntityTransaction entityTransaction = entityManager
				.getTransaction();
			entityTransaction.begin();	
			entityManager.merge(player);
			entityManager.flush();
			entityTransaction.commit();	
			adminClient.send(new ServerChatMessage("Server","<span style=\"color:red;\">"
					+ player.getName()
					+ " (User) has been unbanned !</span>"));
			
			logger.debug("undBlock for user " + player.getName() + " / "
					+ player.getMac() + " removed.");
		} catch (Throwable t) {
			logger.error("error while saving undBlock for User "
					+ player.getName() + " / " + player.getMac() + " ",	t);
		}
		return retrun;
	}
}