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
package com.googlecode.gmail4j.imap;

import java.util.Arrays;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

import com.googlecode.gmail4j.GmailClient;
import com.googlecode.gmail4j.GmailException;
import com.googlecode.gmail4j.GmailMessage;

/**
 * JavaMail IMAP based {@link GmailClient}
 * 
 * @see GmailClient
 * @author Tomas Varaneckas &lt;tomas.varaneckas@gmail.com&gt;
 * @version $Id$
 */
public class ImapGmailClient extends GmailClient {

    public ImapGmailClient() {
        throw new UnsupportedOperationException("IMAP client is not yet implemented");
    }
    
    @Override
    public List<GmailMessage> getUnreadMessages() {
        // TODO complete
        try {
        Store store = ((ImapGmailConnection) connection).openSession().getStore("imaps");
        store.connect("imap.gmail.com", "youraccount@gmail.com", "yourpassword");
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE); 
        List<Message> msgs = Arrays.asList(folder.getMessages());
        folder.close(false); 
        store.close();
        } catch (final Exception e) {
            throw new GmailException("..", e);
        }
        return null;
    }

}
