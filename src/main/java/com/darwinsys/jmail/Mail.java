package com.darwinsys.jmail;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

@ManagedBean
public class Mail {

	@Resource(mappedName="java:jboss/mail/Default")
	Session mSession;
	
	Store mStore;
	
	Message[] list;

	// XXX compile it from java.net project...
	private String mboxProvider = null;
	
	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	@PostConstruct
	public void initStore() {
		try {
			mStore = mSession.getStore(mboxProvider);
		} catch (NoSuchProviderException e) {
			throw new RuntimeException("getStore failed: " + e, e);
		}
	}

	public Message[] getList() {
		try {
			System.out.println("Mail Session = " + mSession);
			list = mStore.getDefaultFolder().getMessages();
			System.out.println("List size = " + list.length);
			return list;
		} catch (MessagingException e) {
			throw new RuntimeException("Mail failure: " + e, e);
		}
	}
}
