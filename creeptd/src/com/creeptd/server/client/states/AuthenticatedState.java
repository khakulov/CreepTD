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
package com.creeptd.server.client.states;

import org.apache.log4j.Logger;

import com.creeptd.common.Permission;
import com.creeptd.common.Constants.ResponseType;
import com.creeptd.common.messages.client.ClientMessage;
import com.creeptd.common.messages.client.CreateGameMessage;
import com.creeptd.common.messages.client.DeleteRequestMessage;
import com.creeptd.common.messages.client.HighscoreRequestMessage;
import com.creeptd.common.messages.client.JoinGameRequestMessage;
import com.creeptd.common.messages.client.LogoutMessage;
import com.creeptd.common.messages.client.RefreshMessage;
import com.creeptd.common.messages.client.ScoreRequestMessage;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.UpdateDataRequestMessage;
import com.creeptd.common.messages.server.CreateGameResponseMessage;
import com.creeptd.common.messages.server.DeleteResponseMessage;
import com.creeptd.common.messages.server.JoinGameResponseMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.ScoreResponseMessage;
import com.creeptd.common.messages.server.UpdateDataResponseMessage;
import com.creeptd.server.AuthenticationService;
import com.creeptd.server.HighscoreService;
import com.creeptd.server.Lobby;
import com.creeptd.server.client.Client;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.GameManager;
import com.creeptd.server.model.Player;

/**
 * This class represents the state of the client when it is logged in.
 * 
 * @author Bernd Hietler
 */
public class AuthenticatedState extends AbstractClientState {

    private static Logger logger = Logger.getLogger(AuthenticatedState.class);
    private AnonymousState anonymousState;

    /**
     * Constructor.
     *
     * @param outQueue
     *            BlockingQueue for the outgoing messages.
     * @param client
     *            Client
     * @param lobby
     *            Lobby
     * @param anonymousState
     *            the previous state.
     * @param authenticationService
     *            the AuthenticationService.
     */
    public AuthenticatedState(Client client, AnonymousState anonymousState) {
        super(client);
        this.anonymousState = anonymousState;
    }

    /**
     * Method for sending a Message.
     *
     * @param message ClientMessage
     * @return ClientState
     */
    @Override
    public AbstractClientState receiveMessage(ClientMessage message) {
        if (message == null) {
            AuthenticationService.logout(this.getClient());
            logger.info("client " + this.getClient() + " disconnected in AuthenticatedState");
            return null;
        }
        if (message instanceof UpdateDataRequestMessage) {
            UpdateDataResponseMessage m = new UpdateDataResponseMessage();
            m.setResponseType(AuthenticationService.update(this.getClient(), (UpdateDataRequestMessage) message));
            this.getClient().send(m);
            return this;
        }
        if (message instanceof DeleteRequestMessage) {
            DeleteResponseMessage m = new DeleteResponseMessage();
            m.setResponseType(AuthenticationService.delete(this.getClient()));
            this.getClient().send(m);
            return this;
        }
        if (message instanceof ClientChatMessage) {
            handleChatMessage(((ClientChatMessage) message).getMessage());
            return this;
        }
        /* if (message instanceof RefreshMessage) {
            this.getClient().send(Lobby.getCompletePlayersMessage());
            this.getClient().send(GameManager.getGamesMessage());
            return this;
        } */
        if (message instanceof ScoreRequestMessage) {
            ScoreRequestMessage requestMessage = (ScoreRequestMessage) message;
            ScoreResponseMessage responseMessage = HighscoreService.getScoreMessage(requestMessage.getPlayerName());
            this.getClient().send(responseMessage);
            return this;
        }
        if (message instanceof HighscoreRequestMessage) {
            HighscoreRequestMessage requestMessage = (HighscoreRequestMessage) message;
            this.getClient().send(HighscoreService.getHighscoreMessage(requestMessage.getStart()));
            return this;
        }
        if (message instanceof CreateGameMessage) {
            Game game = new Game(this.getClient(), (CreateGameMessage) message);
            if (game == null) {
                logger.info("Failed to create game " + ((CreateGameMessage) message).getGameName());
                this.getClient().send(new CreateGameResponseMessage(ResponseType.failed));
                return this;
            }
            this.getClient().send(new CreateGameResponseMessage(ResponseType.ok));
            game.addPlayer(this.getClient());
            return new InGameState(this.getClient(), game, this);
        }
        if (message instanceof JoinGameRequestMessage) {
            JoinGameRequestMessage jgrm = (JoinGameRequestMessage) message;
            Game game = GameManager.find(jgrm.getGameId());
            if ((game == null) || (!game.canPlayerJoin(this.getClient(), jgrm))) {
                logger.info("Failed to join to game " + jgrm.getGameId());
                this.getClient().send(new JoinGameResponseMessage(ResponseType.failed));
                return this;
            }
            logger.info("Client " + this.getClient() + " joined to game " + game);
            this.getClient().send(new JoinGameResponseMessage(ResponseType.ok));
            game.addPlayer(this.getClient());
            return new InGameState(this.getClient(), game, this);
        }
        if (message instanceof LogoutMessage) {
            AuthenticationService.logout(this.getClient());
            return this.anonymousState;
        }
        logger.error("cannot handle message: " + message);
        return this;
    }

    /**
     * Returns the AnonymousState.
     *
     * @return the AnonymousState.
     */
    public AnonymousState getAnonymousState() {
        return this.anonymousState;
    }

    /**
     * Process chat messages. If message starts with a slash, it is considered
     * to be a command.
     *
     * @param message
     *            The message to process
     */
    private void handleChatMessage(String message) {
        if (message.startsWith("/")) {
            String[] msgSplit = message.split(" ", 2);
            String param = "";
            if (msgSplit.length > 1) {
                param = msgSplit[1];
            }
            if (handleCommand(msgSplit[0], param)) {
                return;
            }
        }
        Lobby.sendAll(new ServerChatMessage(getClient().getPlayerModel().getName(), message));
    }

    /**
     * Process chat commands.
     *
     * @param command
     *            The command starting with a slash
     * @param message
     *            Optional command parameters
     * @return true if command was successfully processed, false otherwise.
     */
    private boolean handleCommand(String command, String message) {
        Player player = getClient().getPlayerModel();
        if ("/to".equalsIgnoreCase(command)) {
            String[] msgSplit = message.split(" ", 2);
            if (msgSplit.length > 1 && !player.getName().equalsIgnoreCase(msgSplit[0])) {
                Player user = AuthenticationService.getPlayer(msgSplit[0]);
                if (user != null) {
                    String messageStr = "<b>" + player.getName() + " -&gt; " + user.getName() + ": " + msgSplit[1] + "</b>";
                    if (!Lobby.sendDirectMessage(this.getClient(), user.getName(), messageStr) && !GameManager.sendDirectMessage(this.getClient(), user.getName(), messageStr)) {
                        sendServerMessage("<b>"+user.getName() + "</b> is not online.");
                    }
                } else {
                    sendServerMessage("<b>"+msgSplit[0] + "</b> is not known.");
                }
            }
            return true;
        }
        if ("/me".equalsIgnoreCase(command)) {
            ServerChatMessage scm = new ServerChatMessage();
            scm.setPlayerName(this.getClient().getPlayerName());
            scm.setMessage(message);
            scm.setAction(true);
            Lobby.sendAll(scm);
            return true;
        }
        if ("/global".equalsIgnoreCase(command) && player.hasPermission(Permission.MOD_GLOBAL)) {
            ServerChatMessage scm = new ServerChatMessage();
            scm.setPlayerName("Server");
            scm.setMessage(message);
            Lobby.sendGlobal(scm);
            return true;
        }
        if ("/kick".equalsIgnoreCase(command) && player.hasPermission(Permission.KICK)) {
            Player targetPlayer = AuthenticationService.getPlayer(message);
            if (targetPlayer != null) {
                if (targetPlayer.hasPermission(Permission.KICK_IMMUN)) {
                    sendServerMessage(message + " user can't be kicked.");
                } else {
                    Lobby.kickClient(targetPlayer, this.getClient(), false, false);
                }
            } else {
                sendServerMessage(message + " user not found.");
            }
            return true;
        }
        if ("/ban".equalsIgnoreCase(command) && player.hasPermission(Permission.BAN)) {
            Player targetPlayer = AuthenticationService.getPlayer(message);
            if (targetPlayer != null) {
                if (targetPlayer.hasPermission(Permission.BAN_IMMUNE)) {
                    sendServerMessage(targetPlayer.getName() + " user can't be banned.");
                } else {
                    Lobby.kickClient(targetPlayer, this.getClient(), true, true);
                }
            } else {
                sendServerMessage(message + " user not found.");
            }
            return true;
        }
        if ("/unban".equalsIgnoreCase(command) && player.hasPermission(Permission.UNBAN)) {
            Player targetPlayer = AuthenticationService.getPlayer(message);
            if (targetPlayer != null) {
                Lobby.unbanClient(targetPlayer, this.getClient());
            } else {
                sendServerMessage(message + " user not found.");
            }
            return true;
        }
        return false;
    }

    /**
     * Notify the current user with a system message.
     *
     * @param message The message
     */
    private void sendServerMessage(String message) {
        this.getClient().send(new ServerChatMessage("Server", message));
    }

    @Override
    public void enter(AbstractClientState oldState) {
        if (oldState != null && oldState instanceof InGameState) {
            Lobby.setIngame(this.getClient(), false);
        } else {
            Lobby.add(this.getClient());
        }
    }

    @Override
    public void leave(AbstractClientState newState) {
        if (newState != null && newState instanceof InGameState) {
            Lobby.setIngame(this.getClient(), true);
        } else {
            Lobby.remove(this.getClient());
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof AuthenticatedState;
    }
}
