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

import com.creeptd.common.Constants.ResponseType;

/**
 * Message send from server to client as response of 
 * KickPlayerRequestMessage from client.
 */
public class KickPlayerResponseMessage extends ServerMessage {

    private static final String REG_EXP = "KICK_PLAYER_RESPONSE\\s(ok|failed)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private ResponseType responseType = ResponseType.failed;

    /**
     * Default constructor.
     */
    public KickPlayerResponseMessage() {
        super();
    }

    /**
     * @param responseType the respons type of the message
     */
    public KickPlayerResponseMessage(ResponseType responseType) {
        this.responseType = responseType;
    }

    /**
     * @return the responseType
     */
    public ResponseType getResponseType() {
        return this.responseType;
    }

    /**
     * @param responseType the responseType to set
     */
    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "KICK_PLAYER_RESPONSE " + this.responseType.name();
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setResponseType(ResponseType.valueOf(matcher.group(1)));
        }
    }

    /**
     * Returns true if o is a KickPlayerResponseMessage with the same
     * responseType as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    public boolean equals(Object o) {
        if (!(o instanceof KickPlayerResponseMessage)) {
            return false;
        }
        KickPlayerResponseMessage m = (KickPlayerResponseMessage) o;
        return this.responseType == m.getResponseType();
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    public int hashCode() {
        return (int) this.responseType.ordinal();
    }
}
