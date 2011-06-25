/**
Creep Smash, a multiplayer towerdefence game
created as a project at the Hochschule fuer
Technik Stuttgart (University of Applied Science)
http://www.hft-stuttgart.de

Copyright (C) 2008 by
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
package com.creeptd.server.game.modes;

import com.creeptd.server.game.Game;
import com.creeptd.server.game.PlayerInGame;
import java.util.LinkedList;
import java.util.List;

/**
 * All vs All game mode.
 * 
 * @author Daniel
 */
public class AllvsAllGameMode extends GameMode {
    
    protected AllvsAllGameMode(Game game) {
        super(game);
    }

    /**
     * Receivers are all alive players but oneself.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findReceivers(PlayerInGame sender) {
        List<PlayerInGame> receivers = new LinkedList<PlayerInGame>();
        for (PlayerInGame p : this.getGame().getPlayers()) {
            if (!p.isGameOver() && !p.equals(sender)) {
                receivers.add(p);
            }
        }
        return receivers;
    }

    /**
     * Transfer is the player, where the creep escaped, himself.
     *
     * {@inheritDoc}
     */
    @Override
    public List<PlayerInGame> findTransfers(PlayerInGame sender, PlayerInGame from) {
        List<PlayerInGame> transfers = new LinkedList<PlayerInGame>();
        if (!from.isGameOver()) {
            transfers.add(from);
        }
        return transfers;
    }

    // All other methods are default behaviour
}
