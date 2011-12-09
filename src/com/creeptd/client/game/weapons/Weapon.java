package com.creeptd.client.game.weapons;

import java.awt.Graphics2D;

public interface Weapon {
	void update(long round);
	void paintEffect(Graphics2D graphics);
}
