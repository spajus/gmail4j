package com.googlecode.gmail4j.message;

import java.util.Date;

import com.googlecode.gmail4j.client.Address;

public interface GmailMessage {

    public abstract String getTitle();

    public abstract Address getFrom();

    public abstract String getLink();

    public abstract Date getDate();

    public abstract String getPreview();
    
    public abstract String getContentText();

}