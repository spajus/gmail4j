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
package com.googlecode.gmail4j.javamail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.search.FlagTerm;
import javax.mail.search.SubjectTerm;
import javax.mail.search.RecipientStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.BodyTerm;

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
 * Example of getting priority messages:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     //set the {@code boolean} unreadOnly to {@code true} to retrive unread 
 *     //priority messages only, {@code false} to read priority messages only
 *     List&lt;GmailMessage&gt; priorityMessages = client.getPriorityMessages(true);
 * </pre></blockquote></p>
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
 * Example of marking all messages as read:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     client.markAllAsRead();
 * </pre></blockquote></p>
 * Example of marking a message as unread:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     // now get a read GmailMessage item and pass it's message number
 *     client.markAsUnread(message.getMessageNumber());
 * </pre></blockquote></p>
 * Example of flagging a message as starred:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     // now get a GmailMessage item and pass it's message number
 *     client.addStar(message.getMessageNumber());
 * </pre></blockquote></p>
 * Example of removing a message flagged as starred:
 * <p><blockquote><pre>
 *     GmailConnection conn = new ImapGmailConnection();
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     // now get a GmailMessage item and pass it's message number
 *     client.removeStar(message.getMessageNumber());
 * </pre></blockquote></p>
 * Example of message move to destination folder:
 * <p><blockquote><pre>
 *     // Constructor with the source folder name 
 *     ImapGmailClient client = new ImapGmailClient(ImapGmailLabel.SENT_MAIL);
 *     //configure connection
 *     GmailClient client = new ImapGmailClient();
 *     client.setConnection(conn);
 *     // name the destination folder and the message # to be moved
 *     client.moveTo(ImapGmailLabel.SPAM, 1);
 * </pre></blockquote></p>
 * 
 * @see GmailClient
 * @see ImapGmailConnection
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @since 0.3
 */
public class ImapGmailClient extends GmailClient {

    /**
     * Source {@link Folder} label name 
     */
    private final String srcFolder;

    /**
     * Constructor that defaults to {@code ImapGmailLabel.INBOX.getName()} 
     * as source {@link Folder} name.
     */
    public ImapGmailClient() {
        this.srcFolder = ImapGmailLabel.INBOX.getName();
    }
    
     /**
     * Constructor with the source {@link Folder} name 
     * 
     * @param label source {@link Folder} name.See {@see ImapGmailLabel}
     * @see ImapGmailLabel
     * @since 0.4
     */
    public ImapGmailClient(final ImapGmailLabel label) {
        this.srcFolder = ((label == null) 
                ? ImapGmailLabel.INBOX.getName() : label.getName());
    }
        
    /**
     * Logger
     */
    private static final Log LOG = LogFactory.getLog(ImapGmailClient.class);

    @Override
    public List<GmailMessage> getUnreadMessages() {
        return getMessagesBy(EmailSearchStrategy.UNREAD,"");
    }

    @Override
    public List<GmailMessage> getMessagesBy(EmailSearchStrategy strategy, String value)
    {
        SearchTerm seekStrategy = null;
        switch(strategy)
        {
            case SUBJECT:
                seekStrategy =  new SubjectTerm(value);
                LOG.debug("Fetching emails with a subject of \"" + value + "\"");
                break;
            case TO:
                seekStrategy = new RecipientStringTerm(Message.RecipientType.TO,value);
                LOG.debug("Fetching emails sent to \"" + value + "\"");
                break;
            case FROM:
                seekStrategy = new FromStringTerm(value);
                LOG.debug("Fetching emails sent from \"" + value + "\"");
                break;
            case CC:
                seekStrategy = new RecipientStringTerm(Message.RecipientType.CC,value);
                LOG.debug("Fetching emails CC'd to \"" + value + "\"");
                break;
            case DATE_GT:
                seekStrategy = new SentDateTerm(SentDateTerm.GT,
                        new Date(Date.parse(value)));
                LOG.debug(
                        "Fetching emails with a send date newer than \"" + value + "\"");
                break;
            case DATE_LT:
                seekStrategy = new SentDateTerm(SentDateTerm.LT,
                        new Date(Date.parse(value)));
                LOG.debug(
                        "Fetching emails with a send date newer than \"" + value + "\"");
                break;
            case DATE_EQ:
                seekStrategy = new SentDateTerm(SentDateTerm.EQ,
                        new Date(Date.parse(value)));
                LOG.debug(
                        "Fetching emails with a send date newer than \"" + value + "\"");
                break;
            case KEYWORD:
                seekStrategy = new BodyTerm(value);
                LOG.debug("Fetching emails containing the keyword \"" + value + "\"");
                break;
            case UNREAD:
                seekStrategy = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                LOG.debug("Fetching all unread emails");
                break;
        }
        try {
            final List<GmailMessage> found = new ArrayList<GmailMessage>();
            final Store store = openGmailStore();
            final Folder folder = getFolder(this.srcFolder,store);
            folder.open(Folder.READ_ONLY);
            int counter = 1;
            for (final Message msg : folder.search(seekStrategy)) {
                found.add(new JavaMailGmailMessage(msg));
                counter++;
            }
            LOG.debug("Found " + found.size() + " emails");
            return found;
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
     * Moves given {@link GmailMessage}'s to {@link ImapGmailLabel.TRASH} folder.
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
            folder = getFolder(this.srcFolder,store);
            if(!folder.isOpen())
            {
                folder.open(Folder.READ_WRITE);                
            }
            
            List<Message> markedMsgList = new ArrayList<Message>();
            for (GmailMessage gmailMessage : gmailMessages) {
                // get only messages that match to the specified message number
                Message message = folder.getMessage(gmailMessage.getMessageNumber());
                message.setFlag(Flags.Flag.SEEN, true);
                // mark message as delete
                message.setFlag(Flags.Flag.DELETED, true);
                markedMsgList.add(message);
            }

            Folder trash = getFolder(ImapGmailLabel.TRASH.getName(),store);
            if(folder.getURLName().equals(trash.getURLName())){
                LOG.warn("ImapGmailClient trying to move GmailMessage(s) within"
                        + " same folder(ImapGmailLabel.TRASH.getName())");
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
     * @param messageNumber the message number ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if unable to mark {@link GmailMessage} as read
     */
    public void markAsRead(int messageNumber) {
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid "
                    + "GmailMessage number");
        }
        Folder folder = null;
        
        try {
            final Store store = openGmailStore();
            folder = getFolder(this.srcFolder, store);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(messageNumber);
            if (!message.isSet(Flags.Flag.SEEN)) {
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed marking"
                    + " GmailMessage as read : " + messageNumber, e);
        } finally {
            closeFolder(folder);
        }
    }
    
    /**
     * Mark all {@link GmailMessage}(s) as read in a folder.
     *
     * @throws GmailException if unable to mark all {@link GmailMessage} as read
     */
    public void markAllAsRead() {
        Folder folder = null;

        try {
            final Store store = openGmailStore();
            folder = getFolder(this.srcFolder, store);
            folder.open(Folder.READ_WRITE);
            for (final Message message : folder.search(new FlagTerm(
                    new Flags(Flags.Flag.SEEN), false))) {
                message.setFlag(Flags.Flag.SEEN, true);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed marking"
                    + " all GmailMessage as read" , e);
        } finally {
            closeFolder(folder);
        }
    }
    
    /**
     * Mark a given {@link GmailMessage} as unread.
     *
     * @param messageNumber the message number ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if unable to mark {@link GmailMessage} as unread
     */
    public void markAsUnread(int messageNumber) {
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid "
                    + "GmailMessage number");
        }
        Folder folder = null;

        try {
            final Store store = openGmailStore();
            folder = getFolder(this.srcFolder, store);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(messageNumber);
            if (message.isSet(Flags.Flag.SEEN)) {
                message.setFlag(Flags.Flag.SEEN, false);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed marking"
                    + " GmailMessage as unread : " + messageNumber , e);
        } finally {
            closeFolder(folder);
        }
    }
    
    /**
     * Flag a given {@link GmailMessage} as Starred.
     *
     * @param messageNumber the message number ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if unable to flag {@link GmailMessage} as starred
     */
    public void addStar(int messageNumber){
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid "
                    + "GmailMessage number");
        }
        Folder folder = null;

        try {
            final Store store = openGmailStore();
            folder = getFolder(this.srcFolder, store);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(messageNumber);
            if (!message.isSet(Flags.Flag.FLAGGED)) {
                message.setFlag(Flags.Flag.FLAGGED, true);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed flagging"
                    + " GmailMessage as starred : " + messageNumber ,e);
        } finally {
            closeFolder(folder);
        }        
    }
    
    /**
     * Removes Star Flag of a given Starred {@link GmailMessage}.
     *
     * @param messageNumber the message number ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if unable to remove star flag from {@link GmailMessage}
     */
    public void removeStar(int messageNumber){
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid "
                    + "GmailMessage number");
        }
        Folder folder = null;

        try {
            final Store store = openGmailStore();
            folder = getFolder(ImapGmailLabel.STARRED.getName(), store);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(messageNumber);
            if (message.isSet(Flags.Flag.FLAGGED)) {
                message.setFlag(Flags.Flag.FLAGGED, false);
            }
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed removing"
                    + " GmailMessage star flag : " + messageNumber , e);
        } finally {
            closeFolder(folder);
        }                
    }
        
    /**
     * Move {@link GmailMessage} to a given destination folder.
     *
     * @param destFolder the destination {@link Folder} name.See {@see ImapGmailLabel}
     * @param messageNumber the message number ex:{@code gmailMessage.getMessageNumber()}
     * @throws GmailException if it fails to move {@link GmailMessage} to the
     * destination folder
     */
    public void moveTo(ImapGmailLabel destFolder,int messageNumber){
        if (messageNumber <= 0) {
            throw new GmailException("ImapGmailClient invalid GmailMessage number");
        }

        Folder fromFolder = null;
        Folder toFolder = null;

        try {
            final Store store = openGmailStore();
            fromFolder = getFolder(this.srcFolder, store);
            fromFolder.open(Folder.READ_WRITE);
            Message message = fromFolder.getMessage(messageNumber);

            if (message != null) {
                toFolder = getFolder(destFolder.getName(), store);

                if (fromFolder.getURLName().equals(toFolder.getURLName())) {
                    throw new GmailException("ImapGmailClient cannot move "
                            + "GmailMessage within same folder "
                            + "(from " + fromFolder.getFullName() + " to "
                            + toFolder.getFullName() + ")");
                }
                // copy from source folder to destination folder
                fromFolder.copyMessages(new Message[]{message}, toFolder);
                // move the copied message to trash folder
                moveToTrash(new GmailMessage[]{new JavaMailGmailMessage(message)});
            }
        } catch (GmailException ge) {
            throw ge;
        } catch (Exception e) {
            throw new GmailException("ImapGmailClient failed moving"
                    + " GmailMessage from " + fromFolder.getFullName(), e);
        } finally {
            closeFolder(fromFolder);
        }
    }
    
    /**
     * Returns list of unread/read priority {@link GmailMessage} objects 
     * based on the {@code unreadOnly} value
     * 
     * @param unreadOnly {@code true} to unread priority {@link GmailMessage} 
     * objects only, {@code false} to read priority {@link GmailMessage} 
     * objects only
     * @return List of unread/read priority messages
     * @throws GmailException if unable to get unread/read priority messages
     */
    public List<GmailMessage> getPriorityMessages(boolean unreadOnly){
        try {
            final List<GmailMessage> priorityMessages = new ArrayList<GmailMessage>();
            final Store store = openGmailStore();
            Folder folder = getFolder(ImapGmailLabel.IMPORTANT.getName(),store);
            folder.open(Folder.READ_ONLY); 
            for (final Message msg : folder.search(new FlagTerm(
                    new Flags(Flags.Flag.SEEN), !unreadOnly))) {
                priorityMessages.add(new JavaMailGmailMessage(msg));
            }
            
            return priorityMessages;
        } catch (final Exception e) {
            throw new GmailException("Failed getting priority messages", e);
        }
    }

     /**
     * Return the {@link Folder} object corresponding to the given 
     * {@link ImapGmailLabel} name. 
     * Note that a {@link Folder} object is returned only if the named 
     * {@link Folder} physically exist on the Store.
     * 
     * @param name the name of the folder
     * @param store instance of Gmail {@link Store}
     */
    private Folder getFolder(String name, final Store store) {
        if (store == null) {
            throw new GmailException("Gmail IMAP store cannot be null");
        }
        try {
            name = ((name == null) ? this.srcFolder : name);
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
    private void closeFolder(Folder folder) {
        if (folder != null) {
            try {
                if (folder.isOpen()) {
                    folder.close(true);
                } else {
                    LOG.info(folder.getName() + " folder is already open.");
                }
            } catch (Exception e) {
                LOG.warn("Cannot close folder : " + folder.getName(), e);
            }
        }
    }
    
    @Override
    public void disconnect() {
    	if (connection != null) {
    		connection.disconnect();
    	}
    }
}