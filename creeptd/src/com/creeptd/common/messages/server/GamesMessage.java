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
 * Message from server to client, containing the actual game-list.
 * 
 * @author andreas
 *
 */
public class GamesMessage extends ServerMessage {

    private static final String REG_EXP =
            "GAMES((\\sGAME\\s([0-9]+)\\s\"([^\"]+)\"\\s" + "([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)\\s([0-9]+)" + "\\s\"([^\"]*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"(.*)\"\\s\"([^\"]+)\"\\s(0|1))*)";
    /**
     * pattern for regular expression.
     */
    public static final Pattern PATTERN = Pattern.compile(REG_EXP);
    private Set<GameDescription> games;

    /**
     * Default constructor.
     */
    public GamesMessage() {
        super();
        this.games = new HashSet<GameDescription>();
    }

    /**
     * @param games list of all games
     */
    public GamesMessage(Set<GameDescription> games) {
        super();
        this.games = games;
    }

    /**
     * @return the games
     */
    public Set<GameDescription> getGames() {
        return this.games;
    }

    /**
     * @param games the games to set
     */
    public void setGames(Set<GameDescription> games) {
        this.games = games;
    }

    /**
     * @return the String representation of the message.
     */
    @Override
    public String getMessageString() {
        String message = "GAMES";
        if ((this.games != null) && (this.games.size() > 0)) {
            for (GameDescription gameDescription : this.games) {
                message = message + " GAME " + gameDescription.toString();
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
            this.getGames().clear();
            String[] splitGamesMessagePart = gamesMessagePart.split("GAME");
            for (String gameDescriptionString : splitGamesMessagePart) {

                if (!gameDescriptionString.equals(" ")) {
                    if (GameDescription.PATTERN.matcher(gameDescriptionString).matches()) {
                        GameDescription gameDescription = new GameDescription();
                        gameDescription.initWithMessage(gameDescriptionString);
                        this.getGames().add(gameDescription);
                    }
                }
            }
        }
    }

    /**
     * Returns true if o is a GamesMessage instance with the same set of
     * games as this object.
     * @param o the object to compare to.
     * @return true if o is equal to this object.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof GamesMessage) && this.games.equals(((GamesMessage) o).getGames());
    }

    /**
     * Returns a hash code for this object.
     * @return a hash code
     */
    @Override
    public int hashCode() {
        return this.games.hashCode();
    }
}
