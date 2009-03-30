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
package com.googlecode.gmail4j.test.imap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.EmailAddress;
import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.ProxyAware;
import com.googlecode.gmail4j.javamail.ImapGmailClient;
import com.googlecode.gmail4j.javamail.ImapGmailConnection;
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
        try {
            final ImapGmailClient client = new ImapGmailClient();
            final ImapGmailConnection connection = new ImapGmailConnection();
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
            connection.disconnect();
        } catch (final Exception e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
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
    
}
