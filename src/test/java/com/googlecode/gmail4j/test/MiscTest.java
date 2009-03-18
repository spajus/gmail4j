package com.googlecode.gmail4j.test;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.junit.Test;

import com.googlecode.gmail4j.auth.Credentials;
import com.googlecode.gmail4j.util.LoginDialog;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class MiscTest {

	@Test
	public void testRss() throws Exception {
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				if ("mail.google.com".equals(getRequestingHost())) {
					Credentials login = LoginDialog.show();
					return new PasswordAuthentication(login.getUsername(), 
							login.getPasword());
				}
				return null;
			}
		});
		URL feedSource = new URL("https://mail.google.com/mail/feed/atom");
		SyndFeedInput feedInput = new SyndFeedInput();
		SyndFeed gmail = feedInput.build(new XmlReader(feedSource));
		for (Object x : gmail.getEntries()) {
			System.out.println(x);
		}
	}
	
}
