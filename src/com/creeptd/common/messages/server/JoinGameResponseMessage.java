/**
Creep Smash, a multiplayer towerdefence game
created as a project at the Hochschule fuer
Technik Stuttgart (University of Applied Science)
http://www.hft-stuttgart.de 

Copyright (C) 2008 by      
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
 * Response message for JoinGameRequestMessage.
 *  
 * @author andreas
 *
 */
public class JoinGameResponseMessage extends ServerMessage {
	private static final long serialVersionUID = -7639279691630002381L;

	/**
     * regular expression.
     */
    private static final String REG_EXP = "JOIN_GAME_RESPONSE\\s(ok|failed|multi)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private ResponseType responseType = ResponseType.failed;

    /**
     * No-arg constructor.
     */
    public JoinGameResponseMessage() {
        super();
    }

    /**
     * Constructor that initializes the field.
     * @param responseType the response type for the message.
     */
    public JoinGameResponseMessage(ResponseType responseType) {
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
     * @return the String representation of the message.
     */
    @Override
    public String getMessageString() {
        return "JOIN_GAME_RESPONSE " + this.responseType.name();
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
}
