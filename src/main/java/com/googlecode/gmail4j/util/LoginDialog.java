/*
 * Copyright (c) 2008-2012 Tomas Varaneckas
 * http://www.varaneckas.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

/**
 * Dialog for getting {@link Credentials}.
 * <p>
 * Designed mainly for testing purposes.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     Credentials login = LoginDialog.show("Gmail Login");
 * </pre></blockquote>
 * 
 * @see Credentials
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.1
 */
public class LoginDialog {
       
    /**
     * Dialog frame
     */
    private JFrame dialog;
    
    /**
     * User text input
     */
    private JTextField user;
    
    /**
     * Password input
     */
    private JPasswordField pass;
    
    /**
     * Username label
     */
    private JLabel labelUser;
    
    /**
     * Password label
     */
    private JLabel labelPass;

    /**
     * OK button
     */
    private JButton ok;

    /**
     * Countdown latch to make this dialog application modal
     */
    private CountDownLatch latch;

    /**
     * Lazy singleton instance
     */
    private static LoginDialog instance = null;
    
    /**
     * Getter for lazy singleton instance
     * 
     * @return Instance of <code>LoginDialog</code>
     */
    public static final LoginDialog getInstance() {
        synchronized (LoginDialog.class) {
            if (instance == null) {
                instance = new LoginDialog();
            }
        }
        return instance;
    }
    
    /**
     * Logger
     */
    public static final Log log = LogFactory.getLog(LoginDialog.class);

    /**
     * Method that shows login window with prompt for username and password,
     * like this:
     * <p><pre>
     * +------ prompt ------+
     * |Username            |
     * |+------------------+|
     * ||                  ||
     * |+------------------+|
     * |Password            |
     * |+------------------+|
     * ||                  ||
     * |+------------------+|
     * |[OK]                |
     * +--------------------+
     * </pre>
     * @param prompt
     * @return Credentials with username/password from the dialog prompt
     */
    public Credentials show(final String prompt) {
        createDialog(prompt);
        final Credentials login = new Credentials();
        createListeners(login);
        showDialog();
        waitForData();
        return login;
    }

    /**
     * Creates the required listeners
     * 
     * @param login Credentials object for filling the data
     */
    private void createListeners(final Credentials login) {
        pass.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    setCredentials(login);
                    dialog.setVisible(false);
                }
            } 
        });
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                setCredentials(login);
                dialog.setVisible(false);
            }
        });
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                log.warn("Login dialog closed, no username/password specified");
                latch.countDown();
            }
        });
    }

    /**
     * Lets latch to wait for data
     */
    private void waitForData() {
        try {
            log.info("Waiting for username/password");
            latch.await(30, TimeUnit.SECONDS);
        } catch (final InterruptedException e1) {
            log.warn("Interrupted", e1);
        }
    }

    /**
     * Creates the dialog
     * 
     * @param prompt Dialog prompt
     */
    private void createDialog(final String prompt) {
        dialog = new JFrame(prompt);
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        latch = new CountDownLatch(1);
        labelUser = new JLabel("Username");
        labelPass = new JLabel("Password");
        user = new JTextField();
        pass = new JPasswordField();
        ok = new JButton("OK");
    }

    /**
     * Shows the dialog
     */
    private void showDialog() {
        final BoxLayout layout = new BoxLayout(dialog.getContentPane(), 
                BoxLayout.Y_AXIS);
        dialog.getContentPane().setLayout(layout);
        dialog.getContentPane().add(labelUser);
        dialog.getContentPane().add(user);
        dialog.getContentPane().add(labelPass);
        dialog.getContentPane().add(pass);
        dialog.getContentPane().add(ok);
        dialog.pack();
        dialog.setSize(200, dialog.getSize().height);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        center(dialog);
        dialog.setVisible(true);
        user.requestFocusInWindow();
    }

    /**
     * Centers the frame on screen
     * 
     * @param frame Target frame
     */
    private void center(final JFrame frame) {
        final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        frame.setLocation(screen.width / 2 - dialog.getSize().width / 2, 
                screen.height / 2 - dialog.getSize().height / 2);
    }

    /**
     * Inner method that releases the {@link CountDownLatch} and fills 
     * {@link Credentials} object with values from Username and Password fields
     * 
     * @param user User {@link JTextField}
     * @param pass Password {@link JPasswordField}
     * @param login Login {@link Credentials}
     */
    private void setCredentials(final Credentials login) {
        login.setUsername(user.getText());
        login.setPassword(pass.getPassword());
        latch.countDown();
    }
}
