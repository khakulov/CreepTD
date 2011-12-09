package com.creeptd.client.game;

import java.awt.Rectangle;

public enum BoardLocation {
    TOPLEFT(30, 30, 320, 320),
    BOTTOMLEFT(30, 350, 320, 320),
    TOPRIGHT(350, 30, 320, 320),
    BOTTOMRIGHT(350, 350, 320, 320),
    CENTER(190,190,320,320); // For solo play only

    /**
     * Get BoardLocation by index.
     *
     * @param i Location index
     * @return BoardLocation or null if out of bounds
     */
    public static BoardLocation forIndex(int i) {
        if (i==0) return TOPLEFT;
        if (i==1) return TOPRIGHT;
        if (i==2) return BOTTOMRIGHT;
        if (i==3) return BOTTOMLEFT;
        return null;
    }

    /** X coordinate */
    private final double x;
    /** Y coordinate */
    private final double y;
    /** Width */
    private final int width;
    /** Height */
    private final int height;
    /** Bound rectangle */
    private final Rectangle bounds;

    /**
     * BoardLocation constructor.
     *
     * @param x the x position
     * @param y the y position
     * @param width the width
     * @param height the height
     */
    BoardLocation(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bounds = new Rectangle((int) x, (int) y, width, height);
    }

    /**
     * Getter for the x location.
     *
     * @return the x location
     */
    public double getX() {
        return this.x;
    }

    /**
     * Getter for the y location.
     *
     * @return the x location
     */
    public double getY() {
        return this.y;
    }

    /**
     * Getter for the width.
     *
     * @return the the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Getter for the the height.
     *
     * @return the the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Getter for the bounding box.
     *
     * @return A Rectangle specifying the bounding box
     */
    public Rectangle getBounds() {
        return bounds;
    }

    public String toString() {
        return this.name();
    }
}