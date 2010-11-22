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
 * Example of marking a message as read:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     List&lt;GmailMessage&gt; messages = client.getUnreadMessages();
 *     // now get a GmailMessage item and pass it's message number
 *     client.markAsRead(message.getMessageNumber());
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
     * {@link Folder} name 
     */
    private final String name;

    /**
     * Constructor that defaults to {@code GmailClient.INBOX} 
     * as source {@link Folder} name.
     */
    public ImapGmailClient() {
        this.name = GmailClient.INBOX;
    }
    
     /**
     * Constructor with the source {@link Folder} name 
     * 
     * @param name source {@link Folder} name
     */
    public ImapGmailClient(final String name) {
        this.name = ((name == null) ? GmailClient.INBOX : name);
    }
        
    /**
     * Logger
     */
    private static final Log LOG = LogFactory.getLog(ImapGmailClient.class);

    @Override
    public List<GmailMessage> getUnreadMessages() {
        try {
            final List<GmailMessage> unread = new ArrayList<GmailMessage>();
            final Store store = openGmailStore();
            final Folder folder = getFolder(this.name,store);
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
        Folder folder = null;
        try {
            final Store store = openGmailStore();
            folder = getFolder(this.name,store);
            folder.open(Folder.READ_WRITE);

            List<Message> markedMsgList = new ArrayList<Message>();
            for (GmailMessage gmailMessage : gmailMessages) {
                // get only messages that match to the specified message number
                Message message = folder.getMessage(gmailMessage.getMessageNumber());
                // mark message as delete
                message.setFlag(Flags.Flag.DELETED, true);
                markedMsgList.add(message);
            }

            Folder trash = getFolder(GmailClient.TRASH,store);
            if(folder.getFullName().equals(trash.getFullName())){
                throw new GmailException("ImapGmailClient cannot move "
                        + "GmailMessage(s) within same folder(trash to trash)");
            }
            // move the marked messages to trash folder
            if (!markedMsgList.isEmpty()) {
                folder.copyMessages(markedMsgList.toArray(new Message[0]), trash);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed moving GmailMessage(s)"
                    + " to trash folder: " + e);
        } finally {
            closeFolder(folder);
        }
    }
    
    /**
     * Mark a given {@link GmailMessage} as read.
     *
     * @param messageNumber the message number. ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if unable to mark {@link GmailMessage} as read
     */
    public void markAsRead(int messageNumber) {
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid GmailMessage number");
        }
        Folder folder = null;
        try {
            final Store store = openGmailStore();
            folder = getFolder(this.name, store);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(messageNumber);
            if (!message.isSet(Flags.Flag.SEEN)) {
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed marking"
                    + " GmailMessage as read : " + e);
        } finally {
            closeFolder(folder);
        }
    }

     /**
     * Return the {@link Folder} object corresponding to the given name. 
     * Note that a {@link Folder} object is returned only if the named 
     * {@link Folder} physically exist on the Store.
     * 
     * @param name The name of the {@link Folder}
     * @param store instance of Gmail {@link Store}
     */
    private Folder getFolder(String name, final Store store) {
        if (store == null) {
            throw new GmailException("Gmail IMAP store cannot be null");
        }
        try {

            Folder folder = store.getFolder(name);
            if (folder.exists()) {
                return folder;
            }
        } catch (final Exception e) {
            throw new GmailException("ImapGmailClient failed getting "
                    + "Folder: " + name, e);
        }

        throw new GmailException("ImapGmailClient Folder name cannot be null");
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
