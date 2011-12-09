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


/**
 * Message send from server to client, if a player quits a game.
 *
 * @author andreas
 *
 */
public class PlayerLosesLifeMessage extends ServerMessage implements GameMessage {

	private static final long serialVersionUID = 1987010291279318203L;

	private static final String REG_EXP = "PLAYER_LOSES_LIFE\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s\"([^\"]+)\"\\s([0-9]+)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private long roundId;
    private int playerId;
    private int creatorId;
    private String creepType;
    private int creepId;
    private int lifes;

    /**
     * @return the roundId
     */
    public Long getRoundId() {
        return this.roundId;
    }

    /**
     * @param roundId the roundId to set
     */
    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the playerID
     */
    public Integer getPlayerId() {
        return this.playerId;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getCreepId() {
        return creepId;
    }

    public void setCreepId(int creepId) {
        this.creepId = creepId;
    }

    public String getCreepType() {
        return creepType;
    }

    public void setCreepType(String creepType) {
        this.creepType = creepType;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "PLAYER_LOSES_LIFE "+this.roundId+" "+this.playerId+" "+this.creatorId+" "+this.creepId+" \""+this.creepType+"\" "+this.lifes;
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setRoundId(Long.parseLong(matcher.group(1)));
            this.setPlayerId(Integer.parseInt(matcher.group(2)));
            this.setCreatorId(Integer.parseInt(matcher.group(3)));
            this.setCreepId(Integer.parseInt(matcher.group(4)));
            this.setCreepType(matcher.group(5));
            this.setLifes(Integer.parseInt(matcher.group(6)));
        }
    }
}
