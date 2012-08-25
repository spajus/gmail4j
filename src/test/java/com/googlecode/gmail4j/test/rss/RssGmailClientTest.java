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
package com.googlecode.gmail4j.test.rss;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.googlecode.gmail4j.GmailConnection;
import com.googlecode.gmail4j.GmailMessage;
import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.http.HttpGmailConnection;
import com.googlecode.gmail4j.http.ProxyAware;
import com.googlecode.gmail4j.rss.RssGmailClient;
import com.googlecode.gmail4j.test.TestConfigurer;

/**
 * {@link RssGmailClient} tests
 * 
 * @see RssGmailClient
 * @see Credentials
 * @see TestConfigurer
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 */
public class RssGmailClientTest {

    /**
     * Logger
     */
    private static final Log log = LogFactory.getLog(RssGmailClientTest.class);

    /**
     * Tests retrieval of unread messages
     */
    @Test
    public void testGetUnreadMessages() {
        try {
            TestConfigurer conf = TestConfigurer.getInstance();            
            final RssGmailClient client = new RssGmailClient();
            final GmailConnection connection = new HttpGmailConnection();
            connection.setLoginCredentials(conf.getGmailCredentials());
            if (conf.useProxy()) {
                ((ProxyAware) connection).setProxy(conf.getProxyHost(), 
                        conf.getProxyPort());
                ((ProxyAware) connection).setProxyCredentials(
                        conf.getProxyCredentials());
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
        }
    }
    
}
