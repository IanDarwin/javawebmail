package jmail;

import java.util.Arrays;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

@ManagedBean @SessionScoped
public class Mail {

	@Resource(mappedName="java:jboss/mail/Default")
	private Session mSession;
	private Properties p;
	private Store mStore;
	private boolean loggedIn;
	private Message[] list;
	private String userName;
	private String password;

	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	@PostConstruct
	public void initProps() {
		System.out.println("Mail.initProps()");
		p = mSession.getProperties();
		p.setProperty("mail.imap.host", "dos.old");
		p.setProperty("mail.imap.ssl.trust", "dos.old");
		p.put("mail.imap.ssl.enable", true);
	}
	
	public String login() {
		System.out.println("Mail.login(): username " + userName);
		try {
			mStore = mSession.getStore("imaps");
			mStore.connect(userName, password);
		} catch (MessagingException e) {
			throw new RuntimeException("getStore failed: " + e, e);
		}
		return "Inbox";
	}

	public Message[] getList() {
		System.out.println("Mail.getList(): Mail Session = " + mSession);
		try {
			list = mStore.getDefaultFolder().getMessages();
			System.out.println("List size = " + list.length);
			// order by date descending:
			Arrays.sort(list, (m1,m2)->{
				try {
					return m2.getSentDate().compareTo(m1.getSentDate());
				} catch (MessagingException e) {
					throw new RuntimeException("Mail error: " + e, e);
				}
			});
			return list;
		} catch (MessagingException e) {
			throw new RuntimeException("Mail failure: " + e, e);
		}
	}
	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return "XXXXXX";
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
