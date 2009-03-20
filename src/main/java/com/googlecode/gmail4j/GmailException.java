package com.googlecode.gmail4j;

public class GmailException extends RuntimeException {

    private static final long serialVersionUID = -2919776925807264765L;

    public GmailException(final String message) {
        super(message);
    }
    
    public GmailException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
