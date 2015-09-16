package jmail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

@ManagedBean @SessionScoped
public class Mail {

	@Resource(mappedName="java:jboss/mail/Default")
	private Session mSession;
	private Folder folder;
	private Message message;
	private Properties p;
	private Store mStore;
	private boolean loggedIn;
	private String userName;
	private String password;

	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	@PostConstruct
	public void initProps() {
		System.out.println("Mail.initProps()");
		p = mSession.getProperties();
		p.setProperty("mail.imaps.host", "dos.old");
		p.put("mail.imaps.starttls.enable", true);
		p.setProperty("mail.imaps.ssl.trust", "dos.old");
		p.put("mail.imaps.ssl.enable", true);
	}
	
	public String login() {
		System.out.println("Mail.login(): username " + userName);
		try {
			mStore = mSession.getStore("imaps");
			mStore.connect(userName, password);
			// That didn't throw an exception, so:
			loggedIn = true;
		} catch (MessagingException e) {
			throw new RuntimeException("getStore failed: " + e, e);
		}
		return "Inbox";
	}

	public List<Message> getList() {
		System.out.println("Mail.getList(): Mail Session = " + mSession);
		try {
			folder = mStore.getFolder("INBOX");
			if (!folder.isOpen()) { 
				folder.open(Folder.READ_WRITE); 
			}
			System.out.println("Folder is " + folder);
			final int messageCount = folder.getMessageCount();
			if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				System.out.printf("%d Messages\n", messageCount);
				if (folder.hasNewMessages()) {
					System.out.printf("%d new Messages\n", folder.getNewMessageCount());
				}
				System.out.printf("%d unread mMessages\n", folder.getUnreadMessageCount());
			}
			if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
				System.out.println("Has subfolders:");
				for (Folder f : folder.list()) {
					System.out.println("\t" + f);
				}
			}

			List<Message> subList = new ArrayList<>();
			for (int i = messageCount; i > 0 && subList.size() <= 50; i--) {
				subList.add(folder.getMessage(i));
			}
			// order by date descending:
			Collections.sort(subList, (m1,m2)->{
				try {
					return m2.getSentDate().compareTo(m1.getSentDate());
				} catch (MessagingException e) {
					throw new RuntimeException("Mail error: " + e, e);
				}
			});
			return subList;
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
	
	/** Only called from JSF page */
	public void wireMessage(int number) throws MessagingException {
		if (folder == null) {
			message = null;
			return;
		}
		if (number < 1) {
			System.err.println("wireMessage: invalid messageNumber " + number);
			message = null;
			return;
		}
		message = folder.getMessage(number);
		
	}
	
	public Message getMessage() {
		return message;
	}
	
	public String getContent() throws Exception {
		return message == null ? "" :
			message.getContent().
				toString().
				replaceAll("<", "&lt;").
				replaceAll("\n", "<br/>");
	}
}
