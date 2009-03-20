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
package com.googlecode.gmail4j.rss.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.rss.RssGmailClient;
import com.googlecode.gmail4j.rss.RssGmailMessage;
import com.googlecode.gmail4j.util.LoginDialog;

/**
 * {@link RssGmailClient} tests
 * 
 * @see RssGmailClient
 * @see RssGmailMessage
 * @see Credentials
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 */
public class RssGmailClientTest {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(RssGmailClientTest.class);

    @Test
    public void testGetUnreadMessages() {
        try {
            final GmailClient client = new RssGmailClient();
            client.setLoginCredentials(LoginDialog.getInstance()
                    .show("Enter Gmail Login"));
            log.debug("Initializing RSS client");
            client.init();
            log.debug("Getting unread messages");
            final List<GmailMessage> messages = client.getUnreadMessages();
            for (GmailMessage message : messages) {
                log.debug(message);
            }
            assertNotNull("Messages are not null", messages);
        } catch (final GmailException e) {
            log.error("Test Failed", e);
            fail("Caught exception: " + e.getMessage());
        }
    }
    
}
