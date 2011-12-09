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
 * Message from client to test server availability.
 *
 * @author Daniel
 *
 */
public class ServerOnlineResponseMessage extends ServerMessage {
	private static final long serialVersionUID = 1373148078308382389L;

	private boolean correct_version = true;

    /**
     * 	regular expression for message-parsing.
     */
    private static final String REGEXP_ERROR = "SERVER_ONLINE (0|1)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP_ERROR);

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.correct_version = matcher.group(1).equals("1");
        }
    }

    public boolean isCorrectVersion() {
        return correct_version;
    }

    public void setCorrectVersion(boolean correct_version) {
        this.correct_version = correct_version;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "SERVER_ONLINE "+(this.correct_version ? "1" : "0");
    }
}
