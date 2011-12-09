package com.creeptd.client.panel.common;

import static com.creeptd.client.i18n.Translator._;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.creeptd.client.Core;
import com.creeptd.common.messages.client.ChatMessage;

public class ChatDialog extends JDialog {
	private static final long serialVersionUID = -4572733716375168983L;

	private JPanel panel;
    private Chat chatPane;
    private JScrollPane chatScrollPane = null;
    private JTextField message;
    private JButton send;
    
    private final String playerName;
    
    private static final List<ChatDialog> dialogs = new ArrayList<ChatDialog>();

	public ChatDialog(String playerName) {
		super(Core.getInstance().getClient(), "Chat with " + playerName, false);

		this.playerName = playerName;

        this.setSize(306, 428);
		this.setResizable(false);
		this.setLayout(null);

        UIManager.put("TextField.border", new EmptyBorder(2, 2, 2, 2));
		
        this.panel = new JPanel();
        this.panel.setBackground(Color.BLACK);
        this.panel.setBounds(0, 0, 300, 400);
		this.panel.setLayout(null);
        this.add(this.panel);
        
        this.chatScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.chatScrollPane.setBounds(5, 5, 290, 360);
        this.chatPane = new Chat(chatScrollPane);
        this.chatScrollPane.setViewportView(this.chatPane);
        this.chatScrollPane.setBorder(new LineBorder(Color.GRAY));
        this.panel.add(this.chatScrollPane);

        this.send = new Button();
        this.send.setBackground(Color.BLACK);
        this.send.setForeground(Color.WHITE);
        this.send.setBounds(215, 370, 80, 25);
        this.send.setText(_("Send"));
        this.send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                send();
            }
        });
        this.panel.add(send);

        this.message =  new JTextField();
        this.message.setBounds(5, 370, 205, 25);
        this.message.setEditable(true);
        this.message.setText("");
        this.message.requestFocus();
        this.message.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                	send();
                }
            }
        });
        this.panel.add(this.message);
	}

    public void send() {
        if (!message.getText().equals("")) {
            ChatMessage m = new ChatMessage();
            m.setMessage("/to " + this.playerName + " " + message.getText());
            Core.getInstance().getNetwork().sendMessage(m);
            message.setText("");
        }
    }
    
    public void add(String from, String message) {
    	this.chatPane.addChatText(from, message, false);
    }

	public static ChatDialog getOrCreate(String gast) {
		for (ChatDialog chatDialog : dialogs) {
			if (chatDialog.playerName.equalsIgnoreCase(gast)) {
				chatDialog.setVisible(true);
				return chatDialog;
			}
		}
		ChatDialog chatDialog = new ChatDialog(gast);
		dialogs.add(chatDialog);
		return chatDialog;
	}
}
