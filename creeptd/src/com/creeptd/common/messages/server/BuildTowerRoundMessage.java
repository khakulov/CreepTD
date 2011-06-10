
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

import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Message from server to client, when a new tower hast to be build.
 * 
 * @author andreas
 *
 */
public class BuildTowerRoundMessage extends ServerMessage 
										implements GameMessage {

	/**
	 * regular expression.
	 */
	private static final String REG_EXP = 
		"ROUND\\s([0-9]+):\\sPLAYER\\s([0-9]+)\\s"
		+ "BUILDS\\sTOWER\\s\"([^\"]+)\"\\sAT\\s([0-9]+)," 
		+ "([0-9]+)\\sWITH\\sID\\s([0-9]+)";

	/**
	 * pattern for regular expression.
	 */
	public static final Pattern PATTERN = Pattern.compile(REG_EXP);
	
	
	private Long roundId;
	private Integer playerId;
	private String towerType;
	private Point towerPosition;
	private Integer towerId;
	
		
	/**
	 * @return the roundId
	 */
	public Long getRoundId() {
		return this.roundId;
	}

	/**
	 * @param roundId the roundId to set
	 */
	public void setRoundId(Long roundId) {
		this.roundId = roundId;
	}

	/**
	 * @return the playerId
	 */
	public Integer getPlayerId() {
		return this.playerId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}

	/**
	 * @return the towerType
	 */
	public String getTowerType() {
		return this.towerType;
	}

	/**
	 * @param towerType the towerType to set
	 */
	public void setTowerType(String towerType) {
		this.towerType = towerType;
	}

	/**
	 * @return the towerPosition
	 */
	public Point getTowerPosition() {
		return this.towerPosition;
	}

	/**
	 * @param towerPosition the towerPosition to set
	 */
	public void setTowerPosition(Point towerPosition) {
		this.towerPosition = towerPosition;
	}

	/**
	 * @return the towerId
	 */
	public Integer getTowerId() {
		return this.towerId;
	}

	/**
	 * @param towerId the towerId to set
	 */
	public void setTowerId(Integer towerId) {
		this.towerId = towerId;
	}

	/**
	 * @return the String representint the message.
	 */
	@Override
	public String getMessageString() {
		return "ROUND " + this.roundId + ": PLAYER " 
		+ this.playerId + " BUILDS TOWER \"" + this.towerType 
		+ "\" AT " + this.towerPosition.x + "," + this.towerPosition.y 
		+ " WITH ID " + this.towerId + "";
	}

	/**
	 * @param messageString the String representation of the message.
	 */
	@Override
	public void initWithMessage(String messageString) {
		Matcher matcher = PATTERN.matcher(messageString);
		if (matcher.matches()) {
			this.setRoundId(Long.parseLong(matcher.group(1)));
			this.setPlayerId(Integer.parseInt(matcher.group(2)));
			this.setTowerType(matcher.group(3));
			
			Point position = new Point();
			position.x = Integer.valueOf(matcher.group(4));
			position.y = Integer.valueOf(matcher.group(5));
			this.setTowerPosition(position);
			
			this.setTowerId(Integer.parseInt(matcher.group(6)));
		}
	}

	/**
	 * Returns true if o is a BuildTowerRoundMessage with the same
	 * attributes as this one.
	 * 
	 * @param o the Object to compare to
	 * @return true if o is equal to this object.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BuildTowerRoundMessage)) {
			return false;
		}
		BuildTowerRoundMessage m = (BuildTowerRoundMessage) o;
		return this.roundId == m.getRoundId()
			&& this.playerId == m.getPlayerId()
			&& this.towerType.equals(m.getTowerType())
			&& this.towerPosition.equals(m.getTowerPosition())
			&& this.towerId == m.getTowerId();
	}

	/**
	 * Returns a hash code for this object.
	 * @return a hash code for this object.
	 */
	@Override
	public int hashCode() {
		return (int) (this.roundId ^ this.playerId ^ this.towerId);
	}

}
