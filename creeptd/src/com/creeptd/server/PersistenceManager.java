
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


package com.creeptd.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * A singleton that can be used to retrieve an EntityManager.
 */
public final class PersistenceManager {

	private static PersistenceManager persistenceManager;
	
	private final EntityManagerFactory entityManagerFactory;
	
	/**
	 * Private constructor.
	 */
	private PersistenceManager() {
		this.entityManagerFactory = Persistence
		.createEntityManagerFactory("db", Server.getConfigFileDB());
	}
	
	/**
	 * @return the singleton.
	 */
	public static PersistenceManager getInstance() {
		
		if (persistenceManager == null) {
			persistenceManager = new PersistenceManager();
		}
		return persistenceManager;
	}
	
	
	/**
	 * @return the EntityManager.
	 */
	public EntityManager getEntityManager() {
		return this.entityManagerFactory.createEntityManager();
	}
		
}