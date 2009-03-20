/*
 * Copyright (c) 2008-2009 Tomas Varaneckas
 * http://www.varaneckas.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.googlecode.gmail4j.util;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.googlecode.gmail4j.auth.Credentials;

public class LoginDialog {
    
    public static final Log log = LogFactory.getLog(LoginDialog.class);

    public static Credentials show(final String prompt) {
        final JFrame dialog = new JFrame(prompt);
        final CountDownLatch latch = new CountDownLatch(1);
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JLabel labelUser = new JLabel("Username");
        final JTextField user = new JTextField();
        final JPasswordField pass = new JPasswordField();
        final Credentials login = new Credentials();
        pass.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    returnCredentials(latch, user, pass, login);
                    dialog.setVisible(false);
                }
            } 
        });
        final JLabel labelPass = new JLabel("Password");
        JButton ok = new JButton("Done");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                returnCredentials(latch, user, pass, login);
                dialog.setVisible(false);
            }
        });
        BoxLayout l = new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS);
        dialog.getContentPane().setLayout(l);
        dialog.getContentPane().add(labelUser);
        dialog.getContentPane().add(user);
        dialog.getContentPane().add(labelPass);
        dialog.getContentPane().add(pass);
        dialog.getContentPane().add(ok);
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        
        dialog.pack();
        dialog.setSize(200, dialog.getSize().height);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        dialog.setLocation(screen.width / 2 - dialog.getSize().width / 2, 
                screen.height / 2 - dialog.getSize().height / 2);
        dialog.setVisible(true);
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                log.debug("State:" + e);
            }
            @Override
            public void windowClosing(WindowEvent e) {
                log.warn("Login dialog closed, no username/password specified");
                latch.countDown();
            }
        });
        try {
            log.info("Waiting for username/password");
            latch.await(30, TimeUnit.SECONDS);
        } catch (final InterruptedException e1) {
            log.warn("Interrupted", e1);
        }
        return login;
    }

    private static void returnCredentials(final CountDownLatch latch,
            final JTextField user, final JPasswordField pass,
            final Credentials login) {
        login.setUsername(user.getText());
        login.setPassword(pass.getPassword());
        latch.countDown();
        
    }

}
