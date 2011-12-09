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
 * Message send from server to client, if a new player joined.
 * 
 * @author andreas
 *
 */
public class PlayerJoinedMessage extends ServerMessage {
	private static final long serialVersionUID = 6936455586221007709L;

	private static final String REG_EXP =
            "PLAYER_JOINED\\s([^\\s]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s(0|1)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private String playerName;
    private Integer playerPoints = 0;
    private Integer playerSkill = 1500;
    private Integer playerId;
    private boolean spectate = false;

    /**
     * Default constructor.
     */
    public PlayerJoinedMessage() {
        super();
    }

    /**
     * @param playerName the name of the player
     * @param playerId the id of the player
     */
    public PlayerJoinedMessage(String playerName, Integer playerId, Integer points, Integer skill, boolean spectate) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.playerPoints = points;
        this.playerSkill = skill;
        this.spectate = spectate;
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
     * @return the playerId
     */
    public Integer getPlayerId() {
        return this.playerId;
    }

    /**
     * @param playerId the playerId to set
     */
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    /**
     * @return the playerId
     */
    public Integer getPlayerPoints() {
        return this.playerPoints;
    }

    /**
     * @param playerId the playerId to set
     */
    public void setPlayerPoints(Integer points) {
        this.playerPoints = points;
    }

    public Integer getPlayerSkill() {
        return playerSkill;
    }

    public void setPlayerSkill(Integer skill) {
        this.playerSkill = skill;
    }

    public boolean isSpectate() {
        return spectate;
    }

    public void setSpectate(boolean spectate) {
        this.spectate = spectate;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_JOINED \"" + MessageUtil.prepareToSend(this.playerName) + "\" " + this.playerId + " " + this.playerPoints + " " + this.playerSkill+" "+ (this.spectate ? "1" : "0");
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerName(matcher.group(1).replace("\"", ""));
            this.setPlayerId(Integer.parseInt(matcher.group(2)));
            this.setPlayerPoints(Integer.parseInt(matcher.group(3)));
            this.setPlayerSkill(Integer.parseInt(matcher.group(4)));
            this.setSpectate(matcher.group(5).equals("1"));
        }

    }
}
