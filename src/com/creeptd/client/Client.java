package com.creeptd.client;

import com.creeptd.common.Constants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class Client extends JFrame {
	private static final long serialVersionUID = -4733308035673939732L;

	public static void main(String[] args) {
		if (args.length > 2) {
			System.err.println("Wrong command line arguments");
			System.out.println("Using default configuration...");
			Core.setHost(Constants.DEFAULT_SERVER_HOST);
			Core.setPort(Constants.DEFAULT_SERVER_PORT);
		} else if (args.length == 2) {
			Core.setHost(args[0]);
			Core.setPort(Integer.parseInt(args[1]));
		} else if (args.length == 1) {
			Core.setHost(args[0]);
			Core.setPort(Constants.DEFAULT_SERVER_PORT);
		} else if (args.length == 0) {
			Core.setHost(Constants.DEFAULT_SERVER_HOST);
			Core.setPort(Constants.DEFAULT_SERVER_PORT);
		}
		System.out.println("Using server=" + Core.getHost() + ", port=" + Core.getPort());

		try {
			UIManager.setLookAndFeel(new MetalLookAndFeel());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		Client app = new Client();
		Core core = new Core(app);
		app.setTitle("CreepTD - Online Multiplayer TowerDefense");
		app.getContentPane().setPreferredSize(Core.SCREENSIZE);
		app.setIconImage(core.getIconImage());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		app.setLocation((screenSize.width - Core.WIDTH) / 2, (screenSize.height - Core.HEIGHT) / 2);

		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setResizable(false);
		app.setBackground(Color.BLACK);
		app.setLayout(new BorderLayout());
		app.add(core);
		app.pack();
		core.init();
		core.setVisible(true);
		app.setVisible(true);
	}
}
