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
import java.io.IOException;
import java.text.DateFormat;
import java.util.logging.Logger;

import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import com.creeptd.client.Core;
import com.creeptd.common.Constants;
import javax.swing.JScrollPane;

public class PlayerChat extends JEditorPane {

    private static final long serialVersionUID = 1L;
    private final Logger logger = Logger.getLogger(PlayerChat.class.getName());
    private Boolean ShowDate = true;
    private JScrollPane scroller;

    /**
     * Create the chat text area.
     * @author Contex contex@schlesi.cc
     * @param dimension
     * @return the scrollpane
     */
    public PlayerChat(JScrollPane scroller) {
        this.scroller = scroller;
        this.setContentType("text/html");
        try {
            this.setText("<html><body style=\"font-family: Verdana; font-size: 11\" link='#FFFFFF' vlink='#FFFFFF' alink='#FFFFFF' text='#FFFFFF' bgcolor='#000000'></body></html>");
        } catch (Exception ex) {
            try {
                Thread.sleep(500);
            } catch (Exception ex2) {}
            this.setText("<html><body style=\"font-family: Verdana; font-size: 11\" link='#FFFFFF' vlink='#FFFFFF' alink='#FFFFFF' text='#FFFFFF' bgcolor='#000000'></body></html>");
        }

        this.setEditable(false);

        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        //this.revalidate();
    }

    public String addSmileys(String msg, int max) {
        String plain = msg;
        java.net.URL imageURL = null;

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_smile.gif");
        msg = msg.replace(":)", "<img src='" + imageURL + "'>");
        msg = msg.replace(":-)", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_happyeyes.gif");
        msg = msg.replace("=)", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_biggrin.gif");
        msg = msg.replace(":D", "<img src='" + imageURL + "'>");
        msg = msg.replace(":-D", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_wink.gif");
        msg = msg.replace(";)", "<img src='" + imageURL + "'>");
        msg = msg.replace(";-)", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_sad.gif");
        msg = msg.replace(":(", "<img src='" + imageURL + "'>");
        msg = msg.replace(":-(", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_razz.gif");
        msg = msg.replace(":P", "<img src='" + imageURL + "'>");
        msg = msg.replace(":-P", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL+"icon_cool.gif");
        msg = msg.replace("8)", "<img src='"+imageURL+"'>");
        msg = msg.replace("8-)", "<img src='"+imageURL+"'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL+"icon_neutral.gif");
        msg = msg.replace(":|", "<img src='"+imageURL+"'>");
        msg = msg.replace(":-|", "<img src='"+imageURL+"'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_surprised.gif");
        msg = msg.replace(":o", "<img src='" + imageURL + "'>");
        msg = msg.replace(":-o", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL+"icon_confused.gif");
        msg = msg.replace(":hmm", "<img src='"+imageURL+"'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_lol.gif");
        msg = msg.replace(":lol", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_blush.gif");
        msg = msg.replace(":blush", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_rolleyes.gif");
        msg = msg.replace(":rolleyes", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_eek.gif");
        msg = msg.replace(":eek", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_mad.gif");
        msg = msg.replace(":mad", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_evil.gif");
        msg = msg.replace(":evil", "<img src='" + imageURL + "'>");

        imageURL = getClass().getClassLoader().getResource(Constants.SIMLEY_URL + "icon_idea.gif");
        msg = msg.replace(":idea", "<img src='" + imageURL + "'>");

        int lastIndex = 0;
        for (int i=0; i<=max; i++) {
            int index = msg.indexOf("<img", lastIndex);
            if (index < 0) break;
            lastIndex = index+4;
            if (i == max) {
                System.out.println(i+"="+max);
                return plain;
            }
        }
        return msg;
    }

    /**
     * Send a Chat MSG.
     *
     */
    public void sendChatText(String from, String msg, boolean action, Core core) {
        String s = "";
        if (msg.length() > 200) {
            msg = msg.substring(0, 200);
        }
        if (from.equalsIgnoreCase("Server")) {
            s = "<span style=\"color:#C0C0C0\">"+msg+"</span>"; // Server may send HTML
        } else {
            String color = core.getPlayerName().equalsIgnoreCase(from) ? "FF0000" : "FFFF00";
            if (action) s += "<i>";
            s += "<span style=\"color:#"+color+"\"><b>" + from + "</b></span>";
            if (!action) s += "»";
            s += " "+addSmileys(this.escapeHTML(msg), 10);
            if (action) s += "</i>";
        }
        HTMLDocument doc = (HTMLDocument) this.getDocument();
        boolean doScroll = scroller.getVerticalScrollBar().getHeight() == 0 || scroller.getVerticalScrollBar().getValue() + scroller.getVerticalScrollBar().getHeight() + 5 >= scroller.getVerticalScrollBar().getMaximum();
        try {
            if (this.getShowDatum()) {
                DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
                s = "<span style=\"color:#C0C0C0;\">" + df.format(System.currentTimeMillis()) + "</span> "+s;
            }
            ((HTMLEditorKit) this.getEditorKit()).insertHTML(doc, doc.getLength(), "<div>"+s+"</div>", 0, 0, null);
        } catch (IOException ex) {
            this.logger.warning(ex.toString());
        } catch (BadLocationException ex) {
            this.logger.warning(ex.toString());
        }
        if (doScroll) {
            this.setCaretPosition(doc.getLength());
        }

    }

    /**
     * @see http://www.rgagnon.com/javadetails/java-0306.html
     */
    public final String escapeHTML(String s) {

        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * Set the Date on/off in Chat MSG
     *
     * @return the scrollpane
     */
    public void setShowDatum(Boolean b) {
        this.ShowDate = b;

    }

    /**
     * Set get state
     *
     * @return the scrollpane
     */
    public Boolean getShowDatum() {
        return this.ShowDate;

    }
}
