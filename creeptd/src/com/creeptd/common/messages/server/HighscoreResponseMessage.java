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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message from server to client, 
 * containing the highscore-list.
 * 
 * @author andreas
 *
 */
public class HighscoreResponseMessage extends ServerMessage {

    private static final String REG_EXP =
            "HIGHSCORE_RESPONSE((\\sENTRY\\s\"[^\"]+\"\\s[0-9]+\\s[0-9]+\\s(-)?[0-9]+\\s(-)?[0-9]+)*)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private Set<HighscoreEntry> highscoreEntries;

    /**
     * Default constructor.
     */
    public HighscoreResponseMessage() {
        super();
        this.highscoreEntries = new HashSet<HighscoreEntry>();
    }

    /**
     * @param higscoreEntries the highscore-list
     */
    public HighscoreResponseMessage(Set<HighscoreEntry> higscoreEntries) {
        super();
        this.highscoreEntries = higscoreEntries;
    }

    /**
     * @return the highscoreEntries
     */
    public Set<HighscoreEntry> getHighscoreEntries() {
        return this.highscoreEntries;
    }

    /**
     * @param highscoreEntries the highscoreEntries to set
     */
    public void setHighscoreEntries(Set<HighscoreEntry> highscoreEntries) {
        this.highscoreEntries = highscoreEntries;
    }

    /**
     * @return the String representation of the message.
     */
    @Override
    public String getMessageString() {
        String message = "HIGHSCORE_RESPONSE";
        if ((this.highscoreEntries != null) && (this.highscoreEntries.size() > 0)) {
            for (HighscoreEntry entry : this.highscoreEntries) {
                message = message + " ENTRY " + entry.toString();
            }
        }
        return message;
    }

    /**
     * @param messageString the message as String.
     */
    @Override
    public void initWithMessage(String messageString) {
        Matcher matcher = PATTERN.matcher(messageString);
        if (matcher.matches()) {
            String gamesMessagePart = matcher.group(1);
            this.getHighscoreEntries().clear();
            String[] splittedMessage = gamesMessagePart.split("ENTRY");
            for (String highscoreEntryString : splittedMessage) {
                if (!highscoreEntryString.equals(" ")) {
                    if (HighscoreEntry.PATTERN.matcher(highscoreEntryString).matches()) {
                        HighscoreEntry highscoreEntry = new HighscoreEntry();
                        highscoreEntry.initWithMessage(highscoreEntryString);
                        this.getHighscoreEntries().add(highscoreEntry);
                    }
                }
            }
        }
    }

    /**
     * Returns true if o is a HighscoreResponseMessage with the same
     * contents as this one.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HighscoreResponseMessage)) {
            return false;
        }
        HighscoreResponseMessage m = (HighscoreResponseMessage) o;
        return this.highscoreEntries.equals(m.getHighscoreEntries());
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code for this object.
     */
    @Override
    public int hashCode() {
        return this.highscoreEntries.hashCode();
    }
}
