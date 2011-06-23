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

/**
 * Asynchronous message sent by the client on async detection.
 *
 * Currently, this message is just a hint for the server. Later, maybe, we could
 * resync the client, when it is required.
 * 
 * @author Daniel
 */
public class AsyncronousMessage extends ClientMessage implements GameMessage {
    private static final String REGEXP = "ASYNC\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)";
    public static final Pattern PATTERN = Pattern.compile(REGEXP);

    /** The current round id */
    private long currentRoundId = 0;
    /** The max round id received */
    private long receivedRoundId = 0;

    /**
     * Get the current round id.
     *
     * @return Current round id
     */
    public long getCurrentRoundId() {
        return currentRoundId;
    }

    /**
     * Set the current round id.
     *
     * @param currentRoundId Current round id
     */
    public void setCurrentRoundId(long currentRoundId) {
        this.currentRoundId = currentRoundId;
    }

    /**
     * Get received round id.
     *
     * @return The maximum received round id
     */
    public long getReceivedRoundId() {
        return receivedRoundId;
    }

    /**
     * Set received round id.
     *
     * @param maxRoundId The maximum received round id
     */
    public void setReceivedRoundId(long receivedRoundId) {
        this.receivedRoundId = receivedRoundId;
    }

    /**
     * {@inheritDoc}
     */
    public String getMessageString() {
        return "ASYNC "+this.getClientId()+" "+this.currentRoundId+" "+this.receivedRoundId;
    }

    /**
     * {@inheritDoc}
     */
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setClientId(Integer.valueOf(matcher.group(1)));
            this.setCurrentRoundId(Long.parseLong(matcher.group(2)));
            this.setReceivedRoundId(Long.parseLong(matcher.group(3)));
        }
    }
}
