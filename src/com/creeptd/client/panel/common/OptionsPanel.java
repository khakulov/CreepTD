package com.creeptd.client.panel.common;

import com.creeptd.client.panel.LobbyScreen;
import com.creeptd.client.Core;
import com.creeptd.client.util.Fonts;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import javax.swing.border.LineBorder;
import static com.creeptd.client.i18n.Translator.*;

public class OptionsPanel extends JDialog {

	private static final long serialVersionUID = 8254776935364241202L;

	private JLabel title;
    private JCheckBox soundsCheckBox;
    private JButton closeButton;

    public OptionsPanel() {
        this.setLayout(null);
        this.setBounds(Core.getInstance().getCreatorX()+250, Core.getInstance().getCreatorY()+200, 400, 200);
        this.getContentPane().setForeground(Color.GREEN);
        this.getContentPane().setBackground(Color.BLACK);
        this.setTitle("CreepTD - "+_("Options"));
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        title = Fonts.getFrameTitle(_("Options"), 0, this);

        soundsCheckBox = new JCheckBox();
        soundsCheckBox.setText(_("Play sounds in game"));
        soundsCheckBox.setSelected(!Core.getInstance().getSoundManagement().isMuted());
        soundsCheckBox.setBounds(100, 70, 190, 30);
        soundsCheckBox.setBackground(Color.BLACK);
        soundsCheckBox.setForeground(Color.GREEN);
        soundsCheckBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (soundsCheckBox.isSelected()) {
                    Core.getInstance().getSoundManagement().setMuted(false);
                } else {
                    Core.getInstance().getSoundManagement().setMuted(true);
                }
            }
        });

        closeButton = new Button();
        closeButton.setBounds(150, 125, 110, 30);
        closeButton.setBackground(Color.BLACK);
        closeButton.setForeground(Color.GREEN);
        closeButton.setBorder(new LineBorder(Color.GRAY, 1));
        closeButton.setText(_("Close"));
        closeButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        this.add(title);
        this.add(soundsCheckBox);
        this.add(closeButton);
        this.validate();
    }

    @Override
    public void dispose() {
        super.dispose();
        LobbyScreen.onCloseOptionsPanel();
    }
}
