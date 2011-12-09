package com.creeptd.client.panel.game;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.creeptd.client.i18n.Translator.*;

public class GameInfoPanel extends JPanel {

	private static final long serialVersionUID = -4446563975717092495L;
	private JLabel incomeCounter;
	private JLabel incomeRound;
	private final URL imageURL = this.getClass().getClassLoader()
			.getResource("com/creeptd/client/resources/panel/icon_round.gif");

	/**
	 * Creates a new GameInfoPanel.
	 */
	public GameInfoPanel() {

		this.setLayout(null);
		this.setBackground(Color.BLACK);

		JLabel spectator = new JLabel();
		spectator.setForeground(Color.WHITE);
		spectator.setBounds(5, 5, 250, 15);
		spectator.setText(_("Spectator"));

		Map<String, String> args = new HashMap<String, String>();
		args.put("t", "...");
		this.incomeCounter = new JLabel();
		this.incomeCounter.setForeground(Color.WHITE);
		this.incomeCounter.setBounds(5, 25, 250, 15);
		this.incomeCounter.setText(_("New income in %t%", args));
		this.incomeCounter.setToolTipText(_("Time to next round(in seconds)."));

		this.incomeRound = new JLabel();
		this.incomeRound.setForeground(Color.WHITE);
		this.incomeRound.setBounds(5, 45, 250, 15);

		this.incomeRound.setText("<html><img src=\"" + this.imageURL + "\"> &nbsp;0</html>");
		this.incomeRound.setToolTipText(_("Current income round."));
		
		this.add(spectator);
		this.add(this.incomeCounter);
		this.add(this.incomeRound);
	}

	public void setIncomeCounter(int counter) {
		Map<String, String> args = new HashMap<String, String>();
		args.put("t", counter + "");
		this.incomeCounter.setText(_("New income in %t%", args));
	}

	public void roundChanged(int incomeRound) {
		this.incomeRound.setText("<html><img src=\"" + this.imageURL + "\"> &nbsp;" + incomeRound + "</html>");
		this.repaint();
	}
}
