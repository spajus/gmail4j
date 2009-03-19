package com.googlecode.gmail4j.auth;

public class Credentials {

	private char[] password;
	
	private String username;
	
	public Credentials() {
	    //have to set username/password manually
	}
	
	public Credentials(final String username, final char[] password) {
		setUsername(username);
		setPassword(password);
	}
	
	public void setUsername(final String username) {
		this.username = username;
	}
	
	public void setPassword(final char[] password) {
		this.password = new char[password.length];
		System.arraycopy(password, 0, this.password, 0, password.length);
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
	
}
