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
 * Message from client, requesting an update of the player's data.
 * 
 * @author andreas
 *
 */
public class UpdateDataRequestMessage extends ClientMessage {

    /**
     * regular expression for message parsing.
     */
    private static final String REGEXP_UPDATE_DATA_REQUEST =
            "UPDATE_DATA_REQUEST\\s\"([^\"]*)\"\\s\"([^\"]*)\"\\s\"([^\"]*)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN =
            Pattern.compile(REGEXP_UPDATE_DATA_REQUEST);
    private String oldPassword;
    private String password;
    private String email;

    /**
     * @return the password
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the oldPassword
     */
    public String getOldPassword() {
        return this.oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Initializes Message with data from MessageString.
     *
     * @param messageString the messageString
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            if (matcher.group(1) != null) {
                this.setOldPassword(matcher.group(1));
            }
            if (matcher.group(2) != null) {
                this.setPassword(matcher.group(2));
            }
            if (matcher.group(3) != null) {
                this.setEmail(matcher.group(3));
            }
        }
    }

    /**
     * @return the String-representation of the message
     */
    @Override
    public String getMessageString() {
        String messageString = "UPDATE_DATA_REQUEST";
        if (this.getOldPassword() != null) {
            messageString = messageString + " \"" + MessageUtil.prepareToSend(this.getOldPassword()) + "\"";
        } else {
            messageString = messageString + " \"\"";
        }
        if (this.getPassword() != null) {
            messageString = messageString + " \"" + MessageUtil.prepareToSend(this.getPassword()) + "\"";
        } else {
            messageString = messageString + " \"\"";
        }
        if (this.getEmail() != null) {
            messageString = messageString + " \"" + MessageUtil.prepareToSend(this.getEmail()) + "\"";
        } else {
            messageString = messageString + " \"\"";
        }
        return messageString;

    }
}
