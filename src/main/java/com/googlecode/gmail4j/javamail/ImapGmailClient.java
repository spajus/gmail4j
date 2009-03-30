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
package com.googlecode.gmail4j.javamail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.search.FlagTerm;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;

/**
 * JavaMail IMAP based {@link GmailClient}
 * <p>
 * Example of getting unread messages:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     List&lt;GmailMessage&gt; unreadMessages = client.getUnreadMessages();
 * </pre></blockquote><p>
 * Example of sending a simple message:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     GmailMessage message = new JavaMailGmailMessage();
 *     message.setSubject("Hi!");
 *     message.setContentText("A message from Gmail4J");
 *     message.addTo(new EmailAddress("j.smith@example.com"));  
 *     client.send(message);   
 * </pre></blockquote></p>
 * 
 * @see GmailClient
 * @see ImapGmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class ImapGmailClient extends GmailClient {

    @Override
    public List<GmailMessage> getUnreadMessages() {
        try {
            final List<GmailMessage> unread = new ArrayList<GmailMessage>();
            final Store store = openGmailStore();
            final Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY); 
            for (final Message msg : folder.search(new FlagTerm(
                    new Flags(Flags.Flag.SEEN), false))) {
                unread.add(new JavaMailGmailMessage(msg));
            }
            return unread;
        } catch (final Exception e) {
            throw new GmailException("Failed getting unread messages", e);
        }
    }
    
    /**
     * Opens Gmail {@link Store}
     * 
     * @return instance of Gmail store
     * @throws GmailException if GmailConnection is not ImapGmailConnection
     */
    private Store openGmailStore() {
        if (connection instanceof ImapGmailConnection) {
            return ((ImapGmailConnection) connection).openGmailStore();
        } 
        throw new GmailException("ImapGmailClient requires ImapGmailConnection!");
    }
    
    /**
     * Gets Gmail {@link Transport} for sending messages
     * 
     * @return Configured Gmail Transport ready for use
     * @throws GmailException if GmailConnection is not ImapGmailConnection
     */    
    private Transport getGmailTransport() {
        if (connection instanceof ImapGmailConnection) {
            return ((ImapGmailConnection) connection).getTransport();
        }
        throw new GmailException("ImapGmailClient requires ImapGmailConnection!");
    }

    @Override
    public void send(final GmailMessage message) {
        if (message instanceof JavaMailGmailMessage) {
            try {
                final JavaMailGmailMessage msg = (JavaMailGmailMessage) message;
                getGmailTransport().sendMessage(
                        msg.getMessage(), 
                        msg.getMessage().getAllRecipients());
            } catch (final Exception e) {
                throw new GmailException("Failed sending message: " + message, e);
            }
        } else {
            throw new GmailException("ImapGmailClient requires JavaMailGmailMessage!");
        }
    }

}
