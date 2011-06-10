
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

import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Hashtable;

import com.creeptd.common.messages.MessageUtil;
import java.util.ArrayList;
import java.util.List;

/**
 * Message from server to client, containing the actual players list.
 * 
 * @author andreas
 *
 */
public class PlayersMessage extends ServerMessage {

	private static final String REG_EXP_PLAYERS = "PLAYERS((\\sPLAYER\\s\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+))*)";
	private static final String REG_EXP_PLAYER = "(\\s)*\"([^\"]+)\"\\s([0-9]+)\\s([0-9]+)(\\s)*";
	
	/**
	 * pattern for regular expression.
	 */
	public static final Pattern PATTERN = Pattern.compile(REG_EXP_PLAYERS);
	public static final Pattern PATTERN_PLAYER = Pattern.compile(REG_EXP_PLAYER);
	
	private Hashtable<String, List<Integer>> playerNames;

	/**
	 * Default constructor.
	 */
	public PlayersMessage() {
		super();
		this.playerNames = new Hashtable<String, List<Integer>>();
	}
	
	/**
	 * @param playerNames the playerNames to set. None of the names may be null.
	 */
	public PlayersMessage(Hashtable<String, List<Integer>> playerNames) {
		super();
		setPlayerNames(playerNames);
	}


	/**
	 * @return the playerNames
	 */
	public Hashtable<String, List<Integer>> getPlayerNames() {
		return this.playerNames;
	}

	/**
	 * @param playerNames the playerNames to set. None of the names may be null.
	 */
	public void setPlayerNames(Hashtable<String, List<Integer>> playerNames) {
		this.playerNames = playerNames;
	}

	/**
	 * @return the message as String.
	 */
	@Override
	public String getMessageString() {
		
		String message_player = "";
		String message = "PLAYERS";
		Enumeration<String> e = this.playerNames.keys();
	    while (e.hasMoreElements()) {
	      String key = (String) e.nextElement();
	      
	      message_player +=  " PLAYER";
	      message_player += " \"" + MessageUtil.prepareToSend(key) + "\"";
	      message_player += " " + this.playerNames.get(key).get(0); // Exp
	      message_player += " " + this.playerNames.get(key).get(1); // Elo
	    }
		return message+message_player;
	}

	
	/**
	 * @param messageString the message as String.
	 */
	@Override
	public void initWithMessage(String messageString) {
		Matcher matcher = PATTERN.matcher(messageString);
		if (matcher.matches()) {
			String playerMessagePart = matcher.group(1);
			String[] splitPlayerMessagePart = playerMessagePart.split("PLAYER");
			
			for (String player : splitPlayerMessagePart) {
				Matcher matcher_player = PATTERN_PLAYER.matcher(player);
				
				if (matcher_player.matches()) {
					List l = new ArrayList();
                                        l.add(Integer.parseInt(matcher_player.group(3)));
                                        l.add(Integer.parseInt(matcher_player.group(4)));
					this.playerNames.put(matcher_player.group(2), l);

				}
			}
		}
	}
	
	/**
	 * Returns true if o is a PlayersMessage instance with the same set of
	 * players as this object.
	 * @param o the object to compare to.
	 * @return true if o is equal to this object.
	 */
	@Override
	public boolean equals(Object o) {
		return
			(o instanceof PlayersMessage)
			&& this.playerNames.equals(((PlayersMessage) o).getPlayerNames());
	}

	/**
	 * Returns a hash code for this object.
	 * @return a hash code
	 */
	@Override
	public int hashCode() {
		return this.playerNames.hashCode();
	}
	
}
