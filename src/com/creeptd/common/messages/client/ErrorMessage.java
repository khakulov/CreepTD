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

import com.creeptd.common.Constants.ErrorType;
import com.creeptd.common.messages.MessageUtil;

/**
 * Message from client, indicating that an error has
 * occured.
 * 
 * @author andreas
 *
 */
public class ErrorMessage extends ClientMessage {

    /**
     * 	regular expression for message-parsing.
     */
    private static final String REGEXP_ERROR =
            "ERROR\\s([^\\s]+)\\s\"([^\"]+)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP_ERROR);
    private ErrorType errorType;
    private String errorMessage;

    /**
     * @return the errorType
     */
    public ErrorType getErrorType() {
        return this.errorType;
    }

    /**
     * @param errorType the errorType to set
     */
    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    /**
     * @return the message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage the message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setErrorType(ErrorType.valueOf(matcher.group(1)));
            this.setErrorMessage(matcher.group(2));
        }

    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "ERROR " + this.getErrorType() + " \"" + MessageUtil.prepareToSend(this.getErrorMessage()) + "\"";
    }
}
