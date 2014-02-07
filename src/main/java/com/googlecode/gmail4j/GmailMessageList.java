package com.googlecode.gmail4j;

import com.googlecode.gmail4j.javamail.JavaMailGmailMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.mail.Flags;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;


public class GmailMessageList implements List<GmailMessage>{

    /**
     * Logger
     */
    private static final Log LOG = LogFactory.getLog(GmailMessageList.class);

    private List<GmailMessage> emails;

    public GmailMessageList()
    {
        emails = new LinkedList<GmailMessage>();
    }

    public GmailMessageList(List<GmailMessage> emails)
    {
        this.emails = emails;
    }

    /**
     * Returns list of matching {@link GmailMessage} objects
     *
     * @param strategy search strategy
     * @param value the value to look for
     */
    public GmailMessageList filterMessagesBy(GmailClient.EmailSearchStrategy strategy, String value)    throws Exception
    {
        LOG.debug("Retrieving emails where " + strategy.name() + " equals " + value);
        List<GmailMessage> matchedEmails = new LinkedList<GmailMessage>();
        Date dateToLookFor = null;
        if (strategy == GmailClient.EmailSearchStrategy.DATE_EQ
                || strategy == GmailClient.EmailSearchStrategy.DATE_GT
                || strategy == GmailClient.EmailSearchStrategy.DATE_LT)
        {
            dateToLookFor = new Date(Date.parse(value));
        }
        int total = emails.size();
        int counter = 0;
        boolean wasFound;
        for (GmailMessage message : emails)
        {
            switch (strategy)
            {
                case SUBJECT:
                    if (message.getSubject().equals(value))
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case DATE_EQ:
                    if (message.getSendDate().compareTo(dateToLookFor)== 0)
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case DATE_GT:
                    if (message.getSendDate().compareTo(dateToLookFor) > 0)
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case DATE_LT:
                    if (message.getSendDate().compareTo(dateToLookFor) < 0)
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case TO:
                    wasFound = false;
                    List<EmailAddress> emailAddressesTo = message.getTo();
                    for (EmailAddress address : emailAddressesTo)
                    {
                        if (address.getEmail().equalsIgnoreCase(value))
                        {
                            wasFound = true;
                        }
                    }
                    if (wasFound)
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case FROM:
                    if (message.getFrom().getEmail().equalsIgnoreCase(value))
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case KEYWORD:
                    if (((JavaMailGmailMessage)message).getMessage().match(new BodyTerm(value)))
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case CC:
                    wasFound = false;
                    List<EmailAddress> emailAddressesCC = message.getCc();
                    for (EmailAddress address : emailAddressesCC)
                    {
                        if (address.getEmail().equalsIgnoreCase(value))
                        {
                            wasFound = true;
                        }
                    }
                    if (wasFound)
                    {
                        matchedEmails.add(message);
                    }
                    break;
                case UNREAD:
                    if (((JavaMailGmailMessage)message).getMessage()
                            .match(new FlagTerm(new Flags(Flags.Flag.SEEN), false)))
                    {
                        matchedEmails.add(message);
                    }
                    break;
            }
            LOG.debug("Processing record: " + counter + " of " + total + "  "
                    + Math.round( ((double)counter * 100) / ((double)total) ) + "% done");
            counter++;
        }
        if (matchedEmails.size() == 0)
        {
            LOG.debug("No emails found with " + strategy.name() + " of " + value);
        }
        else
        {
            LOG.debug("Filtered down to " + matchedEmails.size() + " from " + this.emails.size()
                    + " on criteria " + strategy.name() + " equal to " + value);
        }
        return new GmailMessageList(matchedEmails);
    }

    public int size() {
        return emails.size();
    }

    public boolean isEmpty() {
        return emails.isEmpty();
    }

    public boolean contains(Object o) {
        return emails.contains(o);
    }

    public Iterator iterator() {
        return emails.iterator();
    }

    public Object[] toArray() {
        return emails.toArray();
    }

    public boolean add(GmailMessage o) {
        return emails.add((GmailMessage)o);
    }

    public boolean remove(Object o) {
        return emails.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return emails.containsAll(c);
    }

    public boolean addAll(Collection c) {
        return emails.addAll((Collection<GmailMessage>) c);
    }

    public boolean addAll(int index, Collection c) {
        return emails.addAll(index, (Collection<GmailMessage>) c);
    }

    public boolean removeAll(Collection<?> c) {
        return emails.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return emails.retainAll(c);
    }

    public void clear() {
        emails.clear();
    }

    public GmailMessage get(int index) {
        return emails.get(index);
    }

    public GmailMessage set(int index, GmailMessage element) {
        return emails.set(index,(GmailMessage)element);
    }

    public void add(int index, GmailMessage element) {
        emails.add(index,(GmailMessage)element);
    }

    public GmailMessage remove(int index) {
        return emails.remove(index);
    }

    public int indexOf(Object o) {
        return emails.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return emails.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return emails.listIterator();
    }

    public ListIterator listIterator(int index) {
        return emails.listIterator(index);
    }

    public List subList(int fromIndex, int toIndex) {
        return emails.subList(fromIndex,toIndex);
    }

    public Object[] toArray(Object[] a) {
        return emails.toArray((GmailMessage[])a);
    }
}
