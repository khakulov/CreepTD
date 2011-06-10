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

    private static final String REG_EXP =
            "PLAYER_JOINED\\s([^\\s]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private String playerName;
    private Integer playerExperience = 0;
    private Integer playerElopoints = 1500;
    private Integer playerId;

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
    public PlayerJoinedMessage(String playerName, Integer playerId, Integer playerExperience, Integer playerElopoints) {
        this.playerName = playerName;
        this.playerId = playerId;
        this.playerExperience = playerExperience;
        this.playerElopoints = playerElopoints;
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
    public Integer getPlayerExperience() {
        return this.playerExperience;
    }

    /**
     * @param playerId the playerId to set
     */
    public void setPlayerExperience(Integer playerExperience) {
        this.playerExperience = playerExperience;
    }

    public Integer getPlayerElopoints() {
        return playerElopoints;
    }

    public void setPlayerElopoints(Integer playerElopoints) {
        this.playerElopoints = playerElopoints;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_JOINED \"" + MessageUtil.prepareToSend(this.playerName) + "\" " + this.playerId + " " + this.playerExperience + " " + this.playerElopoints;
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
            this.setPlayerExperience(Integer.parseInt(matcher.group(3)));
            this.setPlayerElopoints(Integer.parseInt(matcher.group(4)));
        }

    }

    /**
     * Returns true if o is a PlayerJoinedMessage with the same contents
     * as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlayerJoinedMessage)) {
            return false;
        }
        PlayerJoinedMessage m = (PlayerJoinedMessage) o;
        return this.playerName.equals(m.getPlayerName()) && this.playerId == m.getPlayerId() && this.playerExperience == m.getPlayerExperience() && this.playerElopoints == m.getPlayerElopoints();
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return this.playerName.hashCode() ^ this.playerId;
    }
}
