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

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Constants for client and server.
 * @author andreas
 */
public interface Constants {
    /**
     * Application version
     */
    String VERSION = "0.9.2-2 beta";

    /**
     * Default port for server-socket.
     */
    int DEFAULT_SERVER_PORT = 4747;
    /**
     * Default port for embedded webserver.
     */
    int DEFAULT_WEBSERVER_PORT = 80;
    /**
     * Default hostname for server (used for webstart).
     */
    String DEFAULT_HOSTNAME = "www.creeptd.com";
    /**
     * Default hostname for server (used for webstart).
     */
    int DEFAULT_MAX_CLIENTS = 500;
    /**
     * Default Servername.
     */
    String DEFAULT_SERVER_HOST = "localhost";
    /**
     * How long is one tick in milliseconds.
     */
    int TICK_MS = 50;
    /**
     * How many ticks are user actions (building/upgrading/selling a tower or
     * sending a creep) delayed?
     */
    int USER_ACTION_DELAY = 50;
    /**
     * Interval of income in Ticks.
     */
    int INCOME_TIME = 300;
    /**
     * Income at the beginning.
     */
    int START_INCOME = 200;
    /**
     * Credits at the beginning.
     */
    int CREDITS = 200;
    /**
     * Allow max N times the same IP in a game. You should not use this, because
     * players from a home network won't be able to play against each other.
     * Setting this to 0 will deactivate IP checking.
     */
    int MUTIACCOUNT_IP_CHECK = 0;
    /**
     * Disalow same UID in a game.
     */
    boolean MUTIACCOUNT_UID_CHECK = true;
    
    /** Maximum length of a pause in millis, 0 for unlimited */
    int MAX_PAUSE_MS = 60000;

    /** Maximum number of observers allowed for a game */
    int MAX_SPECTATORS = 20;
    
    /**
     * Lives at the beginning.
     */
    int LIVES = 20;
    double EAST = 0;
    double WEST = Math.PI;
    double WEST_MINUS = Math.PI * (-1);
    double SOUTH = (Math.PI / 2);
    double NORTH = (Math.PI / 2) * (-1);
    String SOUNDS_URL = "com/creeptd/client/resources/sounds/";
    String SIMLEY_URL = "com/creeptd/client/resources/smileys/";
    /**
     * Maps Download Server
     */
    String MAP_DOWNLOAD_URL = "http://static.creeptd.com/";
    String CREEPS_URL = "com/creeptd/client/resources/creeps/";
    /**
     * Timeout used in the server. If the server receives no messages from a
     * client for TIMEOUT milliseconds, it sends PING. If it doesn't receive a
     * message for another TIMEOUT milliseconds after that, it disconnects the
     * client.
     */
    int TIMEOUT = 60 * 1000;
    /**
     * Creeps in einer Welle
     */
    long CREEPS_IN_WAVE = 20;
    /**
     * Zeitlicher Abstand zwischen 2 Creeps beim Senden einer Welle
     */
    long SEND_WAVE_DELAY = 130;
    /**
     * Zeitlicher Abstand zwischen 2 Creeps
     */
    long CREEP_DELAY = SEND_WAVE_DELAY*1000000;
    /**
     * Zeitlicher Abstand zwischen 2 Wellen
     */
    long WAVE_DELAY = SEND_WAVE_DELAY * (CREEPS_IN_WAVE);

    /**
     * Available game modes
     */
    static enum Mode {
        ALLVSALL(0),
        SENDNEXT(1),
        SENDRANDOM(2),
        TEAM2VS2(3),
        SURVIVOR(4);

        /**
         * Return Mode for basic mode value.
         *
         * @param value Mode value
         * @return The mode or null, if unknown
         */
        public static Mode forValue(int value) {
            if (value == 0) return ALLVSALL;
            if (value == 1) return SENDNEXT;
            if (value == 2) return SENDRANDOM;
            if (value == 3) return TEAM2VS2;
            if (value == 4) return SURVIVOR;
            return null;
        }

        private int value;

        private Mode(int value) {
            this.value = value;
        }

        /**
         * Get basic mode value.
         *
         * @return Mode value
         */
        public int getValue() {
            return this.value;
        }
        
        public boolean equals(Constants.Mode m) {
            return this.value == m.getValue();
        }

        @Override
        public String toString() {
            if (this.value == ALLVSALL.getValue()) {
                return "All vs. All";
            } else if (this.value == SENDNEXT.getValue()) {
                return "Send to next";
            } else if (this.value == SENDRANDOM.getValue()) {
                return "Send to random";
            } else if (this.value == TEAM2VS2.getValue()) {
                return "Team 2vs2";
            } else if (this.value == SURVIVOR.getValue()) {
                return "Survivor";
            }
            return "Unknown";
        }
    }

    /**
     * Describes a map for the game.
     */
    static enum Map {

        Random_Map("com/creeptd/client/resources/maps/random.map"),
        AIRPORT("com/creeptd/client/resources/maps/map_airport.map"),
        ARENA("com/creeptd/client/resources/maps/map_arena.map"),
        ASTEROID("com/creeptd/client/resources/maps/map_asteroid.map"),
        ATOMIC("com/creeptd/client/resources/maps/map_atomic.map"),
        BAY("com/creeptd/client/resources/maps/map_TMseries-bay.map"),
        BILLARD("com/creeptd/client/resources/maps/map_Billard.map"),
        BLACKHOLEVECTOR("com/creeptd/client/resources/maps/map_blackholevector.map"),
        BLUE("com/creeptd/client/resources/maps/map_blue.map"),
        BLUEMAGMA("com/creeptd/client/resources/maps/map_bluemagma.map"),
        BLUESTARS("com/creeptd/client/resources/maps/map_bluestars.map"),
        BLUEVECTORWATER("com/creeptd/client/resources/maps/map_bluevectorwater.map"),
        BLOODLINES("com/creeptd/client/resources/maps/map_bloodlines.map"),
        CHROMOSOMVEKTOR("com/creeptd/client/resources/maps/map_chromosomvektor.map"),
        CIRCLE("com/creeptd/client/resources/maps/map_circle.map"),
        CIRCLEVECTOR("com/creeptd/client/resources/maps/map_circlevector.map"),
        CLIFFWAR("com/creeptd/client/resources/maps/map_cliffwar.map"),
        COLORART("com/creeptd/client/resources/maps/map_colorart.map"),
        COLORCIRCLEVECTOR("com/creeptd/client/resources/maps/map_colorcirclevector.map"),
        COLORDOT("com/creeptd/client/resources/maps/map_colordot.map"),
        CORRODED("com/creeptd/client/resources/maps/map_corroded.map"),
        CROSSVECTOR("com/creeptd/client/resources/maps/map_crossvector.map"),
        CRYSTAL("com/creeptd/client/resources/maps/map_crystal.map"),
        DARKVECTOR("com/creeptd/client/resources/maps/map_darkvector.map"),
        DESK("com/creeptd/client/resources/maps/map_desk.map"),
        DISEASEDVECTOR("com/creeptd/client/resources/maps/map_diseasedvector.map"),
        DOODLE("com/creeptd/client/resources/maps/map_Doodle.map"),
        DOODLEDOT("com/creeptd/client/resources/maps/map_doodledot.map"),
        DOODLEWAR("com/creeptd/client/resources/maps/map_doodlewar.map"),
        EASTEREGGVEC("com/creeptd/client/resources/maps/map_eastereggvector.map"),
        EMERALDTILES("com/creeptd/client/resources/maps/map_emeraldtiles.map"),
        FLOWER("com/creeptd/client/resources/maps/map_flower.map"),
        FLYINGANGEL("com/creeptd/client/resources/maps/map_FlyingAngel.map"),
        FLYINGORB("com/creeptd/client/resources/maps/map_flyingorb.map"),
        GEOMETRY("com/creeptd/client/resources/maps/map_geometry.map"),
        GEWITTER("com/creeptd/client/resources/maps/map_gewitter.map"),
        GREEN("com/creeptd/client/resources/maps/map_green.map"),
        GREENBLUEAURA("com/creeptd/client/resources/maps/map_greenblueaura.map"),
        GREENCRISTALINE("com/creeptd/client/resources/maps/map_GreenCristaline.map"),
        GREENVECTOR("com/creeptd/client/resources/maps/map_greenvector.map"),
        GRUNGE("com/creeptd/client/resources/maps/map_grunge.map"),
        HANDY("com/creeptd/client/resources/maps/map_handy.map"),
        HOTROUND("com/creeptd/client/resources/maps/map_hotround.map"),
        ICEWORLD("com/creeptd/client/resources/maps/map_iceworld.map"),
        INFINITY("com/creeptd/client/resources/maps/map_infinity.map"),
        ISLANDS("com/creeptd/client/resources/maps/map_islands.map"),
        JUMPINGCREEPS("com/creeptd/client/resources/maps/map_jumpingcreeps.map"),
        JUNGLE("com/creeptd/client/resources/maps/map_jungle.map"),
        KEEPOFFTHEGRASS("com/creeptd/client/resources/maps/map_keepoffthegrass.map"),
        LADDERVECTOR("com/creeptd/client/resources/maps/map_laddervector.map"),
        LAVA("com/creeptd/client/resources/maps/map_lava.map"),
        LAVACHAIN("com/creeptd/client/resources/maps/map_lavachain.map"),
        LABYRINTH("com/creeptd/client/resources/maps/map_Labyrinth.map"),
        LEITER("com/creeptd/client/resources/maps/map_Leiter.map"),
        LEMONDOT("com/creeptd/client/resources/maps/map_lemondot.map"),
        LITTLEPUNK("com/creeptd/client/resources/maps/map_littlepunk.map"),
        LONG("com/creeptd/client/resources/maps/map_long.map"),
        LOOMIS("com/creeptd/client/resources/maps/map_loomis.map"),
        MAGICMIKE("com/creeptd/client/resources/maps/map_magicmike.map"),
        MASTESOFCREEP("com/creeptd/client/resources/maps/map_mastesofcreep.map"),
        NOVA("com/creeptd/client/resources/maps/map_nova.map"),
        OLDPAPER("com/creeptd/client/resources/maps/map_oldpaper.map"),
        ORANGEVECTOR("com/creeptd/client/resources/maps/map_orangevector.map"),
        PINKVECTOR("com/creeptd/client/resources/maps/map_pinkvector.map"),
        PLASMAVEKTOR("com/creeptd/client/resources/maps/map_plasmavektor.map"),
        PLOX("com/creeptd/client/resources/maps/map_plox.map"),
        QUADCORE("com/creeptd/client/resources/maps/map_quadcore.map"),
        RACEWAYS("com/creeptd/client/resources/maps/map_raceways.map"),
        RADIALFADE("com/creeptd/client/resources/maps/map_radialfade.map"),
        RAFTING("com/creeptd/client/resources/maps/map_rafting.map"),
        RAINBOW("com/creeptd/client/resources/maps/map_rainbow.map"),
        REDCREEP("com/creeptd/client/resources/maps/map_redcreep.map"),
        REDPHASE("com/creeptd/client/resources/maps/map_redphase.map"),
        REDVECTOR("com/creeptd/client/resources/maps/map_redvector.map"),
        REDWORLD("com/creeptd/client/resources/maps/map_red.map"),
        RICHTUNGSWECHSEL("com/creeptd/client/resources/maps/map_richtungswechsel.map"),
        RUBYTILES("com/creeptd/client/resources/maps/map_rubytiles.map"),
        SANDBANK("com/creeptd/client/resources/maps/map_sandbank.map"),
        SENFGLAS("com/creeptd/client/resources/maps/map_senfglas.map"),
        SHOOPDAWHOOP("com/creeptd/client/resources/maps/map_shoopdawhoop.map"),
        SPECIES("com/creeptd/client/resources/maps/map_Species.map"),
        SPEEDRACE("com/creeptd/client/resources/maps/map_speedrace.map"),
        SPEEDVECTOR("com/creeptd/client/resources/maps/map_speedvector.map"),
        SPONGLE("com/creeptd/client/resources/maps/map_spongle.map"),
        STAIRSVECTOR("com/creeptd/client/resources/maps/map_stairsvector.map"),
        STARS("com/creeptd/client/resources/maps/map_stars.map"),
        STONES("com/creeptd/client/resources/maps/map_stones.map"),
        SUMMER2("com/creeptd/client/resources/maps/map_summer2.map"),
        TR2N("com/creeptd/client/resources/maps/map_tr2n.map"),
        TRANSIT("com/creeptd/client/resources/maps/map_Transit.map"),
        TRICKY("com/creeptd/client/resources/maps/map_tricky.map"),
        VERLAUFEN("com/creeptd/client/resources/maps/map_verlaufen.map"),
        VORTEX("com/creeptd/client/resources/maps/map_vortex.map"),
        WALDGEIST("com/creeptd/client/resources/maps/map_waldgeist.map"),
        WATERDROPS("com/creeptd/client/resources/maps/map_waterdrops.map"),
        WHITEVECTOR("com/creeptd/client/resources/maps/map_whitevector.map");

        private String filename = "";

        /**
         * Constructor for enum.
         *
         * @param flname
         *            Filename for Map
         */
        Map(String flname) {
            this.filename = flname;
        }

        /**
         * Filename of this Map.
         *
         * @return String of Filename
         */
        public String getFilename() {
            return this.filename;
        }

        /**
         * Get a map by id.
         *
         * @param id
         *            the id of the map
         * @return the map
         */
        public static Map getMapById(int id) {
            for (Map m : values()) {
                if (m.ordinal() == id) {
                    return m;
                }
            }
            // default, if the id is wrong
            return REDWORLD;
        }

        /**
         * geter for the path of the picture of a map.
         *
         * @param map
         *            mapname
         * @return picture path
         */
        public static String getPicturePath(String map) {

            String path = "random.jpg";
            String tempStr = null;
            InputStream res = (InputStream) Constants.class.getClassLoader().getResourceAsStream(Map.valueOf(map).getFilename());
            if (res == null) {
                System.out.println("Cannot open map: " + Map.valueOf(map).getFilename());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        res));

                try {
                    while ((tempStr = br.readLine()) != null) {

                        if (tempStr.contains(".bmp") || tempStr.contains(".png") || tempStr.contains(".jpg")) {
                            path = tempStr.trim();
                            break;
                        }
                    }
                } catch (IOException e) {

                    System.out.println("Cannot read map: " + Map.valueOf(map).getFilename());

                }
            }

            return "com/creeptd/client/resources/maps/" + path;

        }

        /**
         * geter for the path of the picture thumbnail of a map.
         *
         * @param map
         *            mapname
         * @return picture path
         */
        public static String getPictureThumbnailPath(String map) {

            String path = "random.jpg";
            String tempStr = null;
            InputStream res = (InputStream) Constants.class.getClassLoader().getResourceAsStream(Map.valueOf(map).getFilename());
            if (res == null) {
                System.out.println("Cannot open map: " + Map.valueOf(map).getFilename());

            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        res));

                try {
                    while ((tempStr = br.readLine()) != null) {

                        if (tempStr.contains(".bmp") || tempStr.contains(".png") || tempStr.contains(".jpg")) {
                            path = tempStr.trim();
                            break;
                        }
                    }
                } catch (IOException e) {

                    System.out.println("Cannot read map: " + Map.valueOf(map).getFilename());

                }
            }

            return "com/creeptd/client/resources/maps/thumbnail/" + path;

        }
    }

    /**
     * Indicates the type of an error.
     *
     * @author andreas
     *
     */
    enum ErrorType {

        /**
         * Close Game/Client/Server after a fatal error.
         */
        Fatal,
        /**
         * Error, that can be handeld.
         */
        Error,
        /**
         * Just a warning, not really an error.
         */
        Warning
    }

    /**
     * Indicates the type of a response-message.
     *
     * @author andreas
     *
     */
    enum ResponseType {

        /**
         * Request was successful.
         */
        ok,
        /**
         * Request was unsuccessful.
         */
        failed,
        /**
         * Request failed because of the given username.
         */
        username,
        /**
         * Request failed because the client and server have different version.
         */
        version,
        /**
         * Request failed because multi account using was detected.
         */
        multi
    }

    /**
     * The type of the creep.
     */
    public static enum Creeps {
        // price, income%, health, speed, bounty, reginerationRate, slowImmune, name, special, imageFileName

        creep1(50, 10, 300, 70, 5, 0, false, "Mercury", "", "1.png"),
        creep2(100, 10, 700, 65, 10, 0, false, "Mako", "", "2.png"),
        creep3(250, 10, 1400, 80, 25, 0, false, "Fast Nova", "", "3.png"),
        creep4(500, 10, 3500, 50, 50, 0, false, "Large Manta", "", "4.png"),
        creep5(1000, 9, 6000, 60, 90, 3, false, "Demeter", "Regenerates", "5.png"), // was: health=7000
        creep6(2000, 9, 14000, 65, 180, 0, true, "Ray", "Slow immunity", "6.png"),
        creep7(4000, 9, 30000, 90, 360, 0, false, "Speedy Raider", "Fast", "7.png"),
        creep8(8000, 9, 80000, 60, 720, 0, false, "Big Toucan", "Tough", "8.png"),
        creep9(15000, 8, 120000, 70, 1200, 50, false, "Vulture", "Regenerates", "9.png"), // was: health=1400000
        creep10(25000, 8, 250000, 75, 2000, 0, true, "Shark", "Slow immunity", "10.png"),
        creep11(40000, 8, 500000, 100, 3200, 0, false, "Racing Mamba", "Fast", "11.png"),
        creep12(60000, 8, 1200000, 65, 4800, 0, false, "Huge Titan", "Tough", "12.png"),
        creep13(100000, 7, 1400000, 65, 7000, 500, false, "Zeus", "Regenerates", "13.png"), // was: health=1500000
        creep14(200000, 7, 2500000, 80, 14000, 0, true, "Phoenix", "Slow immunity", "14.png"),
        creep15(400000, 7, 6000000, 140, 28000, 0, false, "Express Raptor", "Very fast", "15.png"),
        creep16(1000000, 7, 15000000, 70, 56000, 0, false, "Fat Colossus", "Very tough", "16.png");
        private int price;
        private int incomePercentage;
        private int health;
        private int speed;
        private int bounty;
        private int regenerationRate;
    	private boolean slowImmune;
        private String name;
        private String special;
    	private String imageFileName;
    	private static final String CREEPS_URL = "com/creeptd/client/resources/creeps/";

        /**
         * Get the creeps numerical index, e.g. 1 for creep1.
         *
         * @return The creep's index
         */
        public int getIndex() {
            int i=1; for (Creeps c : Creeps.values()) {
                if (c.equals(this)) {
                    return i;
                }
                i++;
            }
            return -1;
        }
        
        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the special
         */
        public String getSpecial() {
            return special;
        }

        /**
         * constructor for Enumeration.
         *
         * @param price
         *            of creep
         * @param income
         *            for creep
         * @param health
         *            of creep
         * @param speed
         *            of creep
         * @param bounty
         *            is the money you get for killing a creep
         */
        Creeps(int price, int income, int health, int speed, int bounty,
                int regenerationRate, boolean slowImmune, String name, String special, String imageFileName) {
            this.price = price;
            this.incomePercentage = income;
            this.health = health;
            this.speed = speed;
            this.bounty = bounty;
            this.regenerationRate = regenerationRate;
            this.slowImmune = slowImmune;
            this.name = name;
            this.special = special;
            this.imageFileName = imageFileName;
        }

        /**
         * {@inheritDoc}
         */
        public int getPrice() {
            return price;
        }

        /**
         * {@inheritDoc}
         */
        public int getIncomePercentage() {
            return incomePercentage;
        }

        public int getIncome() {
            return this.price * this.incomePercentage / 100;
        }

        /**
         * {@inheritDoc}
         */
        public int getHealth() {
            return health;
        }

        /**
         * {@inheritDoc}
         */
        public int getSpeed() {
            return speed;
        }

        public int getRegenerationRate() {
            return regenerationRate;
        }

        public boolean isSlowImmune() {
            return slowImmune;
        }

        /**
         * {@inheritDoc}
         */
        public String getSpeedString() {
            return translateSpeed(speed);
        }

        /**
         * {@inheritDoc}
         */
        public int getBounty() {
            return bounty;
        }

        /**
         * Translates the speedValue of the creep to a human readable
         * description.
         *
         * @return the human readable string
         * @param value
         *            int value
         */
        public static String translateSpeed(int value) {
            String speed = "";
            if (value > 100) {
                speed = "Ultra fast";
            } else if (value > 80) {
                speed = "Very fast";
            } else if (value > 70) {
                speed = "Fast";
            } else if (value > 65) {
                speed = "Medium";
            } else if (value > 60) {
                speed = "Slow";
            } else if (value > 55) {
                speed = "Very slow";
            } else {
                speed = "Ultra slow";
            }
            return speed;
        }
        
        public String getImageFileName() {
        	return CREEPS_URL + this.imageFileName;
        }
    }

    /**
     * The types of damage a tower can do on creeps.
     *
     * @author Philipp
     *
     */
    public static enum DamageType {
        normal, speed, slow, splash, strong
    }

    public static enum StrategyType {
        STRONGEST(1),
        WEAKEST(2),
        CLOSEST(3),
        FARTHEST(4),
        FASTEST(5);

        private int identifier;
        
        private StrategyType(int identifier) {
            this.identifier = identifier;
        }

        public int getIdentifier() {
            return this.identifier;
        }

        public static StrategyType valueForIdentifier(int identifier) {
            for (StrategyType type : StrategyType.values()) {
                if (type.identifier == identifier) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * The type of a tower.
     */
    public static enum Towers {
        // price | range | speed | damage | Sradius | Sreduction | slowRate %| slowTime | type | next | color

        tower13(200, 65, 13, 100, 0, 0.0, 0, 0, WeaponType.laser, null, Color.WHITE, "Basictower lvl 4", "Cheap upgrades", "13.png"),
        tower12(150, 55, 13, 75, 0, 0.0, 0, 0, WeaponType.laser, tower13, Color.RED, "Basictower lvl 3", "Cheap upgrades", "12.png"),
        tower11(100, 45, 13, 50, 0, 0.0, 0, 0, WeaponType.laser, tower12, Color.BLUE, "Basictower lvl 2", "Cheap upgrades", "11.png"),
        tower1(50, 35, 13, 25, 0, 0.0, 0, 0, WeaponType.laser, tower11, Color.GREEN, "Basictower lvl 1", "Cheap upgrades", "1.png"),

        tower23(3000, 55, 18, 100, 25, 0.7, 0.50, 50, WeaponType.multislower, null, Color.WHITE, "Slowtower lvl 4", "Slows target ((Splash at LVL4)", "23.png"),
        tower22(400, 50, 17, 75, 0, 0.0, 0.45, 50, WeaponType.slower, tower23, Color.RED, "Slowtower lvl 3", "Slows target (Splash at LVL4)t", "22.png"),
        tower21(200, 45, 16, 50, 0, 0.0, 0.35, 40, WeaponType.slower, tower22, Color.BLUE, "Slowtower lvl 2", "Slows target (Splash at LVL4)", "21.png"),
        tower2(100, 35, 15, 25, 0, 0, 0.30, 40, WeaponType.slower, tower21, Color.GREEN, "Slowtower lvl 1", "Slows target (Splash at LVL4)", "2.png"),

        tower33(7500, 55, 10, 1150, 35, 0.5, 0, 0, WeaponType.splashlaser, null, Color.WHITE, "Splashtower lvl 4", "Splash damage", "33.png"),
        tower32(3000, 50, 10, 500, 35, 0.6, 0, 0, WeaponType.splashlaser, tower33, Color.RED, "Splashtower lvl 3", "Splash damage", "32.png"),
        tower31(750, 45, 12, 200, 35, 0.7, 0, 0, WeaponType.splashlaser, tower32, Color.BLUE, "Splashtower lvl 2", "Splash damage", "31.png"),
        tower3(250, 40, 15, 50, 35, 0.7, 0, 0, WeaponType.splashlaser, tower31, Color.GREEN, "Splashtower lvl 1", "Splash damage", "3.png"),

        tower43(15000, 80, 60, 16500, 35, 0.5, 0, 0, WeaponType.projectile, null, Color.WHITE, "Rockettower lvl 4", "Splash damage, horning rockets", "43.png"),
        tower42(7500, 70, 65, 7500, 30, 0.6, 0, 0, WeaponType.projectile, tower43, Color.RED, "Rockettower lvl 3", "Splash damage, horning rockets", "42.png"),
        tower41(3000, 60, 75, 3000, 25, 0.7, 0, 0, WeaponType.projectile, tower42, Color.BLUE, "Rockettower lvl 2", "Splash damage, horning rockets", "41.png"),
        tower4(1000, 50, 75, 1000, 25, 0.7, 0, 0, WeaponType.projectile, tower41, Color.GREEN, "Rockettower lvl 1", "Splash damage, horning rockets", "4.png"),

        tower53(15000, 65, 3, 1800, 0, 0.0, 0, 0, WeaponType.laser, null, Color.WHITE, "Speedtower lvl 4", "Fast", "53.png"),
        tower52(7500, 60, 5, 1100, 0, 0.0, 0, 0, WeaponType.laser, tower53, Color.RED, "Speedtower lvl 3", "Fast", "52.png"),
        tower51(3000, 55, 7, 550, 0, 0.0, 0, 0, WeaponType.laser, tower52, Color.BLUE, "Speedtower lvl 2", "Fast", "51.png"),
        tower5(1000, 50, 9, 225, 0, 0.0, 0, 0, WeaponType.laser, tower51, Color.GREEN, "Speedtower lvl 1", "Fast", "5.png"),

        tower61(50000, 150, 50, 40000, 0, 0.0, 0, 0, WeaponType.laser, null, Color.WHITE, "Ultimatetower lvl 2", "Huge damage, high range", "61.png"),
        tower6(20000, 100, 100, 25000, 0, 0.0, 0, 0, WeaponType.laser, tower61, Color.GREEN, "Ultimatetower lvl 1", "Huge damage, high range", "6.png");

        private int price;
        private float range;
        private int speed;
        private int damage;
        private int splashRadius;
        private double damageReductionAtRadius;
        private double slowRate;
        private int slowTime;
        private WeaponType weaponType;
        private Constants.Towers next;
        private Color towerColor;
        private String name;
        private String special;
    	private String imageFileName;
    	private static final String TOWERS_URL = "com/creeptd/client/resources/towers/";

    	public static enum WeaponType {
    		laser,
    		splashlaser,
    		slower,
    		multislower,
    		projectile
    	}

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Get numeric tower identifier.
         * @return The numeric tower identifier
         */
        public int getIdentifier() {
            return Integer.parseInt(this.name().substring(5));
        }

        /**
         * @return the special
         */
        public String getSpecial() {
            return special;
        }

        /**
         * @return the slowTime
         */
        public int getSlowTime() {
            return slowTime;
        }

        /**
         * @return the splashRadius
         */
        public int getSplashRadius() {
            return splashRadius;
        }

        /**
         * @return the damageReductionAtRadius
         */
        public double getDamageReductionAtRadius() {
            return damageReductionAtRadius;
        }

        /**
         * @return the slowRate
         */
        public double getSlowRate() {
            return slowRate;
        }

        /**
         * @return the damageType
         */
        public WeaponType getWeaponType() {
            return weaponType;
        }

        /**
         * @param damageType
         *            the damageType to set
         */
        public void setWeaponType(WeaponType weaponType) {
            this.weaponType = weaponType;
        }

        /**
         * specifies Type of tower.
         *
         * @param price
         *            of tower, on upgrades the upgrade price
         * @param range
         *            of tower, on upgrades the new range
         * @param speed
         *            of tower, on upgrades the new speed
         * @param damage
         *            of tower, on upgrades the new damage
         * @param spashRadius
         *            of the tower, on upgrades the new radius
         * @param damageReductionAtRadius
         *            of the tower, on upgrades the new one
         * @param slowRate
         *            of the tower in %, on upgrades the new slowRate
         * @param slowTime
         *            of a slowed Creep in ticks, on upgrades the new time
         * @param damageType
         *            of the tower, on upgrades the new one
         * @param next
         *            next upgrade type
         * @param towerColor
         *            color of the tower
         * @param name
         *            of the tower
         * @param special
         *            of the tower
         */
        Towers(int price, int range, int speed, int damage, int spashRadius,
                double damageReductionAtRadius, double slowRate, int slowTime,
                WeaponType weaponType, Constants.Towers next,
                Color towerColor, String name, String special, String imageFileName) {

            this.price = price;
            this.range = range;
            this.speed = speed;
            this.damage = damage;
            this.splashRadius = spashRadius;
            this.damageReductionAtRadius = damageReductionAtRadius;
            this.slowRate = slowRate;
            this.slowTime = slowTime;
            this.weaponType = weaponType;
            this.next = next;
            this.towerColor = towerColor;
            this.name = name;
            this.special = special;
            this.imageFileName = imageFileName;

        }

        /**
         * getter for price of tower.
         *
         * @return priece
         */
        public int getPrice() {
            return price;
        }

        /**
         * getter for range of tower.
         *
         * @return range
         */
        public float getRange() {
            return range;
        }

        /**
         * getter for speed of tower.
         *
         * @return speed
         */
        public int getSpeed() {
            return speed;
        }

        /**
         * getter.
         *
         * @return the speed of the tower as a string
         */
        public String getSpeedString() {
            return translateSpeed(speed);
        }

        /**
         * getter for damage of tower.
         *
         * @return damage
         */
        public int getDamage() {
            return damage;
        }

        /**
         * getter.
         *
         * @return next Towertype after upgrade
         */
        public Constants.Towers getNext() {
            return next;
        }

        public int getUpgradeLevel() {
            int level = (this.equals(tower61) || this.equals(tower6)) ? 2 : 4;
            Constants.Towers current = this;
            while (current.next != null) {
                current = current.next;
                level--;
            }
            return level;
        }
        
        /**
         * @return the towerColor
         */
        public Color getTowerColor() {
            return towerColor;
        }

        /**
         * Translates the speedValue of the tower to a human readable
         * description.
         *
         * @return the human readable string
         * @param value
         *            int value
         */
        public static String translateSpeed(int value) {
            String speed = "";
            if (value > 50) {
                speed = "Ultra slow";
            } else if (value > 20) {
                speed = "Very slow";
            } else if (value > 15) {
                speed = "Slow";
            } else if (value > 10) {
                speed = "Medium";
            } else if (value > 7) {
                speed = "Fast";
            } else if (value > 3) {
                speed = "Very fast";
            } else {
                speed = "ultra fast";
            }
            return speed;
        }
        
        public String getImageFileName() {
        	return TOWERS_URL + this.imageFileName;
        }
    }
}
