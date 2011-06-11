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

import com.creeptd.common.IConstants.ErrorType;
import com.creeptd.common.messages.MessageUtil;

/**
 * Message from server, indicating that an error has
 * occured.
 * 
 * @author andreas
 *
 */
public class ErrorMessage extends ServerMessage {

    private static final String REG_EXP = "ERROR\\s([0-9]+)\\s\"([^\"]+)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private ErrorType errorType;
    private String errorMessage;

    /**
     * No-arg constructor.
     */
    public ErrorMessage() {
        //do nothing
    }

    /**
     * Constructor that initializes the fields.
     * @param errorType the errorType
     * @param errorMessage the errorMessage
     */
    public ErrorMessage(ErrorType errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

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
     * @return the error message
     */
    public String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * @return the String representation of the message.
     */
    @Override
    public String getMessageString() {
        return "ERROR " + this.errorType.name() + " " + MessageUtil.prepareToSend(this.errorMessage) + "";
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
     * Returns true if o is an ErrorMessage with the same errorType and
     * errorMessage as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ErrorMessage)) {
            return false;
        }
        ErrorMessage m = (ErrorMessage) o;
        return this.errorType == m.getErrorType() && this.errorMessage.equals(m.getErrorMessage());
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return this.errorType.ordinal();
    }
}
