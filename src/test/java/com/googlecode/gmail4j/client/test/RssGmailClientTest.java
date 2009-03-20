package com.googlecode.gmail4j.client.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.rss.RssGmailClient;
import com.googlecode.gmail4j.util.LoginDialog;

public class RssGmailClientTest {

    private static final Log log = LogFactory.getLog(RssGmailClientTest.class);

    @Test
    public void testGetUnreadMessages() throws Exception {
        try {
            final GmailClient client = new RssGmailClient();
            client.setLoginCredentials(LoginDialog.show("Enter Gmail Login"));
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
