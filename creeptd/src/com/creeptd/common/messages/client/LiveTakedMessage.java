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
 * Message from client, taked a live.
 * 
 * @author Azim
 */
public class LiveTakedMessage extends ClientMessage implements GameMessage {

    /**
     * Regular expression for message-parsing.
     */
    private static final String REGEXP = "LIVE_TAKED\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)";
    /**
     * Pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP);
    /**
     * Type of Creep
     */
    private String creepType;
    /**
     * Id of Player from who the live was taked
     */
    private int fromPlayerId;
    /**
     * Id of Player to the creep transfered
     */
    private int toPlayerId;
    /**
     * Id of Player, who created the creep
     */
    private int senderId;
    /**
     * Number of Round
     */
    private long roundId;

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "LIVE_TAKED \"" + MessageUtil.prepareToSend(this.getCreepType()) + "\" " + this.getFromPlayerId() + " " + this.getToPlayerId() + " " + this.getSenderId() + " " + this.getRoundId();
    }

    /**
     * @param messageString
     *            the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setCreepType(matcher.group(1));
            this.setFromPlayerId(Integer.parseInt(matcher.group(2)));
            this.setToPlayerId(Integer.parseInt(matcher.group(3)));
            this.setSenderId(Integer.parseInt(matcher.group(4)));
            this.setRoundId(Long.parseLong(matcher.group(5)));
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
     * @param toPlayerId
     *            the to Player ID to set
     */
    public void setToPlayerId(int toPlayerId) {
        this.toPlayerId = toPlayerId;
    }

    /**
     * @return the to Player ID
     */
    public int getToPlayerId() {
        return toPlayerId;
    }

    /**
     * @param senderId
     *            the sender ID to set
     */
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the sender ID
     */
    public int getSenderId() {
        return senderId;
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
}
