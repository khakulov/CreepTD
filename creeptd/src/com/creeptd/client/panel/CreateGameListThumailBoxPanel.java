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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.creeptd.common.Constants;

/**
 * The Create GameList Panel @ThumailBox
 * @see CreateGameListPanel
 * @author Contex
 *
 */
public class CreateGameListThumailBoxPanel extends JPanel implements MouseListener, MouseMotionListener {

    /**
     *
     */
    private static final long serialVersionUID = 4L;
    public int mapID;
    public Constants.Map map;
    private JLabel name;
    private CreateGameListPanel creategameListPanel;

    public CreateGameListThumailBoxPanel(CreateGameListPanel createGameListPanel, Constants.Map map) {

        this.mapID = map.ordinal();
        this.map = map;
        this.creategameListPanel = createGameListPanel;
        init();

    }

    /**
     * Initialize the Panel.
     *
     */
    private void init() {

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setSize(new Dimension(100, 110));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        name = new JLabel(this.map.toString());
        name.setSize(new Dimension(100, 10));
        name.setBackground(Color.BLACK);
        name.setFont(new Font("Arial", Font.PLAIN, 9));
        name.setForeground(Color.green);
        name.setHorizontalAlignment(JLabel.CENTER);
        name.setVerticalAlignment(JLabel.CENTER);

        ImageIcon preview = null;


        InputStream file = this.getClass().getClassLoader().getResourceAsStream(Constants.Map.getPictureThumbnailPath(this.map.toString()));


        if (file != null) {

            try {
                preview = new ImageIcon(ImageIO.read(file));

                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            preview.setImage(preview.getImage().getScaledInstance(100, 100,
                    Image.SCALE_SMOOTH));

        }


        final JLabel previewLabel = new JLabel(preview);
        previewLabel.setSize(new Dimension(100, 100));
        previewLabel.setBackground(Color.BLACK);

        this.add(previewLabel, BorderLayout.NORTH);
        this.add(name, BorderLayout.SOUTH);

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
        this.creategameListPanel.selectMap(this.mapID);
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        name.setFont(new Font("Arial", Font.BOLD, 9));
        name.setForeground(Color.red);

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        name.setFont(new Font("Arial", Font.PLAIN, 9));
        name.setForeground(Color.green);

    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
    }
}
