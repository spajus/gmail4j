/*
 * Copyright (c) 2008-2009 Tomas Varaneckas
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
package com.googlecode.gmail4j.test.imap;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.ProxyAware;
import com.googlecode.gmail4j.javamail.ImapGmailClient;
import com.googlecode.gmail4j.javamail.ImapGmailConnection;
import com.googlecode.gmail4j.javamail.ImapGmailLabel;
import com.googlecode.gmail4j.javamail.JavaMailGmailMessage;
import com.googlecode.gmail4j.test.TestConfigurer;

/**
 * {@link ImapGmailClient} tests
 * 
 * @see ImapGmailClient
 * @see JavaMailGmailMessage
 * @see Credentials
 * @see TestConfigurer
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 */
public class ImapGmailClientTest {

    /**
     * Test configuration
     */
    private TestConfigurer conf = TestConfigurer.getInstance();
    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(ImapGmailClientTest.class);

    /**
     * Tests retrieval of unread messages
     */
    @Test
    public void testGetUnreadMessages() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting unread messages");
            client.setConnection(connection);
            final List<GmailMessage> messages = client.getUnreadMessages();
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }

    /**
     * Test a sending of a simple message
     */
    @Test
    public void testSendMessage() {
        final GmailClient client = new ImapGmailClient();
        final GmailConnection connection = new ImapGmailConnection();
        connection.setLoginCredentials(conf.getGmailCredentials());
        if (conf.useProxy() && (connection instanceof ProxyAware)) {
            ((ProxyAware) connection).setProxy(
                    conf.getProxyHost(), conf.getProxyPort());
            ((ProxyAware) connection).setProxyCredentials(
                    conf.getProxyCredentials());
        }
        client.setConnection(connection);
        GmailMessage msg = new JavaMailGmailMessage();
        msg.setSubject("Test mail subject. Unicode: ąžuolėlį");
        msg.setContentText("Test mail content. Unicode: ąžuolėlį");
        msg.addTo(new EmailAddress(conf.getTestRecipient()));
        client.send(msg);
    }
    
    /**
     * Tests marking of a message as read
     */
    @Test
    public void testMarkAsRead() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);

            final List<GmailMessage> messages = client.getUnreadMessages();
            if (messages.size() > 0) {
                log.debug("Starting to mark message as read.");
                GmailMessage gmailMessage = messages.get(1);
                log.debug("Msg Subject: " + gmailMessage.getSubject() + " has "
                        + "been marked as read.");
                client.markAsRead(gmailMessage.getMessageNumber());
                log.debug("Finished marking message as read.");
            }            
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
        
    /**
     * Tests marking of a message as unread
     */
    @Test
    public void testMarkAsUnread() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            
            final List<GmailMessage> messages = client.getUnreadMessages();
            if (messages.size() > 0) {
                log.debug("Starting to mark message as unread.");
                GmailMessage gmailMessage = messages.get(1);   
                log.debug("Msg Subject: " + gmailMessage.getSubject() + " has "
                        + "been marked as unread.");
                client.markAsUnread(gmailMessage.getMessageNumber());
                log.debug("Finished marking message as unread.");
            }            
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
    
    /**
     * Tests marking all the messages as read
     */
    @Test
    public void testMarkAllAsRead() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to mark all the message's as read.");
            client.markAllAsRead();
            log.debug("Finished marking all the message's as read.");
            List<GmailMessage> unreadMessages = client.getUnreadMessages();
            assertTrue("All messages are marked as read.",unreadMessages.isEmpty());       
            
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
        
    /**
     * Tests moving a message to a given destination folder.
     */
    @Test
    public void testMoveTo() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.INBOX);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to move message #1 to " + ImapGmailLabel.INBOX.getName());
            client.moveTo(ImapGmailLabel.SPAM, 1);
            log.debug("Finished moving message #1 to " + ImapGmailLabel.SPAM.getName());            
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
    
     /**
     * Tests moving a message to the same source folder which will 
     * throw {@link GmailException}.
     */
    @Test(expected = GmailException.class)
    public void testMoveTo_GmailException() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.INBOX);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to move message #1 to same " + ImapGmailLabel.INBOX.getName());
            client.moveTo(ImapGmailLabel.INBOX, 1);
            log.debug("Test Passes with expected exception");
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }

    /**
     * Tests moving of message(s) to [Gmail]/Trash
     */
    @Test
    public void testMoveToTrash() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);

            final List<GmailMessage> messages = client.getUnreadMessages();
            log.debug("Starting to move message(s) to trash folder.");
            // moving single GmailMessage to trash folder
            //GmailMessage[] jmgms = new JavaMailGmailMessage[1];
            //jmgms[0] = messages.get(0);
            //client.moveToTrash(jmgms);
            client.moveToTrash(messages.toArray(new JavaMailGmailMessage[0]));
            log.debug("Finished moving all selected message(s) to trash.");
            assertTrue("All unread messages are moved to trash.",
                    client.getUnreadMessages().isEmpty());
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
    }
}
