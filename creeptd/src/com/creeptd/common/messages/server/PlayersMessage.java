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

import com.creeptd.common.messages.MessageUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Message from server to client, containing the actual players list.
 * 
 * @author andreas, Daniel
 */
public class PlayersMessage extends ServerMessage {

    private static final String REG_EXP_PLAYERS = "PLAYERS((\\sPLAYER\\s\"([^\"]+)\"\\s([0-9]+)\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+))*)";
    private static final String REG_EXP_PLAYER = "[\\s]*\"([^\"]+)\"\\s([0-9]+)\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)(\\s)*";
    public static final Pattern PATTERN = Pattern.compile(REG_EXP_PLAYERS);
    public static final Pattern PATTERN_PLAYER = Pattern.compile(REG_EXP_PLAYER);
    /** List of players contained in this message */
    private List<Player> players = new LinkedList<Player>();

    /**
     * A player struct.
     */
    public class Player {
        public String operation;
        public int id;
        public String name;
        public int points;
        public int skill;

        /** Create player from message */
        Player(String message) {
            Matcher matcher = PATTERN_PLAYER.matcher(message);
            if (matcher.matches()) {
                this.operation = matcher.group(1);
                this.id = Integer.parseInt(matcher.group(2));
                this.name = matcher.group(3);
                this.points = Integer.parseInt(matcher.group(4));
                this.skill = Integer.parseInt(matcher.group(5));
            }
        }

        /** Create player from details */
        Player(String operation, int id, String name, int points, int skill) {
            this.operation = operation;
            this.id = id;
            this.name = name;
            this.points = points;
            this.skill = skill;
        }

        @Override
        public String toString() {
            return "PLAYER \""+this.operation+"\" "+this.id+" \""+MessageUtil.prepareToSend(this.name)+"\" "+this.points+" "+this.skill;
        }
    }

    /**
     * Default constructor.
     */
    public PlayersMessage() {
        super();
    }

    /**
     * Get players.
     *
     * @return The players
     */
    public List<Player> getPlayers() {
        return this.players;
    }
    
    /**
     * Add a player.
     * 
     * @param id Player's id
     * @param name Player's name
     * @param points Player's points
     * @param skill Player's skill
     */
    public void addPlayer(String operation, int id, String name, int points, int skill) {
        Player p = new Player(operation, id, name, points, skill);
        this.players.add(p);
    }

    /**
     * @return the message as String.
     */
    @Override
    public String getMessageString() {
        String message = "PLAYERS";
        Iterator<Player> i = this.players.iterator();
        while (i.hasNext()) {
            Player p = i.next();
            message += " "+p.toString();
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
            String playersMessage = matcher.group(1);
            String[] splitMessage = playersMessage.split("PLAYER");
            for (String playerMessage : splitMessage) {
                Matcher pmatcher = PATTERN_PLAYER.matcher(playerMessage);
                if (pmatcher.matches()) {
                    Player p = new Player(playerMessage);
                    this.players.add(p);
                }
            }
        }
    }
}
