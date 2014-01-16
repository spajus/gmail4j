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
package com.googlecode.gmail4j;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Gmail client.
 * <p>
 * Example use:
 * <p><blockquote><pre>
 *     GmailConnection connection = //...instance of GmailConnection
 *     GmailClient client = new RssGmailClient();
 *     client.setConnection(connection);
 *     for (GmailMessage message : client.getUnreadMessages()) {
 *         System.out.println(message.getFrom() + ": " + message.getTitle());
 *     }
 * </pre></blockquote>
 * 
 * @see GmailMessage
 * @see GmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.1
 */
public abstract class GmailClient {
    /**
     * Logger
     */
    protected final Log log = LogFactory.getLog(getClass());

    /**
     * Strategies to fetch emails with
     */
    public enum EmailSearchStrategy {
        SUBJECT(), DATE(), TO(), FROM(), KEYWORD(), CC(), UNREAD();
    };

    /**
     * Gmail Connection 
     * 
     * @see #setConnection(GmailConnection)
     */
    protected GmailConnection connection;  
    
    /**
     * Argless constructor for safe extending 
     */
    public GmailClient() {
        super();
    }
    
    /**
     * Sets Gmail #connection 
     * 
     * @param connection Gmail connection
     */
    public void setConnection(final GmailConnection connection) {
        this.connection = connection;
    }
    
    /**
     * Returns list of unread {@link GmailMessage} objects
     * 
     * @return List of unread messages
     */
    public abstract List<GmailMessage> getUnreadMessages();

    /**
     * Returns list of matching {@link GmailMessage} objects
     *
     * @param strategy search strategy
     * @param value the value to look for
     */
    public abstract List<GmailMessage> getMessagesBy(EmailSearchStrategy strategy, String value);
    
    /**
     * Sends the message
     * 
     * @param message Message to be sent
     */
    public abstract void send(final GmailMessage message);
    
    /**
     * Disconnects from Gmail
     */
    public abstract void disconnect();


}