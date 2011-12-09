package com.creeptd.client.game;

import java.util.logging.Logger;

/**
 * Main GameLoop for updates and repaint.
 * Sync with Server tick and frame rate approximation.
 */
public class GameLoop extends Thread {
    private static Logger logger = Logger.getLogger(GameLoop.class.getName());

	private static final int NO_DELAYS_PER_YIELD = 10;
	private static final int MAX_FRAME_SKIPS = 5;

	private boolean terminate;
	private Game game;
	private long maxRound = 0;

	public GameLoop(Game game) {
		this.game = game;
	}

	@Override
	public void run() {
		logger.info("GameLoop run...");

		this.terminate = false;

		long beforeTime, afterTime, timeDiff, sleepTime;
		long overSleepTime = 0L;
		long excess = 0L;
		int noDelays = 0;

		beforeTime = System.nanoTime();
		while (!this.terminate) {
			long period = this.game.getSpeed() * 1000000;
			try {
	            if (this.game.isPaused()) {
	                Thread.sleep(this.game.getSpeed());
	                this.game.gameRender();
	                beforeTime = System.nanoTime();
	                continue;
	            }
				if (this.game.getRoundId() >= this.maxRound) {
					Thread.sleep(this.game.getSpeed()/4);
				}
	
				this.game.gameUpdate(); // updates the gamestate
				this.game.gameRender(); // paints new screen in a buffer
				afterTime = System.nanoTime();
	
				timeDiff = afterTime - beforeTime;
				sleepTime = (period - timeDiff) - overSleepTime;
	
				if (sleepTime > 0) { // some time left in this cycle
					Thread.sleep(sleepTime / 1000000L); // nano -> ms
					overSleepTime = (System.nanoTime() - afterTime) - sleepTime;
				} else { // sleepTime <= 0; frame took longer than the period
					excess -= sleepTime; // store excess time value
					overSleepTime = 0L;
	
					if (++noDelays >= NO_DELAYS_PER_YIELD) {
						Thread.yield(); // give another thread a chance to run
						noDelays = 0;
					}
				}
	
				beforeTime = System.nanoTime();
	
				/*
				 * If frame animation is taking too long, update the game state
				 * without rendering it, to get the updates/sec nearer to the
				 * required FPS.
				 */
				int skips = 0;
				while ((excess > period) && (skips < MAX_FRAME_SKIPS)) {
					excess -= period;
					this.game.gameUpdate(); // update state but don't render
					skips++;
				}
			} catch (InterruptedException e) {
				this.terminate = true;
			}
		}

		logger.info("GameLoop stop...");
	}

	public void setMaxRound(long maxRound) {
		this.maxRound = maxRound;
	}

	public void terminate() {
		this.terminate = true;
		this.interrupt();
	}
}
