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
package com.creeptd.client.util;

import com.creeptd.client.Core;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * JNLP only functionality that can't be used in standalone apps.
 *
 * @author Daniel
 */
public class JNLP {

    /**
     * Check if JNLP functionality is available.
     *
     * @return true if available, else false
     */
    public static boolean isAvailable() {
        try {
            Class.forName("javax.jnlp.ServiceManager");
            javax.jnlp.PersistenceService ps = (javax.jnlp.PersistenceService) javax.jnlp.ServiceManager.lookup("javax.jnlp.PersistenceService");
        } catch (Exception ex) {
            System.out.println("JNLP.PersistenceService is not available: "+ex);
            return false;
        }
        return true;
    }
    
    /**
     * Get persistent value.
     *
     * @param uri Data URI
     * @return Data String or null if not present
     */
    public static String getValue(String uri) {
        Logger logger = Core.getLogger();
        javax.jnlp.PersistenceService ps = null;
        String codebase = null;
        try {
            ps = (javax.jnlp.PersistenceService) javax.jnlp.ServiceManager.lookup("javax.jnlp.PersistenceService");
            javax.jnlp.BasicService bs = (javax.jnlp.BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
            codebase = bs.getCodeBase().toString();
        } catch (Exception ex) {
            logger.warning("Unable to look up PersistenceService: "+ex);
            return null;
        }
        URL key = null;
        javax.jnlp.FileContents fc = null;
        try {
            key = new URL(codebase+uri);
        } catch (MalformedURLException ex) {
            Core.getLogger().warning("Unable to build persistence service uri: "+ex);
            return null;
        }
        try {
            fc = ps.get(key);
            byte[] b = new byte[(int) fc.getLength()];
            fc.getInputStream().read(b);
            return new String(b);
        } catch (Exception ex) {
            return null; // Failed to put something
        }
    }

    /**
     * Set persistent value.
     *
     * @param uri Data URI
     * @param data Data String
     * @return true on success, else false
     */
    public static boolean setValue(String uri, String data) {
        Logger logger = Core.getLogger();
        javax.jnlp.PersistenceService ps = null;
        String codebase = null;
        try {
            ps = (javax.jnlp.PersistenceService) javax.jnlp.ServiceManager.lookup("javax.jnlp.PersistenceService");
            javax.jnlp.BasicService bs = (javax.jnlp.BasicService) javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
            codebase = bs.getCodeBase().toString();
        } catch (Exception ex) {
            logger.warning("Unable to look up PersistenceService: "+ex);
            return false;
        }
        URL key = null;
        javax.jnlp.FileContents fc = null;
        try {
            key = new URL(codebase+uri);
        } catch (MalformedURLException ex) {
            Core.getLogger().warning("Unable to build persistence service uri: "+ex);
            return false;
        }
        try {
            ps.create(key, data.getBytes().length);
            fc = ps.get(key);
            fc.getOutputStream(false).write(data.getBytes());
            return true;
        } catch (Exception ex) {
            logger.warning("Unable to write to persistence service: "+ex);
        }
        return false;
    }
}
