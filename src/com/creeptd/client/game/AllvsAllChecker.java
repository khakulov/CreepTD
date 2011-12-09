package com.creeptd.client.game;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.creeptd.common.Constants;
import com.creeptd.common.messages.server.BuildCreepRoundMessage;

public class AllvsAllChecker {

    private static Logger logger = Logger.getLogger(GameContext.class.getName());

	private static class Entry {
		private long round;
		private int sender;
		private String type;
		
		
		public Entry(BuildCreepRoundMessage m) {
			this.round = m.getRoundId();
			this.sender = m.getSenderId();
			this.type = m.getCreepType();
		}
		
		public boolean check(BuildCreepRoundMessage m) {
			return this.round == m.getRoundId() && this.sender == m.getSenderId() && this.type.equals(m.getCreepType());
		}
	}
	
	private static List<Entry> list = new ArrayList<Entry>();
	
	public static boolean check(Game game, BuildCreepRoundMessage m) {
		// check only in AllvsAll mode
		if (game.getGameMode() != Constants.Mode.ALLVSALL)
			return false;
		
		// check only if there are more than 2 Players
		if (game.getPlayers().size() <= 2)
			return false;
		
		for (Entry element : list) {
			if (element.check(m)) {
				logger.info("checker - creep already checked up");
				return true;
			}
		}
		list.add(new Entry(m));
		logger.info("checker - new creep checked up");
		return false;
	}
	
	public static void garbageCollection(Game game, long roundId) {
		if ((game.getGameMode() != Constants.Mode.ALLVSALL) || (game.getPlayers().size() <= 2))
			return;

		logger.info("garbageCollection - size before clean: " + list.size());
		List<Entry> remove = new ArrayList<Entry>();
		for (Entry element : list) {
			if (element.round < roundId) {
				remove.add(element);
			}
		}
		list.removeAll(remove);
		logger.info("garbageCollection - size after clean: " + list.size());
	}
}
