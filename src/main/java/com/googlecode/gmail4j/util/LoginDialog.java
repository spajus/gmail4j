package com.googlecode.gmail4j.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.CountDownLatch;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.googlecode.gmail4j.auth.Credentials;

public class LoginDialog {

    public static Credentials show(final String prompt) {
        final JFrame dialog = new JFrame(prompt);
        final CountDownLatch latch = new CountDownLatch(1);
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JTextField user = new JTextField();
        final JPasswordField pass = new JPasswordField();
        JButton ok = new JButton("OK");
        final Credentials login = new Credentials();
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                login.setUsername(user.getText());
                login.setPassword(pass.getPassword());
                latch.countDown();
            }
        });
        BoxLayout l = new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS);
        dialog.getContentPane().setLayout(l);
        dialog.getContentPane().add(user);
        dialog.getContentPane().add(pass);
        dialog.getContentPane().add(ok);
        dialog.setLocation(300, 150);
        dialog.pack();
        dialog.setVisible(true);
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (final InterruptedException e1) {
            e1.printStackTrace();
        }
        return login;
    }

}
