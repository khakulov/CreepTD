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

import com.creeptd.client.Core;
import com.creeptd.common.messages.client.ClientChatMessage;
import com.creeptd.common.messages.client.StartGameRequestMessage;
import java.util.HashMap;
import java.util.Map;

import static com.creeptd.client.i18n.Translator.*;

/**
 * Countdown when starting game. 
 *
 */
public class WaitingGameCountdownThread extends Thread {

    private Core core = null;

    /**
     * @param c core
     */
    public WaitingGameCountdownThread(Core c) {
        core = c;
    }

    /**
     *
     */
    @Override
    public void run() {
        ClientChatMessage m = new ClientChatMessage();
        m.setMessage(_("GAME STARTS IN 3..."));
        core.getNetwork().sendMessage(m);
        for (int i = 2; i > 0; i--) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Map<String,String> args = new HashMap<String,String>();
            args.put("n", i+"");
            m.setMessage(__("in %n%...", args));
            core.getNetwork().sendMessage(m);
        }
        core.getNetwork().sendMessage(new StartGameRequestMessage());

    }
}
