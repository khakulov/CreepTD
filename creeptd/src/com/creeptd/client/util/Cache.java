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

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import com.creeptd.common.IConstants;

/**
 * Cache for images and shapes. All images used in the game should be placed
 * into this cache. The hashtables with the array of images can be used for
 * animation.
 * 
 * @author Philipp
 */
public class Cache {

    private Hashtable<IConstants.Towers, BufferedImage> imgTowerCache;
    private Hashtable<IConstants.Creeps, BufferedImage> imgCreepCache;
    private Hashtable<IConstants.Towers, BufferedImage[]> imgArrayTowerCache;
    private Hashtable<IConstants.Creeps, BufferedImage[]> imgArrayCreepCache;
    /**
     * Creates the instance of Cache.
     */
    private final static Cache instance = new Cache();

    /**
     * todo.
     */
    private Cache() {
        this.imgTowerCache = new Hashtable<IConstants.Towers, BufferedImage>();
        this.imgCreepCache = new Hashtable<IConstants.Creeps, BufferedImage>();
        this.imgArrayTowerCache = new Hashtable<IConstants.Towers, BufferedImage[]>();
        this.imgArrayCreepCache = new Hashtable<IConstants.Creeps, BufferedImage[]>();
    }

    /**
     * Method for unit test. Clears all hashtables.
     */
    public void clearCache() {
        this.imgTowerCache.clear();
        this.imgCreepCache.clear();
        this.imgArrayCreepCache.clear();
        this.imgArrayTowerCache.clear();
    }

    /**
     * todo.
     *
     * @return todo
     */
    public static Cache getInstance() {
        return instance;
    }

    /**
     * Tests if there is already an array of image for the given type.
     *
     * @param type
     *            the Tower type
     * @return true if theres already an image array in the hashtable
     */
    public boolean hasArrayTowerImg(IConstants.Towers type) {
        return this.imgArrayTowerCache.containsKey(type);
    }

    /**
     * Gets an image array from the cache.
     *
     * @param type
     *            the Tower type
     * @return the BufferedImage array for the type or null if there is no image
     *         for the Tower type
     */
    public BufferedImage[] getArrayTowerImg(IConstants.Towers type) {
        return this.imgArrayTowerCache.get(type);
    }

    /**
     * Adds an image array to the cache.
     *
     * @param type
     *            the Tower type
     * @param image
     *            the BufferedImage array
     */
    public void putArrayTowerImg(IConstants.Towers type, BufferedImage[] image) {
        this.imgArrayTowerCache.put(type, image);
    }

    /**
     * Tests if there is already an image for the given type.
     *
     * @param type
     *            the tower type
     * @return true if theres already an image in the hashtable
     */
    public boolean hasTowerImg(IConstants.Towers type) {
        return this.imgTowerCache.containsKey(type);
    }

    /**
     * Gets an image from the cache.
     *
     * @param type
     *            the tower type
     * @return the BufferedImage for the type or null if there is no image for
     *         the tower type
     */
    public BufferedImage getTowerImg(IConstants.Towers type) {
        return this.imgTowerCache.get(type);
    }

    /**
     * Adds an image to the cache.
     *
     * @param type
     *            the tower type
     * @param image
     *            the BufferedImage
     */
    public void putTowerImg(IConstants.Towers type, BufferedImage image) {
        this.imgTowerCache.put(type, image);
    }

    /**
     * Tests if there is already an array of image for the given type.
     *
     * @param type
     *            the Creep type
     * @return true if theres already an image array in the hashtable
     */
    public boolean hasArrayCreepImg(IConstants.Creeps type) {
        return this.imgArrayCreepCache.containsKey(type);
    }

    /**
     * Gets an image array from the cache.
     *
     * @param type
     *            the Creep type
     * @return the BufferedImage array for the type or null if there is no image
     *         for the Creep type
     */
    public BufferedImage[] getArrayCreepImg(IConstants.Creeps type) {
        return this.imgArrayCreepCache.get(type);
    }

    /**
     * Adds an image array to the cache.
     *
     * @param type
     *            the Creep type
     * @param image
     *            the BufferedImage array
     */
    public void putArrayCreepImg(IConstants.Creeps type, BufferedImage[] image) {
        this.imgArrayCreepCache.put(type, image);
    }

    /**
     * Tests if there is already an image for the given type.
     *
     * @param type
     *            the Creep type
     * @return true if theres already an image in the hashtable
     */
    public boolean hasCreepImg(IConstants.Creeps type) {
        return this.imgCreepCache.containsKey(type);
    }

    /**
     * Gets an image from the cache.
     *
     * @param type
     *            the Creep type
     * @return the BufferedImage for the type or null if there is no image for
     *         the Creep type
     */
    public BufferedImage getCreepImg(IConstants.Creeps type) {
        return this.imgCreepCache.get(type);
    }

    /**
     * Adds an image to the cache.
     *
     * @param type
     *            the Creep type
     * @param image
     *            the BufferedImage
     */
    public void putCreepImg(IConstants.Creeps type, BufferedImage image) {
        this.imgCreepCache.put(type, image);
    }
}
