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
package com.creeptd.client.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.creeptd.client.Core;
import com.creeptd.client.game.GameContext;
import com.creeptd.client.game.GameLoop;
import com.creeptd.client.sound.SoundManagement;
import com.creeptd.client.tower.FindClosestCreep;
import com.creeptd.client.tower.FindCreepStrategy;
import com.creeptd.client.tower.FindFarthestCreep;
import com.creeptd.client.tower.FindFastestCreep;
import com.creeptd.client.tower.FindStrongestCreep;
import com.creeptd.client.tower.FindWeakestCreep;
import com.creeptd.client.tower.StrategyFactory;
import com.creeptd.common.IConstants;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.client.SendMessageMessage;

/**
 * The GamePanel class is the container for all other game related panels.
 * 
 * @author philipp
 * 
 */
public class GamePanel extends GameScreen {

    private static final long serialVersionUID = -5720168895966087312L;
    private BoardPanel boardPanel;
    private TowerSelPanel towerPanel;
    private CreepSelPanel creepPanel;
    private ChatPanel chatPanel;
    private GameInfoPanel gameInfoPanel;
    private BuildTowerInfoPanel buildTowerInfoPanel;
    private SelectTowerInfoPanel selectTowerInfoPanel;
    private BuildTowerInfoPanel towerInfoPanel;
    private CreepInfoPanel creepInfoPanel;
    private NoInfoPanel noInfoPanel;
    private JPanel lastTowerInfoPanel;
    private JButton quit;
    private JButton options;
    private GameContext context;
    private GameLoop loop;

    /**
     * @return the context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    /**
     * Creates a new instance of GamePanel.
     */
    public GamePanel() {
        super();
        this.setPreferredSize(Core.SCREENSIZE);
        this.setForeground(Color.BLACK);
        this.setSize(new Dimension(933, 700));
        this.initComponents();
        this.setupShortcuts();
    }

    /**
     * init all the components for the panel.
     */
    private void initComponents() {
        this.setLayout(null);

        // BoardPanel
        this.boardPanel = new BoardPanel(700, 700);
        this.boardPanel.setBounds(0, 0, 700, 700);
        this.add(boardPanel);

        // GameInfoPanel
        this.gameInfoPanel = new GameInfoPanel();
        this.gameInfoPanel.setBounds(700, 0, 233, 100);
        this.add(gameInfoPanel);

        this.quit = new JButton("Quit");
        this.quit.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.quit.setBounds(143, 10, 80, 20);
        this.quit.setEnabled(false);
        this.quit.setBackground(Color.BLACK);
        this.quit.setForeground(Color.RED);
        this.quit.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // sends a messages to all players that we have left
                SendMessageMessage chatMsg = new SendMessageMessage();
                chatMsg.setClientId(context.getPlayerId());
                chatMsg.setMessage("has left the game");
                getCore().getNetwork().sendMessage(chatMsg);

                getCore().getNetwork().sendMessage(new ExitGameMessage());
                if (!loop.isRunning()) {
                    getCore().switchScreen(new GameResultPanel(loop.getPlayers()));
                } else {
                    getCore().popScreen();
                }
                if (loop.isRunning()) {
                    loop.setRunning(false);
                }
                loop.setGameOver(false);
            }
        });

        this.options = new JButton("Options");
        this.options.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.options.setBounds(143, 35, 80, 20);
        this.options.setBackground(Color.BLACK);
        this.options.setForeground(Color.YELLOW);
        this.options.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                GameLobby.optionsFrame.setVisible(true);
            }
        });

        this.gameInfoPanel.add(this.options);
        this.gameInfoPanel.add(this.quit);

        // TowerSelPanel
        this.towerPanel = new TowerSelPanel(this, 233, 100);
        this.towerPanel.setBounds(700, 100, 233, 100);
        this.add(towerPanel);

        // buildTowerInfoPanel
        this.buildTowerInfoPanel = new BuildTowerInfoPanel(this, 233, 125);
        this.buildTowerInfoPanel.setBounds(700, 200, 233, 125);
        this.buildTowerInfoPanel.setVisible(false);
        this.add(buildTowerInfoPanel);
        this.setLastTowerInfoPanel(buildTowerInfoPanel);

        // towerInfoPanel
        this.towerInfoPanel = new BuildTowerInfoPanel(this, 233, 125);
        this.towerInfoPanel.setBounds(700, 200, 233, 125);
        this.towerInfoPanel.setVisible(false);
        this.add(towerInfoPanel);

        // buildTowerInfoPanel
        this.selectTowerInfoPanel = new SelectTowerInfoPanel(this, 233, 125);
        this.selectTowerInfoPanel.setBounds(700, 200, 233, 125);
        this.selectTowerInfoPanel.setVisible(false);
        this.add(selectTowerInfoPanel);

        // CreepInfoPanel
        this.creepInfoPanel = new CreepInfoPanel(this, 233, 125);
        this.creepInfoPanel.setBounds(700, 200, 233, 125);
        this.creepInfoPanel.setVisible(false);
        this.add(creepInfoPanel);

        // NoInfoPanel
        this.noInfoPanel = new NoInfoPanel(this, 233, 125);
        this.noInfoPanel.setBounds(700, 200, 233, 125);
        this.noInfoPanel.setVisible(true);
        this.add(noInfoPanel);

        // CreepSelPanel
        this.creepPanel = new CreepSelPanel(this, 233, 120);
        this.creepPanel.setBounds(700, 325, 233, 120);
        this.add(creepPanel);

        // ChatPanel
        this.chatPanel = new ChatPanel(this, 233, 255);
        this.chatPanel.setBounds(700, 445, 233, 255);
        this.add(chatPanel);

        this.doLayout();
    }

    @SuppressWarnings("serial")
    private void setupShortcuts() {
        // Quit
        addShortcut("ESCAPE", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                leave();
            }
        });
        //Upgrade
        addShortcut("U", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                context.upgradeTower();
            }
        });
        //Sell
        addShortcut("I", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                context.sellTower();
            }
        });
        //Strategies
        //Clostest
        addShortcut("K", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = StrategyFactory.getStrategyForName(FindClosestCreep.class.getSimpleName(), context.getSelectedTower());
                strategy.setCreepLock(context.getSelectedTower().getStrategy().isCreepLock());
                context.setStrategy(strategy);
            }
        });
        //Farthest
        addShortcut("0", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = StrategyFactory.getStrategyForName(FindFarthestCreep.class.getSimpleName(), context.getSelectedTower());
                strategy.setCreepLock(context.getSelectedTower().getStrategy().isCreepLock());
                context.setStrategy(strategy);
            }
        });
        //Fastest
        addShortcut("L", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = StrategyFactory.getStrategyForName(FindFastestCreep.class.getSimpleName(), context.getSelectedTower());
                strategy.setCreepLock(context.getSelectedTower().getStrategy().isCreepLock());
                context.setStrategy(strategy);
            }
        });
        //Strongest
        addShortcut("P", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = StrategyFactory.getStrategyForName(FindStrongestCreep.class.getSimpleName(), context.getSelectedTower());
                strategy.setCreepLock(context.getSelectedTower().getStrategy().isCreepLock());
                context.setStrategy(strategy);
            }
        });
        //Weakest
        addShortcut("O", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = StrategyFactory.getStrategyForName(FindWeakestCreep.class.getSimpleName(), context.getSelectedTower());
                strategy.setCreepLock(context.getSelectedTower().getStrategy().isCreepLock());
                context.setStrategy(strategy);
            }
        });

        //Lock
        addShortcut("9", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {
                FindCreepStrategy strategy = context.getSelectedTower().getStrategy();
                strategy.setCreepLock(!strategy.isCreepLock());
                context.setStrategy(strategy);
            }
        });

        /**
         *
         * private inner classes to automate send-creep shortcuts
         *
         */
        class SendAction extends AbstractAction {

            protected IConstants.Creeps type;
            protected GamePanel panel;

            public SendAction(IConstants.Creeps type, GamePanel panel) {
                this.type = type;
                this.panel = panel;
            }

            public void actionPerformed(ActionEvent event) {

                GameContext conText = this.panel.getContext();
                if (!conText.isDead() && conText.readyForNewCreep()) {
                    conText.sendCreep(type);
                }

            }
        }
        class SendWaveAction extends SendAction {

            public SendWaveAction(IConstants.Creeps type, GamePanel panel) {
                super(type, panel);
            }

            public void actionPerformed(ActionEvent event) {

                GameContext conText = this.panel.getContext();
                if (!conText.isDead() && conText.readyForNewWave()) {
                    conText.sendCreepsWave(type);
                }
            }
        }

        class BuildAction extends AbstractAction {

            protected IConstants.Towers type;
            protected GamePanel panel;

            public BuildAction(IConstants.Towers type, GamePanel panel) {
                this.type = type;
                this.panel = panel;
            }

            public void actionPerformed(ActionEvent event) {
                GameContext conText = this.panel.getContext();
                if (!conText.isDead()) {
                    conText.buyTower(type);
                }
            }
        }

        //Send Creeps
        IConstants.Creeps[] creeps = IConstants.Creeps.values();
        String[] shortcuts = {"1", "2", "3", "4", "Q", "W", "E", "R", "A", "S", "D", "F", "Y", "X", "C", "V"};

        //Sanity check
        if (creeps.length != shortcuts.length) {
            throw new RuntimeException("Non-equal number of creeps and shortcuts.");
        }
        //Add the shortcuts
        for (int i = 0; i < creeps.length; i++) {
            addShortcut(shortcuts[i], new SendAction(creeps[i], this));
            addShortcut("shift " + shortcuts[i], new SendWaveAction(creeps[i], this));
        }

        // Tower shortcuts
        IConstants.Towers[] tower = IConstants.Towers.values();
        String[] shortcutsT = {"G", "H", "J", "B", "N", "M"};

        //Add the shortcuts
        addShortcut(shortcutsT[0], new BuildAction(tower[3], this));
        addShortcut(shortcutsT[1], new BuildAction(tower[7], this));
        addShortcut(shortcutsT[2], new BuildAction(tower[11], this));
        addShortcut(shortcutsT[3], new BuildAction(tower[15], this));
        addShortcut(shortcutsT[4], new BuildAction(tower[19], this));
        addShortcut(shortcutsT[5], new BuildAction(tower[21], this));
    }

    /**
     * leaves the game.
     */
    private void leave() {
        if (!loop.isRunning()) {
            // sends a messages to all players that we have left
            SendMessageMessage chatMsg = new SendMessageMessage();
            chatMsg.setClientId(context.getPlayerId());
            chatMsg.setMessage("has left the game");
            getCore().getNetwork().sendMessage(chatMsg);

            loop.setGameOver(false);
            this.getCore().getNetwork().sendMessage(new ExitGameMessage());
            this.getCore().switchScreen(new GameResultPanel(loop.getPlayers()));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        // remove the game from the network
        this.getCore().getNetwork().removeListener(loop);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        waitFromInitToThis100ms();
        loop = new GameLoop(this, getCore().getNetwork(), this.soundM);
    }

    /**
     * @param boardPanel
     *            the boardPanel to set
     */
    public void setBoardPanel(BoardPanel boardPanel) {
        this.boardPanel = boardPanel;
    }

    /**
     * @return the boardPanel
     */
    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    /**
     * @return the towerPanel
     */
    public TowerSelPanel getTowerPanel() {
        return towerPanel;
    }

    /**
     * @return the creepPanel
     */
    public CreepSelPanel getCreepPanel() {
        return creepPanel;
    }

    /**
     * @return the chatPanel
     */
    public ChatPanel getChatPanel() {
        return chatPanel;
    }

    /**
     * @param gameInfoPanel
     *            the gameInfoPanel to set
     */
    public void setGameInfoPanel(GameInfoPanel gameInfoPanel) {
        this.gameInfoPanel = gameInfoPanel;
    }

    /**
     * @return the gameInfoPanel
     */
    public GameInfoPanel getGameInfoPanel() {
        return gameInfoPanel;
    }

    /**
     * @return the loop
     */
    public GameLoop getLoop() {
        return loop;
    }

    /**
     * @return the buildTowerInfoPanel
     */
    public BuildTowerInfoPanel getBuildTowerInfoPanel() {
        return buildTowerInfoPanel;
    }

    /**
     * @return the selectTowerInfoPanel
     */
    public SelectTowerInfoPanel getSelectTowerInfoPanel() {
        return selectTowerInfoPanel;
    }

    /**
     * @return the creepInfoPanel
     */
    public CreepInfoPanel getCreepInfoPanel() {
        return creepInfoPanel;
    }

    /**
     * @return the lastTowerInfoPanel
     */
    public JPanel getLastTowerInfoPanel() {
        return lastTowerInfoPanel;
    }

    /**
     * @param lastTowerInfoPanel
     *            the lastTowerInfoPanel to set
     */
    public void setLastTowerInfoPanel(JPanel lastTowerInfoPanel) {
        this.lastTowerInfoPanel = lastTowerInfoPanel;
    }

    /**
     * @return the noInfoPanel
     */
    public NoInfoPanel getNoInfoPanel() {
        return noInfoPanel;
    }

    /**
     * @return the towerInfoPanel
     */
    public BuildTowerInfoPanel getTowerInfoPanel() {
        return towerInfoPanel;
    }

    /**
     * Setter for SoundManagement.
     *
     * @param m
     *            to specify the SoundManagement Object
     */
    public void setSoundManagementObject(SoundManagement m) {
        this.soundM = m;
    }

    /**
     * @return the quit
     */
    public JButton getQuit() {
        return quit;
    }

    /**
     * @param quit
     *            the quit to set
     */
    public void setQuit(JButton quit) {
        this.quit = quit;
    }

    /* Have to be overriden to avoid execution of shortcuts in chat */
    @SuppressWarnings("serial")
    @Override
    public void addShortcut(String key, Action action) {
        super.addShortcut(key, action);
        KeyStroke keyStroke = KeyStroke.getKeyStroke(key);
        chatPanel.getMessageField().getInputMap().put(keyStroke, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // nothing
            }
        });
    }
}
