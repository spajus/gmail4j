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
package com.googlecode.gmail4j.test.imap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailAttachment;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.ProxyAware;
import com.googlecode.gmail4j.javamail.ImapGmailClient;
import com.googlecode.gmail4j.javamail.ImapGmailConnection;
import com.googlecode.gmail4j.javamail.ImapGmailLabel;
import com.googlecode.gmail4j.javamail.JavaMailGmailMessage;
import com.googlecode.gmail4j.test.TestConfigurer;
import com.googlecode.gmail4j.util.Constants;

/**
 * {@link ImapGmailClient} tests
 * 
 * @see ImapGmailClient
 * @see JavaMailGmailMessage
 * @see Credentials
 * @see TestConfigurer
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
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
	private static final String TEST_MAIL_WITH_ATTACHEMENTS_SUBJECT = "Test mail with attachements";

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
        client.disconnect();
    }

    /**
     * Tests marking of a message as unread
     */
    @Test
    public void testMarkAsUnread() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to mark message as unread.");
            client.markAsUnread(1);
            log.debug("Finished marking message as unread.");
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of unread messages
     */
    @Test
    public void testGetUnreadMessages() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
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
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of messages by date
     */
    @Test
    public void testGetMessagesByDateGreaterThan() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -1);
            date = cal.getTime();
            log.debug("Getting messages newer than yesterday" + date.toString());
            client.setConnection(connection);
            final List<GmailMessage> messages = client.getMessagesBy(
                    GmailClient.EmailSearchStrategy.DATE_GT,
                    date.toString());
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of messages by subject
     */
    @Test
    public void testGetMessagesBySubject() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting messages by subject");
            client.setConnection(connection);
            final List<GmailMessage> messages = client.getMessagesBy(
                    GmailClient.EmailSearchStrategy.SUBJECT,
                    "Test mail subject. Unicode: ąžuolėlį");
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of messages by keyword
     */
    @Test
    public void testGetMessagesByKeyword() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting messages by keyword");
            client.setConnection(connection);
            final List<GmailMessage> messages = client.getMessagesBy(
                    GmailClient.EmailSearchStrategy.KEYWORD,"Unicode");
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of messages by keyword as well as chained filtering
     * with the GmailMessageList class
     */
    @Test
    public void testGetMessagesByMultipleKeywords() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting unread messages");
            client.setConnection(connection);
            final List<GmailMessage> messages = client.getMessagesBy(
                    GmailClient.EmailSearchStrategy.KEYWORD,"Unicode")
                    .filterMessagesBy(GmailClient.EmailSearchStrategy.KEYWORD,"ąžuolėlį");
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests retrieval of unread priority messages
     */
    @Test
    public void testGetPriorityMessagesUnreadOnly() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting unread priority messages");
            client.setConnection(connection);
            final List<GmailMessage> unreadMessages = client.getPriorityMessages(true);
            for (GmailMessage message : unreadMessages) {
                log.debug(message);
            }
            assertNotNull("Unread Priority Messages are not null", unreadMessages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
    /**
     * Tests retrieval of read priority messages
     */
    @Test
    public void testGetPriorityMessagesReadOnly() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            log.debug("Getting read priority messages");
            client.setConnection(connection);
            final List<GmailMessage> readMessages = client.getPriorityMessages(false);
            for (GmailMessage message : readMessages) {
                log.debug(message);
            }
            assertNotNull("Read Priority Messages are not null", readMessages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests flagging a message as starred
     */
    @Test
    public void testAddStar() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
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
                log.debug("Starting to flag message as starred.");
                GmailMessage gmailMessage = messages.get(0);
                log.debug("Msg Subject: " + gmailMessage.getSubject() + " has "
                        + "been flagged as starred.");
                client.addStar(gmailMessage.getMessageNumber());
                log.debug("Finished flagging message as starred.");
            }
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests removing star flag from a star flagged message
     */
    @Test
    public void testRemoveStar() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to remove star flag from starred message.");
            client.removeStar(1);
            log.debug("Finished removing star flag from starred message.");

        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests moving of message(s) to [Gmail]/Trash
     */
    @Test
    public void testMoveToTrash() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
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
            client.disconnect();
        }
    }

    /**
     * Tests marking all the messages as read
     */
    @Test
    public void testMarkAllAsRead() {
        testSendMessage(); 
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            client.markAsUnread(1);
            log.debug("Starting to mark all the message's as read.");
            client.markAllAsRead();
            log.debug("Finished marking all the message's as read.");
            List<GmailMessage> unreadMessages = client.getUnreadMessages();
            assertTrue("All messages are marked as read.", unreadMessages.isEmpty());

        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests marking of a message as read
     */
    @Test
    public void testMarkAsRead() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            client.markAsUnread(1);
            final List<GmailMessage> messages = client.getUnreadMessages();
            if (messages.size() > 0) {
                log.debug("Starting to mark message as read.");
                GmailMessage gmailMessage = messages.get(0);
                log.debug("Msg Subject: " + gmailMessage.getSubject() + " has "
                        + "been marked as read.");
                client.markAsRead(gmailMessage.getMessageNumber());
                log.debug("Finished marking message as read.");
            }
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests moving a message to the same source folder which will 
     * throw {@link GmailException}.
     */
    @Test(expected = GmailException.class)
    public void testMoveToGmailException() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to move message #1 to same " + 
                    ImapGmailLabel.SENT_MAIL.getName());
            client.moveTo(ImapGmailLabel.SENT_MAIL, 1);
            log.debug("Test Passes with expected exception");
        } finally {
            client.disconnect();
        }
    }

    /**
     * Tests moving a message to a given destination folder.
     */
    @Test
    public void testMoveTo() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            log.debug("Starting to move message #1 from " + 
                    ImapGmailLabel.SENT_MAIL.getName());
            client.moveTo(ImapGmailLabel.INBOX, 1);
            log.debug("Finished moving message #1 to " + 
                    ImapGmailLabel.INBOX.getName());
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
    
    @Test
    public void testGetContentText() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            
            // Send a message so we have one
            GmailMessage msg = new JavaMailGmailMessage();
            msg.setSubject("Test mail subject. Unicode: ąžuolėlį");
            msg.setContentText("Test mail content. Unicode: ąžuolėlį");
            msg.addTo(new EmailAddress(conf.getTestRecipient()));
            client.send(msg);
            
            log.debug("Getting messages");
            final List<GmailMessage> messages = client.getUnreadMessages();
            log.debug("Got " + messages.size() + " messages");
            for (GmailMessage message : messages) {
                String text = message.getContentText();
                log.debug("Got text: " + text);
                assertTrue("Text is not empty", text.length() > 0);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
    
    @Test
    public void testGetPreview() {
        final ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            
            // Send a message so we have one
            GmailMessage msg = new JavaMailGmailMessage();
            msg.setSubject("Test mail subject. Unicode: ąžuolėlį");
            msg.setContentText("Let's try to write a very long message and see" +
                    "if it will get trimmed when preview is called. It has to " +
                    "be over ? chars long so it will get trimmed. Now this text " +
                    "is over ? chars long, it should really get trimmed good.");
            msg.addTo(new EmailAddress(conf.getTestRecipient()));
            client.send(msg);
            
            log.debug("Getting messages");
            final List<GmailMessage> messages = client.getUnreadMessages();
            log.debug("Got " + messages.size() + " messages");
            for (GmailMessage message : messages) {
                String text = message.getPreview();
                log.debug("Got text: " + text);
                assertTrue("Text is not empty", text.length() > 0);
                assertTrue("Text is not too long", 
                        text.length() <= Constants.PREVIEW_LENGTH);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
    
    @Test
    public void testGetAttachements() {
        final ImapGmailClient client = new ImapGmailClient();
        final ImapGmailConnection connection = new ImapGmailConnection();

        try {
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                connection.setProxy(conf.getProxyHost(), conf.getProxyPort());
                connection.setProxyCredentials(conf.getProxyCredentials());
            }
            client.setConnection(connection);
            
            // Send a message so we have one
            GmailMessage msg = new JavaMailGmailMessage();
            msg.setSubject(TEST_MAIL_WITH_ATTACHEMENTS_SUBJECT);
            msg.setContentText("This email have two attachements. One png and one zip.");
            msg.addTo(new EmailAddress(conf.getTestRecipient()));
            
            URL url = this.getClass().getClassLoader().getResource("attachement1.png");
            msg.addAttachement(new File(url.toURI()));
            url = this.getClass().getClassLoader().getResource("attachement2.zip");
            msg.addAttachement(new File(url.toURI()));
            client.send(msg);
            
            log.debug("Getting messages");
            final List<GmailMessage> messages = client.getUnreadMessages();
            log.debug("Got " + messages.size() + " messages");
            for (GmailMessage message : messages) {
            	if(message.getSubject().equals(TEST_MAIL_WITH_ATTACHEMENTS_SUBJECT)) {
            		/* Try to get attachements. */
            		List<GmailAttachment> attachements = message.getAttachements();
            		assertTrue(attachements.size() > 0);
            		for (GmailAttachment gmailAttachment : attachements) {
						log.debug(gmailAttachment.toString());
						byte[] data1 = IOUtils.toByteArray(gmailAttachment.getData());
						assertTrue(data1.length > 0);
						byte[] data2 = IOUtils.toByteArray(message.getAttachment(gmailAttachment.getPartIndex()).getData());
						assertTrue(data2.length > 0);
					}
            		break;
            	}
            }
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        } finally {
            client.disconnect();
        }
    }
}
