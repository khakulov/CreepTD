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
 * Message from server to clients, when a creep is build.
 * 
 * @author andreas
 * 
 */
public class BuildCreepRoundMessage extends ServerMessage implements
        GameMessage {

    /**
     * Regular expression for this message.
     */
    private static final String REG_EXP =
            "ROUND\\s([0-9]+):\\sPLAYER\\s([0-9]+)\\s" + "GETS\\sCREEP\\s\"([^\"]+)\"\\sFROM\\s([0-9]+)";
    /**
     * Pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private Long roundId;
    private Integer playerId;
    private Integer senderId;
    private String creepType;

    /**
     * @return the roundId
     */
    public Long getRoundId() {
        return this.roundId;
    }

    /**
     * @param roundId
     *            the roundId to set
     */
    public void setRoundId(Long roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the playerId
     */
    public Integer getPlayerId() {
        return this.playerId;
    }

    /**
     * @param playerId
     *            the playerId to set
     */
    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    /**
     * @return the senderId
     */
    public Integer getSenderId() {
        return this.senderId;
    }

    /**
     * @param senderId
     *            the senderId to set
     */
    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the creepType
     */
    public String getCreepType() {
        return this.creepType;
    }

    /**
     * @param creepType
     *            the creepType to set
     */
    public void setCreepType(String creepType) {
        this.creepType = creepType;
    }

    /**
     * @return the String representing the message.
     */
    @Override
    public String getMessageString() {
        return "ROUND " + this.roundId + ": PLAYER " + this.playerId + " GETS CREEP \"" + MessageUtil.prepareToSend(this.creepType) + "\" FROM " + this.senderId;
    }

    /**
     * @param messageString
     *            the messageString to fill message with.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setRoundId(Long.parseLong(matcher.group(1)));
            this.setPlayerId(Integer.parseInt(matcher.group(2)));
            this.setCreepType(matcher.group(3));
            this.setSenderId(Integer.parseInt(matcher.group(4)));
        }

    }

    /**
     * Returns true if o is a BuildCreepRoundMessage with the same attributes as
     * this one.
     *
     * @param o the Object to test equals for.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BuildCreepRoundMessage)) {
            return false;
        }
        BuildCreepRoundMessage m = (BuildCreepRoundMessage) o;
        return this.roundId == m.getRoundId() && this.playerId == m.getPlayerId() && this.senderId == m.getSenderId() && this.creepType.equals(m.getCreepType());
    }

    /**
     * Returns a hash code for this object.
     *
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return (int) (this.roundId ^ this.playerId ^ this.senderId);
    }
}
