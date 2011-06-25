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

import com.creeptd.common.Constants;
import java.awt.BorderLayout;
import javax.swing.JApplet;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author Daniel
 */
public class ClientApplet extends JApplet {

    private Core core = null;

    @Override
    public void init() {
        String host = this.getParameter("host");
        if (host != null) {
            Core.host = host;
        } else {
            Core.host = Constants.DEFAULT_HOSTNAME;
        }
        String port = this.getParameter("port");
        if (port != null) {
            Core.port = Integer.parseInt(port);
        } else {
            Core.port = Constants.DEFAULT_SERVER_PORT;
        }

        this.core = new Core(this);
        this.core.setBounds(0, 0, Core.WIDTH, Core.HEIGHT);
        this.setBounds(0, 0, Core.WIDTH, Core.HEIGHT);
        this.core.init();
        this.setLayout(new BorderLayout());
        this.add(this.core);
        this.getContentPane().setPreferredSize(Core.SCREENSIZE);
    }

    @Override
    public void start() {
        new ParserDelegator(); // @see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6993691
        
        this.core.setVisible(true);
        this.setVisible(true);
    }

    @Override
    public void stop() {
        Core.network.shutdown();
    }

    @Override
    public void destroy() {
    }
}
