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
package com.creeptd.client.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import com.creeptd.common.messages.server.BuildCreepRoundMessage;
import com.creeptd.common.messages.server.BuildTowerRoundMessage;
import com.creeptd.common.messages.server.ChangeStrategyRoundMessage;
import com.creeptd.common.messages.server.CreateGameResponseMessage;
import com.creeptd.common.messages.server.DeleteResponseMessage;
import com.creeptd.common.messages.server.ErrorMessage;
import com.creeptd.common.messages.server.GamesMessage;
import com.creeptd.common.messages.server.HighscoreResponseMessage;
import com.creeptd.common.messages.server.JoinGameResponseMessage;
import com.creeptd.common.messages.server.KickPlayerResponseMessage;
import com.creeptd.common.messages.server.KickedMessage;
import com.creeptd.common.messages.server.LoginResponseMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.PasswordResetResponseMessage;
import com.creeptd.common.messages.server.PingMessage;
import com.creeptd.common.messages.server.PlayerGameOverMessage;
import com.creeptd.common.messages.server.PlayerJoinedMessage;
import com.creeptd.common.messages.server.PlayerLosesLifeMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.PlayersMessage;
import com.creeptd.common.messages.server.RegistrationResponseMessage;
import com.creeptd.common.messages.server.RoundMessage;
import com.creeptd.common.messages.server.ScoreResponseMessage;
import com.creeptd.common.messages.server.SellTowerRoundMessage;
import com.creeptd.common.messages.server.ServerMessage;
import com.creeptd.common.messages.server.ServerOnlineResponseMessage;
import com.creeptd.common.messages.server.StartGameMessage;
import com.creeptd.common.messages.server.StartGameResponseMessage;
import com.creeptd.common.messages.server.TransferCreepMessage;
import com.creeptd.common.messages.server.UpdateDataResponseMessage;
import com.creeptd.common.messages.server.UpgradeTowerRoundMessage;


/**
 * The InTranslator translates incoming String-messages to message objects.
 * Regular Expressions are used for translating.
 * 
 * @author andreas
 */
public class InTranslator {

    private static Logger logger = Logger.getLogger(InTranslator.class.getName());
    private BufferedReader bufferedReader;
    private String messageString = "";

    /**
     * Create InTranslator.
     * 
     * @param inputStream The input stream created from the client socket
     */
    public InTranslator(InputStream inputStream) {
        super();
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

    }

    /**
     * Get the next message from input stream.
     *
     * @return The next message
     */
    public ServerMessage getNextMessage() throws IOException {
        ServerMessage messageObject = null;
        synchronized (bufferedReader) {
            messageString = "";
            messageString = this.bufferedReader.readLine();
        }
        if (messageString == null || messageString.length() < 1) {
            System.out.println("Got an empty message from readLine(): "+messageString);
            return null;
        }

        logger.info("Received: " + messageString);

        if (BuildTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new BuildTowerRoundMessage();
        } else if (BuildCreepRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new BuildCreepRoundMessage();
        } else if (TransferCreepMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new TransferCreepMessage();
        } else if (CreateGameResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new CreateGameResponseMessage();
        } else if (PlayerLosesLifeMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerLosesLifeMessage();
        } else if (PlayerGameOverMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerGameOverMessage();
        } else if (ErrorMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ErrorMessage();
        } else if (GamesMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new GamesMessage();
        } else if (HighscoreResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new HighscoreResponseMessage();
        } else if (JoinGameResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new JoinGameResponseMessage();
        } else if (LoginResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new LoginResponseMessage();
        } else if (ServerChatMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ServerChatMessage();
        } else if (PlayerJoinedMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerJoinedMessage();
        } else if (PlayerQuitMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerQuitMessage();
        } else if (PlayersMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayersMessage();
        } else if (RegistrationResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new RegistrationResponseMessage();
        } else if (RoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new RoundMessage();
        } else if (SellTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new SellTowerRoundMessage();
        } else if (ChangeStrategyRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ChangeStrategyRoundMessage();
        } else if (StartGameMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new StartGameMessage();
        } else if (StartGameResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new StartGameResponseMessage();
        } else if (UpgradeTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new UpgradeTowerRoundMessage();
        } else if (KickedMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new KickedMessage();
        } else if (KickPlayerResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new KickPlayerResponseMessage();
        } else if (PingMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PingMessage();
        } else if (UpdateDataResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new UpdateDataResponseMessage();
        } else if (ScoreResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ScoreResponseMessage();
        } else if (DeleteResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new DeleteResponseMessage();
        } else if (PasswordResetResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PasswordResetResponseMessage();
        } else if (ServerOnlineResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ServerOnlineResponseMessage();
        } else {
            // TODO logging
            logger.warning("Invalid message received: " + messageString);
        }

        if (messageObject != null) {
            messageObject.initWithMessage(messageString);
            // System.out.println(messageObject.getMessageString());
        }

        return messageObject;
    }
}
