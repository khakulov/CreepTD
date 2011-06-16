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
package com.creeptd.client;

import com.creeptd.common.IConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Daniel
 */
public class Client extends JFrame {

    public static void main(String[] args) {
        if (args.length > 2) {
            System.err.println("Wrong command line arguments");
            System.out.println("Using default configuration...");
            Core.host = IConstants.DEFAULT_SERVER_HOST;
            Core.port = IConstants.DEFAULT_SERVER_PORT;
        } else if (args.length == 2) {
            Core.host = args[0];
            Core.port = Integer.parseInt(args[1]);
        } else if (args.length == 1) {
            Core.host = args[0];
            Core.port = IConstants.DEFAULT_SERVER_PORT;
        } else if (args.length == 0) {
            Core.host = IConstants.DEFAULT_SERVER_HOST;
            Core.port = IConstants.DEFAULT_SERVER_PORT;
        }
        System.out.println("Using server=" + Core.host + ", port=" + Core.port);
        Client app = new Client();
        Core core = new Core(app);
        app.setTitle("CreepTD - Online Multiplayer TowerDefense");
        app.getContentPane().setPreferredSize(Core.SCREENSIZE);
        app.setIconImage(core.getIconImage());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        core.setLocation((screenSize.width - Core.WIDTH) / 2, (screenSize.height - Core.HEIGHT) / 2);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setResizable(false);
        app.setBackground(Color.BLACK);
        app.setLayout(new BorderLayout());
        app.add(core);
        app.pack();
        core.init();
        core.setVisible(true);
        app.setVisible(true);
        app.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                Core.network.shutdown();
            }
        });
    }
}
