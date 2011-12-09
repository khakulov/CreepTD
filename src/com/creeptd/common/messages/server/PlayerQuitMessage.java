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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creeptd.common.messages.MessageUtil;

/**
 * Message send from server to client, if a player quits a game.
 * 
 * @author andreas
 *
 */
public class PlayerQuitMessage extends ServerMessage {
	private static final long serialVersionUID = 7145783239764797240L;

	private static final String REG_EXP =
            "PLAYER_QUIT\\s\"([^\"]+)\"\\s([0-9]+)\\s\"([^\"]*)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private String playerName;
    private int playerID = 0;
    private String reason;

    /**
     * default constructor.
     */
    public PlayerQuitMessage() {
        super();
    }

    /**
     * @param playerName the name of the player
     * @param reason the reason for quitting
     */
    public PlayerQuitMessage(String playerName, String reason, int playerID) {
        this.playerName = playerName;
        this.reason = reason;
        this.playerID = playerID;
    }

    /**
     * @return the playerID
     */
    public int getPlayerID() {
        return this.playerID;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return this.playerName;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_QUIT \"" + MessageUtil.prepareToSend(this.playerName) + "\" " + this.playerID + " \"" + MessageUtil.prepareToSend(this.reason) + "\"";
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerName(matcher.group(1));
            this.setPlayerID(Integer.parseInt(matcher.group(2)));
            this.setReason(matcher.group(3));
        }
    }
}
