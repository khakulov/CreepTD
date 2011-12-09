package com.creeptd.client.game.strategies;

import java.util.ArrayList;
import java.util.List;

import com.creeptd.client.game.creeps.Creep;
import com.creeptd.client.game.grids.Grid;
import com.creeptd.client.game.towers.Tower;

public abstract class AbstractStrategy implements Strategy {

	private boolean locked;
	protected Tower tower;
	protected Creep lastCreep;

	public AbstractStrategy(Tower tower) {
		this.tower = tower;
		this.lastCreep = null;
		this.locked = false;
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public void unlock() {
		this.locked = false;
	}

	@Override
	public Creep findCreep() {
		if (this.locked && this.lastCreep != null && tower.getContext().getCreeps().contains(this.lastCreep)
				&& this.lastCreep.isActive() && this.isCreepInRange(this.lastCreep)) {
			return this.lastCreep;
		}
		List<Creep> creeps = new ArrayList<Creep>();
		for (Creep creep : tower.getContext().getCreeps()) {
			if (creep.isActive() && this.isCreepInRange(creep))
				creeps.add(creep);
		}
		this.lastCreep = this.findNewCreep(creeps);
		return this.lastCreep;
	}
	
	protected abstract Creep findNewCreep(List<Creep> creeps);

	private boolean isCreepInRange(Creep creep) {
        float distanceMin = this.tower.getType().getRange() * this.tower.getType().getRange();
        float dX = (creep.getX() + Grid.SIZE / 2) - (this.tower.getGrid().getLocation()[0] + Grid.SIZE / 2);
        float dY = (creep.getY() + Grid.SIZE / 2) - (this.tower.getGrid().getLocation()[1] + Grid.SIZE / 2);
        float dist = dX * dX + dY * dY;
        return dist < distanceMin;
	}
}
