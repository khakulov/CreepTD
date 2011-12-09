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
package com.creeptd.common.messages;

/**
 * super-class for all messages.
 */
public abstract class Message {

	/**
	 * Sets the values of a message with the given arguments from
	 * message-string.
	 * 
	 * @param messageString
	 *            the message to initialize with
	 */
	public abstract void initWithMessage(String messageString);

	/**
	 * @return the message-string to transfer to the client
	 * @throws Exception
	 */
	public abstract String getMessageString();

	/**
	 * Returns the message as a string.
	 * 
	 * @return the message string.
	 */
	public String toString() {
		return this.getMessageString();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (this.getClass() != object.getClass())
			return false;
		if (!super.equals(object))
			return false;
		if (!this.getMessageString().equals(((Message) object).getMessageString()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = 17 + 89 * hash + 113*(this.getMessageString() != null ? this.getMessageString().hashCode() : 0);
		return hash;
	}
}
