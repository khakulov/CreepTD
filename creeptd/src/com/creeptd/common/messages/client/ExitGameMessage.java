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

import com.creeptd.common.messages.MessageUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message from client, after player has quit the game.
 * 
 * @author andreas
 *
 */
public class ExitGameMessage extends ClientMessage implements GameMessage {
    private String message = "";

    /**
     * regular expression for message-parsing.
     */
    private static final String REGEXP_EXIT_GAME = "EXIT_GAME \"([^\"]*)\"";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REGEXP_EXIT_GAME);

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            this.setMessage(matcher.group(1));
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        return "EXIT_GAME \""+MessageUtil.prepareToSend(this.message)+"\"";
    }
}
