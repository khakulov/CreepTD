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
 * Message from server to client, containing a message from a player.
 * 
 * @author andreas
 *
 */
public class ServerChatMessage extends ServerMessage {

    private static final String REG_EXP = "MSG\\s\"([^\"]+)\"\\s\"([^\"]+)\"\\s(0|1)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private String playerName;
    private String message;
    private Boolean translate = false;

    /**
     * Default constructor.
     */
    public ServerChatMessage() {
        super();
    }

    /**
     * @param playerName the name of the sender
     * @param message the message
     */
    public ServerChatMessage(String playerName, String message, Boolean translate) {
        super();
        this.playerName = playerName;
        this.message = message;
        this.translate = translate;
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
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getTranslate() {
        return translate;
    }

    public void setTranslate(Boolean translate) {
        this.translate = translate;
    }

    /**
     * @return the String representation of the message.
     */
    @Override
    public String getMessageString() {
        return "MSG \"" + MessageUtil.prepareToSend(this.playerName) + "\" \"" + MessageUtil.prepareToSend(this.message) + "\" "+(this.translate ? "1" : "0");
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setPlayerName(matcher.group(1));
            this.setMessage(matcher.group(2));
            this.setTranslate(matcher.group(3).equals("1"));
        }

    }

    /**
     * Returns true if o is a ServerChatMessage with the same contents
     * as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ServerChatMessage)) {
            return false;
        }
        ServerChatMessage m = (ServerChatMessage) o;
        return this.playerName.equals(m.getPlayerName()) && this.message.equals(m.getMessage()) && this.translate.equals(m.getTranslate());
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return this.playerName.hashCode() ^ this.message.hashCode() ^ this.translate.hashCode();
    }
}
