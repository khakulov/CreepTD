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
package com.creeptd.client.game.towers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.strategies.Strategy;
import com.creeptd.client.game.strategies.StrategyFactory;
import com.creeptd.client.game.weapons.Weapon;
import com.creeptd.client.game.weapons.WeaponFactory;
import com.creeptd.client.util.Cache;
import com.creeptd.common.Constants;
import com.creeptd.common.Constants.Towers;

/**
 * Implements the basic methods for a tower.
 */
public class TowerImpl implements Tower {

	private int id;
	private Constants.Towers type;

	protected Strategy strategy;
	private Weapon weapon;

	private int price;

	private int buildTime;
	private int upgradeTime;
	private int sellTime;
	private int changeStrategyTime;

	private Strategy newStrategy;

	private GameContext context;
	private Grid grid;

	protected BufferedImage image;
	private AffineTransform translation = new AffineTransform();

	/**
	 * Creates a new instance of AbstractTower.
	 * 
	 * @param context
	 *            the gameContext for the tower
	 * @param type
	 *            the type of the tower
	 * @param grid
	 *            the grid where the tower is placed
	 */
	protected TowerImpl(Constants.Towers type, GameContext context, Grid grid) {
		this.buildTime = Constants.USER_ACTION_DELAY;
		this.upgradeTime = 0;
		this.changeStrategyTime = 0;
		this.context = context;
		this.type = type;
		this.price = type.getPrice();
		this.grid = grid;
		grid.setTower(this);
		this.strategy = StrategyFactory.createStrategy(this); 
		this.weapon = WeaponFactory.createWeapon(this);

		loadImage();

		this.translation.setToIdentity();
		this.translation.translate(grid.getLocation()[0], grid.getLocation()[1]);
	}

	private void loadImage() {
		if (Cache.getInstance().hasTowerImg(this.getType())) {
			this.image = Cache.getInstance().getTowerImg(this.getType());
			return;
		}
		try {
			this.image = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(this.getType().getImageFileName())); 
			Cache.getInstance().putTowerImg(this.getType(), this.image);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	public void paint(Graphics2D g) {

		AffineTransform save = g.getTransform();

		g.transform(translation);

		g.drawImage(image, 0, 0, null);

		g.setTransform(save);
	}

	/**
	 * {@inheritDoc}
	 */
	public void paintEffect(Graphics2D g) {
		AffineTransform save = g.getTransform();

		g.transform(translation);

		// works only if a client does not lag too much
		// or the build time can be bigger than USER_ACTION_DELAY
		if (this.buildTime > 0) {
			g.setColor(Color.BLACK);
			g.fillRect(20 - 20 * this.buildTime / Constants.USER_ACTION_DELAY, 1, 20 * this.buildTime
					/ Constants.USER_ACTION_DELAY, 19);
		}

		if (this.upgradeTime > 0) {
			g.setColor(Color.BLUE);
			g.fillRect(1, 1, 20 * this.upgradeTime / Constants.USER_ACTION_DELAY, 3);
		}

		if (this.sellTime > 0) {
			g.setColor(Color.RED);
			g.fillRect(1, 1, 20 * this.sellTime / Constants.USER_ACTION_DELAY, 3);
		}

		if (this.changeStrategyTime > 0) {
			g.setColor(Color.ORANGE);
			g.fillRect(1, 1, 20 * this.changeStrategyTime / Constants.USER_ACTION_DELAY, 3);
		}

		g.setTransform(save);
		
		if (!this.context.isGameOver()) {
			this.weapon.paintEffect(g);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void update(long roundId) {
		// upgrade and build animation
		if (this.buildTime > 0) {
			this.buildTime--;
			if (this.buildTime > 0) return;
		}

		// upgrade animation
		if (this.upgradeTime > 0) {
			this.upgradeTime--;
			if (this.upgradeTime == 0) {
				this.upgrade();
			}
		}

		// sell animation
		if (sellTime > 0) {
			sellTime--;
			if (sellTime == 0) {
				this.context.setCredits(this.context.getCredits() + (int) (this.price * 0.75));
				this.grid.setOccupiedStatus(false);
				getContext().removeTower((Tower) this);
			}
		}

		// strategy change update
		if (this.changeStrategyTime > 0) {
			this.changeStrategyTime--;
			if (this.changeStrategyTime == 0) {
				this.strategy = this.newStrategy;
				this.newStrategy = null;
			}
		}

		this.weapon.update(roundId);
	}

	private void upgrade() {

		Towers nameOfNextTower = this.type.getNext();

		if (nameOfNextTower != null) {
			this.price = this.price + nameOfNextTower.getPrice();
			this.type = nameOfNextTower;

			if (Cache.getInstance().hasArrayTowerImg(this.getType())) {
				this.setImage(Cache.getInstance().getTowerImg(this.getType()));
			} else {
				this.image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB_PRE);
				loadImage();
			}

			this.context.getGameBoard().clearImage();

		}

	}

	/**
	 * {@inheritDoc}
	 */
	public void build(int buildTime) {
		this.buildTime = buildTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public void upgrade(int upgradeTime) {
		this.upgradeTime = upgradeTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public void sell(int sellTime) {
		this.sellTime = sellTime;
	}

	/**
	 * {@inheritDoc}
	 */
	public void changeStrategy(int changeTime, Strategy fcs) {
		this.changeStrategyTime = changeTime;
		this.newStrategy = fcs;
	}

	/**
	 * @return the type
	 */
	public Constants.Towers getType() {
		return type;
	}

	/**
	 * @return the grid
	 */
	public Grid getGrid() {
		return grid;
	}

	public Strategy getStrategy() {
		return strategy;
	}
	/**
	 * @param image
	 *            the image to set
	 */
	protected void setImage(BufferedImage image) {
		this.image = image;
	}

	/**
	 * @return the image
	 */
	protected BufferedImage getImage() {
		return image;
	}

	/**
	 * @return the context
	 */
	public GameContext getContext() {
		return context;
	}
}
