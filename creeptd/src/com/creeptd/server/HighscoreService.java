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
package com.creeptd.server;

import com.creeptd.client.Core;
import com.creeptd.common.IConstants;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.log4j.Logger;

import com.creeptd.common.messages.server.HighscoreEntry;
import com.creeptd.common.messages.server.HighscoreResponseMessage;
import com.creeptd.common.messages.server.ScoreResponseMessage;
import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import com.creeptd.server.model.Player;

/**
 * 
 * Service for the High scores.
 * 
 */
public class HighscoreService {

    private static Logger logger = Logger.getLogger(HighscoreService.class);

    private HighscoreService() {
    }

    public static class ExtendedPlayer {

        private Player player;
        private int postion;

        public ExtendedPlayer(Player player, int postion) {
            super();
            this.player = player;
            this.postion = postion;
        }

        public Player getPlayer() {
            return this.player;
        }

        public int getPostion() {
            return this.postion;
        }
    }

    /**
     * @param playerName
     *            the username.
     * @return a ScoreResponseMessage with the user's data.
     */
    public static ScoreResponseMessage getScoreMessage(String playerName) {
        ScoreResponseMessage scoreResponseMessage = new ScoreResponseMessage();
        Player player = AuthenticationService.getPlayer(playerName);
        if (player != null) {
            scoreResponseMessage.setPlayerName(player.getName());
            scoreResponseMessage.setExperience(player.getExperience());
            scoreResponseMessage.setElopoints(player.getElopoints());
            scoreResponseMessage.setLastgameExperience(player.getLastgameExperience());
            scoreResponseMessage.setLastgameElopoints(player.getLastgameElopoints());
        }
        return scoreResponseMessage;
    }

    /**
     * @param offset
     * @return the actual HighscoreResponseMessage to send to clients.
     */
    public static HighscoreResponseMessage getHighscoreMessage(int offset) {
        HighscoreResponseMessage highscoreResponseMessage = new HighscoreResponseMessage();
        Set<HighscoreEntry> highscoreEntries = new HashSet<HighscoreEntry>();
        Set<Player> players = AuthenticationService.getPlayers(offset);
        for (Player player : players) {
            HighscoreEntry highscoreEntry = new HighscoreEntry();
            highscoreEntry.setPlayerName(player.getName());
            highscoreEntry.setExperience(player.getExperience());
            highscoreEntry.setElopoints(player.getElopoints());
            highscoreEntry.setLastgameExperience(player.getLastgameExperience());
            highscoreEntry.setLastgameElopoints(player.getLastgameElopoints());
            highscoreEntries.add(highscoreEntry);
        }
        highscoreResponseMessage.setHighscoreEntries(highscoreEntries);
        return highscoreResponseMessage;
    }

    /**
     * Updates the highscore table with the results of a game.
     *
     * @param playerNamePositionMap
     *            maps player names to position (position 1 means the player won
     *            the game etc)
     */
    public static void createHighscoreEntry(ArrayList<PlayerInGame> playerPositions, Game game) {
        try {
            EntityManager entityManager = entityManager = PersistenceManager.getInstance().getEntityManager();

            int[] experience = new int[playerPositions.size()];
            for (int i = 0; i < playerPositions.size(); i++) {
                experience[i] = playerPositions.get(i).getClient().getPlayerModel().getExperience();
            }

            int[] elopoints = new int[playerPositions.size()];
            for (int i = 0; i < playerPositions.size(); i++) {
                elopoints[i] = playerPositions.get(i).getClient().getPlayerModel().getElopoints();
            }
            
            EntityTransaction entityTransaction = entityManager.getTransaction();
            entityTransaction.begin();
            
            int[] newExperience = (game.getMode().equals(IConstants.Mode.TEAM2VS2)) ? calcExperienceTeam(experience) : calcExperience(experience);
            int[] newElopoints = (game.getMode().equals(IConstants.Mode.TEAM2VS2)) ? calcElopointsTeam(elopoints) : calcElopoints(elopoints);

            for (int i = 0; i < playerPositions.size(); i++) {
                playerPositions.get(i).getClient().getPlayerModel().setExperience((int) newExperience[i]);
                playerPositions.get(i).getClient().getPlayerModel().setElopoints((int) newElopoints[i]);
                playerPositions.get(i).getClient().getPlayerModel().setLastgameExperience((int) (newExperience[i] - experience[i]));
                playerPositions.get(i).getClient().getPlayerModel().setLastgameElopoints((int) (newElopoints[i] - elopoints[i]));
                playerPositions.get(i).getClient().getPlayerModel().setLastgameId(game.getGameId());
                entityManager.merge(playerPositions.get(i).getClient().getPlayerModel());
            }
            entityTransaction.commit();
            logger.debug("Highscores saved");
        } catch (Throwable t) {
            logger.error("Error while saving highscores", t);
        }
    }

    /**
     * Calculate elopoints for given match.
     *
     * @param eloPoints The players' elopoints before the match in correct winning order
     * @return
     */
    private static int[] calcElopoints(int[] eloPoints) {
        int[] newElopoints = eloPoints.clone();

        // Calculation is as following:
        // 2 player game is calculated as normal match
        // 3 player game is calculated as tournament:
        //   player 1 vs player 2, player 1 vs player 3, player 2 vs player 3
        //   for each of these matches we check for the winner (best position) and calculate points
        // 4 player game is calculated as 4 player tournament accordingly

        for (int p1 = 0; p1 < eloPoints.length; p1++) {
            for (int p2 = 0; p2 < eloPoints.length; p2++) {
                if (p2 != p1) { // Can't play against oneself
                    // ...but again everyone else
                    double p1_prob = getEloProbability(eloPoints[p1], eloPoints[p2]);
                    newElopoints[p1] += getNewElopoints(eloPoints[p1], p1 < p2 ? 1.0 : 0.0, p1_prob);
                }
            }
        }
        return newElopoints;
    }

    /**
     * Calculate  elopoints for given team match.
     *
     * @param eloPoints The players' elopoints before the match in correct winning order
     * @return
     */
    private static int[] calcElopointsTeam(int[] eloPoints) {
        int[] newElopoints = eloPoints.clone();

        newElopoints[0] += getNewElopoints(eloPoints[0], 1.0, getEloProbability(eloPoints[0], eloPoints[2])); // p1 vs p3
        newElopoints[0] += getNewElopoints(eloPoints[0], 1.0, getEloProbability(eloPoints[0], eloPoints[3])); // p1 vs p4

        newElopoints[1] += getNewElopoints(eloPoints[1], 1.0, getEloProbability(eloPoints[1], eloPoints[2])); // p2 vs p3
        newElopoints[1] += getNewElopoints(eloPoints[1], 1.0, getEloProbability(eloPoints[1], eloPoints[2])); // p2 vs p4

        newElopoints[2] += getNewElopoints(eloPoints[2], 0.0, getEloProbability(eloPoints[2], eloPoints[0])); // p3 vs p1
        newElopoints[2] += getNewElopoints(eloPoints[2], 0.0, getEloProbability(eloPoints[2], eloPoints[1])); // p3 vs p2

        newElopoints[3] += getNewElopoints(eloPoints[3], 0.0, getEloProbability(eloPoints[3], eloPoints[0])); // p4 vs p1
        newElopoints[3] += getNewElopoints(eloPoints[3], 0.0, getEloProbability(eloPoints[3], eloPoints[1])); // p4 vs p2

        return newElopoints;
    }

    /**
     * Calculate probability for a player to win.
     *
     * @param ra Current ELO points of player
     * @param rb Current ELO points of opponent
     * @return Probability for player to win against opponent
     */
    private static double getEloProbability(double ra, double rb) {
        return 1.0 / (1.0 + Math.pow(10, (rb - ra) / 400.0));
    }

    /**
     * Calculate change in ELO points.
     *
     * @param ro ELO points before match
     * @param sa 1.0 for victory, 0.5 for tie (there are none in CreepTD), 0.0 for defeat
     * @param ea Probability for player to win
     * @return Change in ELO points
     */
    private static int getNewElopoints(double ro, double sa, double ea) {
        // Modified ELO constant:
        // We calculate k linearly from 30 at ro=0 to 0 at ro=3000, but it can't
        // become less than 2 (eqal enemies receive 1 point) so that there is
        // finally a soft limit
        int k = (int) Math.round((double) 30 - (ro / 100.0));
        if (k < 2) {
            k = 2;
        }
        return (int) (Math.round((sa - ea) * k));
    }

    /**
     * Calculate experience with fixed point system.
     *
     * @param ePoints old points
     * @return
     */
    private static int[] calcExperience(int[] ePoints) {
        int[] newExperience = new int[ePoints.length];

        if (ePoints.length == 4) {
            newExperience[0] = ePoints[0] + 8;
            newExperience[1] = ePoints[1] + 4;
            newExperience[2] = ePoints[2] + 2;
            newExperience[3] = ePoints[3] + 0;
        } else if (ePoints.length == 3) {
            newExperience[0] = ePoints[0] + 6;
            newExperience[1] = ePoints[1] + 3;
            newExperience[2] = ePoints[2] + 0;
        } else if (ePoints.length == 2) {
            newExperience[0] = ePoints[0] + 4;
            newExperience[1] = ePoints[1] + 0;
        }

        return newExperience;
    }

    /**
     * Calculate experience with fixed point system in team mode.
     *
     * @param ePoints old points
     * @return
     */
    private static int[] calcExperienceTeam(int[] ePoints) {
        int[] newExperience = new int[ePoints.length];
        newExperience[0] = ePoints[0] + 6;
        newExperience[1] = ePoints[1] + 6;
        newExperience[2] = ePoints[2] + 0;
        newExperience[3] = ePoints[3] + 0;
        return newExperience;
    }
}
