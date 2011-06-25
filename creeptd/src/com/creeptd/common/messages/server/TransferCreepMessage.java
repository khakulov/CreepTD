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
 * Message send from server to client, when a creep is transfered.
 *
 * @author andreas
 *
 */
public class TransferCreepMessage extends RoundMessage implements GameMessage {
    private static final String REG_EXP = "CREEP_TRANSFERED\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private int playerId;
    private int fromId;
    private int creatorId;
    private int creepId;
    private String creepType;
    private int creepHealth;
    private int transferCount;

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return super.hashCode() ^ this.getPlayerId() ^ this.fromId ^ this.creatorId ^ this.creepId ^ this.creepType.hashCode() ^ this.creepHealth ^ this.transferCount;
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

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
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

    public int getCreepHealth() {
        return creepHealth;
    }

    public void setCreepHealth(int creepHealth) {
        this.creepHealth = creepHealth;
    }
    
    public int getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(int transferCount) {
        this.transferCount = transferCount;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "CREEP_TRANSFERED "+this.getRoundId()+" "+this.playerId+" "+this.fromId+" "+this.creatorId+" "+this.creepId+" \""+this.creepType+"\" "+this.creepHealth+" "+this.transferCount;
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
            this.setFromId(Integer.parseInt(matcher.group(3)));
            this.setCreatorId(Integer.parseInt(matcher.group(4)));
            this.setCreepId(Integer.parseInt(matcher.group(5)));
            this.setCreepType(matcher.group(6));
            this.setCreepHealth(Integer.parseInt(matcher.group(7)));
            this.setTransferCount(Integer.parseInt(matcher.group(8)));
        }
    }

    /**
     * Returns true if o is a PlayerLosesLifeMessage with the same attributes
     * as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TransferCreepMessage)) {
            return false;
        }
        TransferCreepMessage m = (TransferCreepMessage) o;
        return this.getRoundId() == m.getRoundId() && this.playerId == m.getPlayerId() && this.fromId == m.getFromId() && this.creatorId == m.getCreatorId() && this.creepId == m.getCreepId() && this.creepType.equals(m.getCreepType()) && this.creepHealth == m.getCreepHealth() && this.transferCount == m.getTransferCount();
    }
}
