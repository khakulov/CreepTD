package com.creeptd.client.game;

import com.creeptd.client.game.BoardLocation;
import com.creeptd.common.Constants;
import com.creeptd.common.Constants.Map;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class EmptyContext {
    private BoardLocation location;
    private GameBoard board;
    private Constants.Map map;
    /** The boards translation matrix */
    private AffineTransform translation = new AffineTransform();

    public EmptyContext(BoardLocation location, Constants.Map map) {
        this.location = location;
        this.map = map;
        this.board = new GameBoard(location, map, null); // GameBoard without context
    }

    public GameBoard getBoard() {
        return board;
    }

    public BoardLocation getLocation() {
        return location;
    }

    public Map getMap() {
        return map;
    }
    
    public void paint(Graphics2D g) {
        AffineTransform previousTransform = g.getTransform();
        translation.setToIdentity();
        translation.translate(this.getLocation().getX(), this.getLocation().getY());
        g.transform(translation);
        this.board.paint(g);
        g.setTransform(previousTransform);
    }
}
