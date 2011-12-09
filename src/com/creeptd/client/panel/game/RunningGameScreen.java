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
package com.creeptd.client.panel.game;

import static com.creeptd.client.i18n.Translator._;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.creeptd.client.Core;
import com.creeptd.client.game.Game;
import com.creeptd.client.network.MessageListener;
import com.creeptd.client.network.NetworkFakeImpl;
import com.creeptd.client.network.NetworkImpl;
import com.creeptd.client.panel.LobbyScreen;
import com.creeptd.client.panel.Screen;
import com.creeptd.client.panel.common.Button;
import com.creeptd.client.util.JNLP;
import com.creeptd.common.messages.client.ExitGameMessage;
import com.creeptd.common.messages.server.PlayerQuitMessage;
import com.creeptd.common.messages.server.ServerChatMessage;
import com.creeptd.common.messages.server.ServerMessage;

/**
 * The GamePanel class is the container for all other game related panels.
 * 
 * @author philipp
 * 
 */
public class RunningGameScreen extends Screen implements MessageListener, ChangeListener {

    private static final long serialVersionUID = -5720168895966087312L;
    private static final int[] SPEEDS = {100, 75, 50, 40, 20, 10, 5, 2};
    private GameCanvas boardPanel;
    private ChatPanel chatPanel;
    private GameInfoPanel gameInfoPanel;

    private Button quit;
    private Button options;
    private Button save;
	private JSlider framesPerSecond;

    private Game game;


    /**
     * Creates a new instance of GamePanel.
     */
    public RunningGameScreen() {
        super();
        this.setPreferredSize(Core.SCREENSIZE);
        this.setForeground(Color.BLACK);
        this.setSize(new Dimension(933, 700));
        this.initComponents();
    }

    /**
     * init all the components for the panel.
     */
    private void initComponents() {
        this.setLayout(null);

        // BoardPanel
        this.boardPanel = new GameCanvas(700, 700);
        this.boardPanel.setBounds(0, 0, 700, 700);
        this.add(boardPanel);

        // GameInfoPanel
        this.gameInfoPanel = new GameInfoPanel();
        this.gameInfoPanel.setBounds(700, 0, 235, 200);
        this.add(gameInfoPanel);

        this.quit = new Button(_("Quit"));
        this.quit.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.quit.setBounds(143, 10, 80, 20);
        this.quit.setBackground(Color.BLACK);
        this.quit.setForeground(Color.RED);
        this.quit.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	Core.getInstance().getNetwork().sendMessage(new ExitGameMessage());
                if (!game.isRunning() && game.getPlayers().size() > 1 && (Core.getInstance().getNetwork() instanceof NetworkImpl)) {
                	String url = "http://www.creeptd.com/game?lastgameof=" + game.getWinner();
                	Core.getInstance().openBrowser(url);
                }
                if (game.isRunning()) {
                    game.terminate();
                }
            	Core.getInstance().popScreen();
            }
        });
        this.gameInfoPanel.add(this.quit);

        this.options = new Button(_("Options"));
        this.options.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.options.setBounds(143, 35, 80, 20);
        this.options.setBackground(Color.BLACK);
        this.options.setForeground(Color.YELLOW);
        this.options.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                LobbyScreen.openOptionsPanel();
            }
        });
        this.gameInfoPanel.add(this.options);
        
        this.save = new Button(_("Save"));
        this.save.setFont(new Font("Helvetica", Font.PLAIN, 9));
        this.save.setBounds(143, 60, 80, 20);
        this.save.setBackground(Color.BLACK);
        this.save.setForeground(Color.GREEN);
        this.save.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	if (!game.isRunning()) {
            		JNLP.saveFile(game.getMap(), game.getGameMode(), game.getPlayers(), game.getPlayersOrder(), game.getHistory());
            	}
            }
        });
        this.gameInfoPanel.add(this.save);

        if (Core.getInstance().getNetwork() instanceof NetworkFakeImpl) {
			this.framesPerSecond = new JSlider(JSlider.HORIZONTAL, 0, 7, 2);
			this.framesPerSecond.addChangeListener(this);
			this.framesPerSecond.setMajorTickSpacing(1);
			this.framesPerSecond.setSnapToTicks(true);
			this.framesPerSecond.setPaintTicks(true);
	
			this.framesPerSecond.setFont(new Font("Helvetica", Font.PLAIN, 9));
			this.framesPerSecond.setBounds(0, 85, 235, 115);
	        this.framesPerSecond.setBackground(Color.BLACK);
	        this.framesPerSecond.setForeground(Color.GREEN);
	
			Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put( new Integer( 7 ), new JLabel("Fast") );
			labelTable.put( new Integer( 0 ), new JLabel("Slow") );
			this.framesPerSecond.setLabelTable( labelTable );
			
			this.framesPerSecond.setPaintLabels(true);
	
	    	this.gameInfoPanel.add(this.framesPerSecond);
        }

        // ChatPanel
        this.chatPanel = new ChatPanel(235, 500);
        this.chatPanel.setBounds(700, 200, this.chatPanel.getWidth(), this.chatPanel.getHeight());
        this.add(chatPanel);

        this.doLayout();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end() {
        // remove the game from the network
    	Core.getInstance().getNetwork().removeListener(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
    	Core.getInstance().getNetwork().addListener(this);
        game = new Game(this);
    }

    /**
     * @param boardPanel
     *            the boardPanel to set
     */
    public void setBoardPanel(GameCanvas boardPanel) {
        this.boardPanel = boardPanel;
    }

    /**
     * @return the boardPanel
     */
    public GameCanvas getBoardPanel() {
        return boardPanel;
    }

    /**
     * @return the chatPanel
     */
    public ChatPanel getChatPanel() {
        return chatPanel;
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
    public Game getGame() {
        return game;
    }

	@Override
	public void update(ServerMessage m) {
		if (m instanceof ServerChatMessage) {
	        ServerChatMessage scm = (ServerChatMessage) m;
	        String msg = scm.getMessage();
	        if (scm.getTranslate()) {
	            int idx = msg.indexOf("#");
	            if (idx >= 0) {
	                int round = Integer.parseInt(msg.substring(idx+1));
	                Map<String,String> args = new HashMap<String,String>();
	                args.put("n",""+round);
	                msg = "<span style='font-weight:bold'>"+_("Round %n%", args)+"</span>: "+_(msg.substring(0, idx).trim());
	            } else {
	                msg = _(msg);
	            }
	        }

	        this.chatPanel.addMessage(scm.getPlayerName(), msg, scm.isAction());
		} else if (m instanceof PlayerQuitMessage) {
            PlayerQuitMessage pqm = (PlayerQuitMessage) m;

            Map<String,String> args = new HashMap<String,String>();
            args.put("name", "<b>"+pqm.getPlayerName()+"</b>");

            this.chatPanel.addMessage("Server", _("%name% has left...", args), false);
        }
	}

	@Override
	public void stateChanged(ChangeEvent e) {
	   JSlider source = (JSlider)e.getSource();
	    if (!source.getValueIsAdjusting()) {
	        int fps = source.getValue();
	        this.game.setSpeed(SPEEDS[fps]);
	    }
	}

}
