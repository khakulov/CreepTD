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
package com.creeptd.client.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.creeptd.client.game.grids.EmptyGrid;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.grids.HolyGrid;
import com.creeptd.client.game.grids.PathGrid;
import com.creeptd.common.Constants;
import java.awt.AlphaComposite;
import java.awt.Composite;

/**
 * Holds the whole grid and acts as an mouseListener on the BoardPanel.
 * 
 * @author schuhu
 * 
 */
public class GameBoard {

    private static Logger logger = Logger.getLogger(GameBoard.class.getName());
    private GameContext context;
    private Grid[][] gridArray;
    private BufferedImage image;
    private boolean clearImg = true;
    private BufferedImage imgBackground;
    private BufferedImage imgBackgroundRotated;
    @SuppressWarnings("unused")
	private Color playerContextAlphaColor = null;
    private Path path;
    private BufferedReader br;
    private Point[] mapWithPath;
    private Point[] mapWithHoly;
    private Boolean maploaded = false;
    private int x = 16;
    private int y = 16;

    /**
     * Create basic game board.
     *
     * @param location Board location
     * @param map Map played
     * @param context Player's context or null, if none
     */
    public GameBoard(BoardLocation location, Constants.Map map, GameContext context) {
        this.context = context;
        this.setPath(new Path());
        this.imgBackground = new BufferedImage(321, 321, BufferedImage.TYPE_INT_RGB);
        this.imgBackgroundRotated = new BufferedImage(321, 321, BufferedImage.TYPE_INT_RGB);
        this.loadMap(map);
        if (maploaded) {
            this.rotateMap(location);
        }
        this.image = new BufferedImage(321, 321, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * Create GameBoard from existing context.
     *
     * @param context the gameContext, may not be null
     */
    public GameBoard(GameContext context) {
        this(context.getLocation(), context.getGameLoop().getMap(), context);

        if (this.context != null) {
            for (int i = 0; i < this.mapWithPath.length; i++) {
                getPath().addSegment((int) this.mapWithPath[i].getX(),
                        (int) this.mapWithPath[i].getY());
            }
            for (int i = 0; i < getPath().getLength(); i++) {
                Integer[] ij = getPath().getStep(i);
                gridArray[ij[0]][ij[1]] = new PathGrid(ij[0] * Grid.SIZE, ij[1] * Grid.SIZE, this.context);
            }
            // add HolyGrids to gridArray
            for (int i = 0; i < this.mapWithHoly.length; i++) {
                int xHoly = (int) this.mapWithHoly[i].getX();
                int yHoly = (int) this.mapWithHoly[i].getY();
                gridArray[xHoly][yHoly] = new HolyGrid(x * Grid.SIZE,
                        y * Grid.SIZE, this.context);
            }
        }
    }

    /**
     * Load the map
     */
    public void loadMap(Constants.Map map) {
        String filename = "";

        // Initialize the grid
        gridArray = new Grid[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                gridArray[i][j] = new EmptyGrid(i * Grid.SIZE, j * Grid.SIZE, this.context);
            }
        }

        // Read map file
        ArrayList<String> lineWithPath = new ArrayList<String>();
        ArrayList<String> lineWithHoly = new ArrayList<String>();
        String tempStr = null;

        InputStream res = this.getClass().getClassLoader().getResourceAsStream(map.getFilename());
        if (res == null) {
            logger.severe("Cannot open map!");
        }
        br = new BufferedReader(new InputStreamReader(res));
        try {
            while ((tempStr = br.readLine()) != null) {
                if (tempStr.contains(",")) { // Path, e.g. 10,9
                    lineWithPath.add(tempStr);
                }
                if (tempStr.contains(";")) { // Holy land, e.g. 10;9
                    lineWithHoly.add(tempStr);
                }
                if (tempStr.contains(".bmp") || tempStr.contains(".png") || tempStr.contains(".jpg")) {
                    filename = tempStr.trim();
                }
                if (tempStr.contains("SET_ALPHA_BACKGROUND_COLOR:")) {
                    String[] split = tempStr.trim().split(":");
                    if (split[1].equals("GRAY")) {
                        playerContextAlphaColor = Color.GRAY;
                    }
                    if (split[1].equals("WHITE")) {
                        playerContextAlphaColor = Color.WHITE;
                    }
                    if (split[1].equals("BLUE")) {
                        playerContextAlphaColor = Color.BLUE;
                    }
                    if (split[1].equals("RED")) {
                        playerContextAlphaColor = Color.RED;
                    }
                    if (split[1].equals("YELLOW")) {
                        playerContextAlphaColor = Color.YELLOW;
                    }
                    if (split[1].equals("MAGENTA")) {
                        playerContextAlphaColor = Color.MAGENTA;
                    }
                    if (split[1].equals("CYAN")) {
                        playerContextAlphaColor = Color.CYAN;
                    }
                    if (split[1].equals("GREEN")) {
                        playerContextAlphaColor = Color.GREEN;
                    }
                    if (split[1].equals("OFF")) {
                        playerContextAlphaColor = null;
                    }
                }
            }
        } catch (IOException e) {
            logger.severe("Cannot read map!");
        }

        mapWithPath = new Point[lineWithPath.size()];
        for (int i = 0; i < mapWithPath.length; i++) {
            tempStr = (String) lineWithPath.get(i);
            String[] split = tempStr.split(",");
            mapWithPath[i] = new Point(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]));
        }

        mapWithHoly = new Point[lineWithHoly.size()];
        for (int i = 0; i < mapWithHoly.length; i++) {
            tempStr = (String) lineWithHoly.get(i);
            String[] split = tempStr.split(";");
            mapWithHoly[i] = new Point(Integer.parseInt(split[0]),
                    Integer.parseInt(split[1]));
        }

        try {
            imgBackground = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("com/creeptd/client/resources/maps/" + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        maploaded = true;
    }

    /**
     * paint method to paint the board.
     *
     * @param g the graphics2d object
     */
    public void paint(Graphics2D g) {
        // paint grid background into bufferedImage
        if (clearImg) {

            Graphics2D g2d = (Graphics2D) image.getGraphics();
            if (this.context == null || this.context.isGameOver()) {
                Composite previousComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.33f));
                g2d.drawImage(imgBackgroundRotated, 0, 0, null);
                g2d.setComposite(previousComposite);
            } else {
                g2d.drawImage(imgBackgroundRotated, 0, 0, null);
            }
            /* draws a filled, blended, rect around the current
             * player, turn it off or on, just as you like it
             */
            /* if (this.context instanceof PlayerContext && playerContextAlphaColor != null) {
                //g2d.setColor(playerContextAlphaColor);
                //g2d.drawRect(0, 0, 319, 320);
                AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f);
                g2d.setComposite(myAlpha);
                g2d.fillRoundRect(0, 0, 320, 320, 20, 20);
                AlphaComposite noAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
                g2d.setComposite(noAlpha);
            } */


            for (int i = 0; i < gridArray.length; i++) {
                for (int j = 0; j < gridArray[0].length; j++) {
                    /* maybe you like it, turn it on
                     * this shows the grid
                     */
                    //gridArray[i][j].paint(g2d);


                    // paint the tower in the grid
                    if (gridArray[i][j].getTower() != null) {
                        gridArray[i][j].getTower().paint(g2d);
                    }
                }
            }

            g2d.dispose();
            clearImg = false;

        }
        // paint image

        g.drawImage(image, 0, 0, null);
    }

    /**
     * Sets a boolean so that the grid repaints its image.
     */
    public void clearImage() {
        this.clearImg = true;
    }

    /**
     * Rotates the map.
     * @param location the boardLocation
     */
    private void rotateMap(BoardLocation location) {
        Graphics2D bgGraphic = imgBackgroundRotated.createGraphics();
        
        switch (location) {
            case TOPLEFT:
            case CENTER:
                // top left is reading location
                bgGraphic.drawImage(imgBackground, 0, 0, null);
                break;
            case TOPRIGHT:
                for (int i = 0; i < mapWithPath.length; i++) {
                    mapWithPath[i] = new Point((x - 1) - (int) mapWithPath[i].getY(),
                            (int) mapWithPath[i].getX());
                }
                for (int i = 0; i < mapWithHoly.length; i++) {
                    mapWithHoly[i] = new Point((x - 1) - (int) mapWithHoly[i].getY(),
                            (int) mapWithHoly[i].getX());
                }

                bgGraphic.rotate(90 * Math.PI / 180, 160.5, 160.5);
                bgGraphic.drawImage(imgBackground, 0, 0, null);
                break;
            case BOTTOMRIGHT:
                for (int i = 0; i < mapWithPath.length; i++) {
                    mapWithPath[i] = new Point((x - 1) - (int) mapWithPath[i].getX(),
                            (x - 1) - (int) mapWithPath[i].getY());
                }
                for (int i = 0; i < mapWithHoly.length; i++) {
                    mapWithHoly[i] = new Point((x - 1) - (int) mapWithHoly[i].getX(),
                            (x - 1) - (int) mapWithHoly[i].getY());
                }
                bgGraphic.rotate(180 * Math.PI / 180, 160.5, 160.5);
                bgGraphic.drawImage(imgBackground, 0, 0, null);
                break;
            case BOTTOMLEFT:
                for (int i = 0; i < mapWithPath.length; i++) {
                    mapWithPath[i] = new Point((int) mapWithPath[i].getY(),
                            (x - 1) - (int) mapWithPath[i].getX());
                }
                for (int i = 0; i < mapWithHoly.length; i++) {
                    mapWithHoly[i] = new Point((int) mapWithHoly[i].getY(),
                            (x - 1) - (int) mapWithHoly[i].getX());
                }
                bgGraphic.rotate(270 * Math.PI / 180, 160.5, 160.5);
                bgGraphic.drawImage(imgBackground, 0, 0, null);
                break;

            default:
                logger.severe("Unknown BoardLocation");
                break;
        }
    }

    /**
     * removes the tower from the grid.
     * @param id towerID
     */
    public void removeTower(int id) {
        for (int i = 0; i < gridArray.length; i++) {
            for (int j = 0; j < gridArray[0].length; j++) {
                if (gridArray[i][j].getTower() != null) {
                    if (gridArray[i][j].getTower().getId() == id) {
                        gridArray[i][j] = new EmptyGrid(
                                i * Grid.SIZE, j * Grid.SIZE,
                                this.context);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Getter for a grid by position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the grid
     */
    public Grid getGrid(int x, int y) {
        x = x / Grid.SIZE;
        y = y / Grid.SIZE;
        if (x < gridArray.length && y < gridArray[0].length) {
            return this.gridArray[x][y];
        } else {
            return null;
        }
    }

    /**
     * @param path the path to set
     */
    public void setPath(Path path) {
        this.path = path;
    }

    /**
     * @return the path
     */
    public Path getPath() {
        return path;
    }
}
