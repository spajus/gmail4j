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
import com.googlecode.gmail4j.util.CommonConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
 * Example of moving message(s) to the trash folder:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     List&lt;GmailMessage&gt; messages = client.getUnreadMessages();
 *     client.moveToTrash(messages.toArray(new JavaMailGmailMessage[0]));
 * </pre></blockquote></p>
 * 
 * @see GmailClient
 * @see ImapGmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 * @since 0.3
 */
public class ImapGmailClient extends GmailClient {

     /**
     * Logger
     */
    private static final Log LOG = LogFactory.getLog(ImapGmailClient.class);

    @Override
    public List<GmailMessage> getUnreadMessages() {
        try {
            final List<GmailMessage> unread = new ArrayList<GmailMessage>();
            final Store store = openGmailStore();
            final Folder folder = store.getFolder(CommonConstants.GMAIL_INBOX);
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
            Transport transport = null;
            try {
                final JavaMailGmailMessage msg = (JavaMailGmailMessage) message;
                transport = getGmailTransport();
                transport.sendMessage(
                        msg.getMessage(),
                        msg.getMessage().getAllRecipients());
            } catch (final Exception e) {
                throw new GmailException("Failed sending message: " + message, e);
            }
            finally{
                if(transport.isConnected())
                {
                    try {
                        transport.close();
                    } catch (final Exception e) {
                        LOG.warn("Cannot Close ImapGmailConnection : " + transport, e);
                    }
                }
            }
        } else {
            throw new GmailException("ImapGmailClient requires JavaMailGmailMessage!");
        }
    }

    /**
     * Moves the given {@link GmailMessage}'s to Trash the folder.
     *
     * @param gmailMessages {@link GmailMessage} message(s)
     * @throws GmailException if unable to move {@link GmailMessage}'s to
     * the Trash Folder
     */
    public void moveToTrash(final GmailMessage[] gmailMessages) {
        if (gmailMessages == null || gmailMessages.length <= 0) {
            LOG.warn("ImapGmailClient requires GmailMessage(s) to move"
                    + " to move messages to trash folder");
            return;
        }
        Folder inbox = null;
        try {
            final Store store = openGmailStore();
            inbox = store.getFolder(CommonConstants.GMAIL_INBOX);
            inbox.open(Folder.READ_WRITE);

            List<Message> markedMsgList = new ArrayList<Message>();
            for (GmailMessage gmailMessage : gmailMessages) {
                // get only messages that match to the specified message number
                Message message = inbox.getMessage(gmailMessage.getMessageNumber());
                // mark message as delete
                message.setFlag(Flags.Flag.DELETED, true);
                markedMsgList.add(message);
            }

            Folder trash = store.getFolder(CommonConstants.GMAIL_TRASH);
            // move the marked messages to trash folder
            if (!markedMsgList.isEmpty()) {
                inbox.copyMessages(markedMsgList.toArray(new Message[0]), trash);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed moving GmailMessage(s)"
                    + " to trash folder: " + e);
        } finally {
            closeFolder(inbox);
        }
    }

    /**
     * Close any {@link Folder} that contain {@link Message} and are in open state.
     *
     * @param folder {@link Folder} to be closed
     */
    public void closeFolder(Folder folder) {
        if (folder != null) {
            try {
                if (folder.isOpen()) {
                    folder.close(true);
                } else {
                    LOG.info("{0} folder is already open" + folder.getName());
                }
            } catch (Exception e) {
                LOG.warn("Cannot close folder : " + folder.getName(), e);
            }
        }
    }
}
