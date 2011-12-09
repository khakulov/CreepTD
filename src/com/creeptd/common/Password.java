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
package com.creeptd.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Daniel
 */
public class Password {
    private static String CREEPTD_SALT = "CREEPON";

    /**
     * Encode password to MD5 string.
     *
     * @param password
     * @return
     */
    public static String encodePassword(String password) {
        return md5(md5(password, CREEPTD_SALT), password);
    }

    /**
     * Create MD5 hashed string.
     *
     * @param s
     * @return
     */
    public static String md5(String s, String salt) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            if (salt != null) md5.update(salt.getBytes());
            md5.update(s.getBytes());
            byte[] result = md5.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                String c = Integer.toHexString(0xFF & result[i]);
                if (c.length() < 2) c = "0"+c;
                hexString.append(c);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
