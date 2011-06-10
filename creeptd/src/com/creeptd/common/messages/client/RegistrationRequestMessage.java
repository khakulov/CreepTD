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
 * Message from client, requesting a registration of a new user.
 * 
 * @author andreas
 *
 */
public class RegistrationRequestMessage extends ClientMessage {

    /**
     * regular expression for message parsing.
     */
    private static final String REGEXP_REGISTRATION_REQUEST =
            "REGISTRATION_REQUEST\\s\"([^\"]+)\"\\s\"([^\"]+)\"\\s\"([^\"]*)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN =
            Pattern.compile(REGEXP_REGISTRATION_REQUEST);
    private String username;
    private String password;
    private String email;

    /**
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

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
            this.setUsername(matcher.group(1));
            this.setPassword(matcher.group(2));
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
        String messageString = "REGISTRATION_REQUEST \"" + MessageUtil.prepareToSend(this.getUsername()) + "\" \"" + MessageUtil.prepareToSend(this.getPassword()) + "\"";
        if (this.getEmail() != null) {
            messageString = messageString + " \"" + MessageUtil.prepareToSend(this.getEmail()) + "\"";
        }
        return messageString;

    }
}
