
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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Message send from server to client, to start a waiting game.
 * 
 * @author andreas
 *
 */
public class StartGameMessage extends ServerMessage {

	private static final String REG_EXP = "START_GAME((\\s[0-9]+)*)";

	/**
	 * pattern for regular expression.
	 */
	public static final Pattern PATTERN = Pattern.compile(REG_EXP);
	private List<Integer> players;
	private int MapID;

	/**
	 * Set the players.
	 * @param players the ids of the players participating in the game.
	 */
	public void setPlayers(List<Integer> players) {
		this.players = players;
	}
	/**
	 * Set the MapID.
	 * @param int MapID.
	 */
	public void setMapID(int MapID) {
		this.MapID = MapID;
	}
	/**
	 * Returns the MapID.
	 * @return MapID
	 */
	public int getMapID() {
		return this.MapID;
	}
	/**
	 * Returns the list of players.
	 * @return the list of players.
	 */
	public List<Integer> getPlayers() {
		return this.players;
	}

	/**
	 * @return the message as String.
	 */
	public String getMessageString() {
		String message = "START_GAME";
		if (this.players != null) {
			message += " " + this.MapID;
			for (int id : this.players) {
				message += " " + id;
			}
		}
		return message;
	}
	
	/**
	 * @param messageString the message as String.
	 */
	public void initWithMessage(String messageString) {
		this.players = new LinkedList<Integer>();
		Matcher matcher = PATTERN.matcher(messageString);
		if (matcher.matches()) {
			String playersMessagePart = matcher.group(1);
			String[] splitPlayerMessagePart = playersMessagePart.split("\\s+");
			int count = 0;
			for (String id : splitPlayerMessagePart) {
				if (!id.equals("")) {
					if (count == 0){
					   this.MapID = Integer.parseInt(id);
					}else{
						this.players.add(Integer.parseInt(id));
					}
				count++;
				}
			}
		}
	}

	/**
	 * Returns true if o is a StartGameMessage with the same list of players.
	 * @param o the object to compare to.
	 * @return true if o is equal to this object.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof StartGameMessage)) {
			return false;
		}
		StartGameMessage m = (StartGameMessage) o;
		return (this.players == null)
			? m.getPlayers() == null
			: this.players.equals(m.getPlayers());
	}

	/**
	 * Returns a hash code for this object.
	 * @return a hash code for this object.
	 */
	public int hashCode() {
		return this.players.hashCode();
	}

}
