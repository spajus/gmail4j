package com.googlecode.gmail4j.auth;

import com.googlecode.gmail4j.GmailException;

public class Credentials {

    private char[] password;

    private String username;

    public Credentials() {
        // have to set username/password manually
    }

    public Credentials(final String username, final char[] password) {
        setUsername(username);
        setPassword(password);
        validate();
    }
    
    public void validate() {
        if (username == null || username.length() == 0 
                ||password == null || password.length == 0) {
            throw new GmailException("No username and/or password provided");
        }
    }
 
    public void setUsername(final String username) {
        this.username = username;
    }

    public void setPassword(final char[] password) {
        this.password = new char[password.length];
        System.arraycopy(password, 0, this.password, 0, password.length);
        disposePassword(password);
    }

    private void disposePassword(final char[] password) {
        for (int i = 0; i < password.length; i++) {
            password[i] = 0;
        }
    }

    public char[] getPasword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }
    
    public void dispose() {
        disposePassword(this.password);
    }
    
    @Override
    protected void finalize() throws Throwable {
        dispose();
    }

}
