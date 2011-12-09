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
package com.creeptd.common.messages.server;

import java.io.Serializable;

import com.creeptd.common.messages.Message;

/**
 * Messages from server to client.
 * 
 * @author andreas
 *
 */
public abstract class ServerMessage extends Message implements Serializable {

	private static final long serialVersionUID = 7173723370665337868L;

	public static ServerMessage renderMessageString(String messageString) {
        ServerMessage messageObject = null;

        if (BuildTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new BuildTowerRoundMessage();
        } else if (BuildCreepRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new BuildCreepRoundMessage();
        } else if (TransferCreepMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new TransferCreepMessage();
        } else if (PlayerLosesLifeMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerLosesLifeMessage();
        } else if (PlayerGameOverMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PlayerGameOverMessage();
        } else if (ErrorMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ErrorMessage();
        } else if (GamesMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new GamesMessage();
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
        } else if (RoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new RoundMessage();
        } else if (SellTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new SellTowerRoundMessage();
        } else if (ChangeStrategyRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ChangeStrategyRoundMessage();
        } else if (StartGameMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new StartGameMessage();
        } else if (UpgradeTowerRoundMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new UpgradeTowerRoundMessage();
        } else if (PausedMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PausedMessage();
        } else if (KickedMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new KickedMessage();
        } else if (PingMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new PingMessage();
        } else if (ServerOnlineResponseMessage.PATTERN.matcher(messageString).matches()) {
            messageObject = new ServerOnlineResponseMessage();
        }

        if (messageObject != null) {
            messageObject.initWithMessage(messageString);
        }

        return messageObject;
    }
}
