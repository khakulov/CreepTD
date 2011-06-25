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
package com.creeptd.common.messages.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.creeptd.common.messages.MessageUtil;

/**
 * Message from client, a creep escaped.
 * 
 * @author Daniel
 */
public class CreepEscapedMessage extends ClientMessage implements GameMessage {

    /**
     * Regular expression for message-parsing.
     */
    private static final String REGEXP = "CREEP_ESCAPED\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)";
    /**
     * Pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP);
    /**
     * Type of Creep
     */
    private String creepType;
    /**
     * ID of creep
     */
    private int creepId;
    /**
     * Health of the creep
     */
    private int creepHealth;
    /**
     * Id of Player from who the life was taken
     */
    private int fromPlayerId;
    /**
     * Id of Player, who created the creep
     */
    private int creatorId;
    /**
     * Number of Round
     */
    private long roundId;
    /** Number of transfers of this creep */
    private int transferCount = 0;

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "CREEP_ESCAPED \"" + MessageUtil.prepareToSend(this.getCreepType()) + "\" "+this.creepId+" "+this.creepHealth+ " " + this.getFromPlayerId() + " " + this.getCreatorId() + " " + this.getRoundId()+" "+this.getTransferCount();
    }

    /**
     * @param messageString The message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setCreepType(matcher.group(1));
            this.setCreepId(Integer.parseInt(matcher.group(2)));
            this.setCreepHealth(Integer.parseInt(matcher.group(3)));
            this.setFromPlayerId(Integer.parseInt(matcher.group(4)));
            this.setCreatorId(Integer.parseInt(matcher.group(5)));
            this.setRoundId(Long.parseLong(matcher.group(6)));
            this.setTransferCount(Integer.parseInt(matcher.group(7)));
        }
    }

    /**
     * @return the type of the creep
     */
    public String getCreepType() {
        return this.creepType;
    }

    /**
     * @param creepType
     *            the type of the creep
     */
    public void setCreepType(String creepType) {
        this.creepType = creepType;
    }

    public int getCreepId() {
        return creepId;
    }

    public void setCreepId(int creepId) {
        this.creepId = creepId;
    }

    public int getCreepHealth() {
        return creepHealth;
    }

    public void setCreepHealth(int creepHealth) {
        this.creepHealth = creepHealth;
    }

    /**
     * @param fromPlayerId
     *            the from player ID to set
     */
    public void setFromPlayerId(int fromPlayerId) {
        this.fromPlayerId = fromPlayerId;
    }

    /**
     * @return the from player ID
     */
    public int getFromPlayerId() {
        return this.fromPlayerId;
    }

    /**
     * @param creatorId the creator ID to set
     */
    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * @return the creator ID
     */
    public int getCreatorId() {
        return creatorId;
    }

    /**
     * @param roundId the roundId to set
     */
    public void setRoundId(long roundId) {
        this.roundId = roundId;
    }

    /**
     * @return the roundId
     */
    public long getRoundId() {
        return roundId;
    }

    public int getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }
}
