package jmail;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

/**
 * The main program for the Mail application.
 */
@ManagedBean @SessionScoped
public class Mail {

	private static final String LIST_PAGE = "Inbox";
	private static final String COMPOSE_PAGE = "Compose";
	private static final String FORCE_REDIRECT = "";

	@Resource(mappedName="java:jboss/mail/Default")
	private Session mSession;
	Folder mFolder;
	private Message message;
	private Properties p;
	private Store mStore;
	private boolean loggedIn;
	private String userName;
	private String password;
	private MessageBean messageBean;
	private final static String DOMAIN = "darwinsys.com";

	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	@PostConstruct
	public void initProps() {
		System.out.println("Mail.initProps()");
		p = mSession.getProperties();
		p.setProperty("mail.imaps.host", "localhost");
		p.put("mail.imaps.starttls.enable", true);
		p.setProperty("mail.imaps.ssl.trust", "localhost");
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
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login failed."));
			return "login";
		}
		addFacesMessage("Welcome aboard!");
		return LIST_PAGE + FORCE_REDIRECT;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public String logout() throws MessagingException {
		if (mFolder.isOpen()) {
			mFolder.close(false);
		}
		loggedIn = false;
		return "index" + FORCE_REDIRECT;
	}

	public List<Message> getList() {
		System.out.println("Mail.getList(): Mail Session = " + mSession);
		try {
			mFolder = mStore.getFolder("INBOX");
			if (!mFolder.isOpen()) { 
				mFolder.open(Folder.READ_WRITE); 
			}
		} catch (Exception ex) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Folder open failed."));
			return Collections.emptyList();
		}
		try {
			System.out.println("Folder is " + mFolder);
			final int messageCount = mFolder.getMessageCount();
			if ((mFolder.getType() & Folder.HOLDS_MESSAGES) != 0) {
				System.out.printf("%d Messages\n", messageCount);
				if (mFolder.hasNewMessages()) {
					System.out.printf("%d new Messages\n", mFolder.getNewMessageCount());
				}
				System.out.printf("%d unread mMessages\n", mFolder.getUnreadMessageCount());
			}
			if ((mFolder.getType() & Folder.HOLDS_FOLDERS) != 0) {
				System.out.println("Has subfolders:");
				for (Folder f : mFolder.list()) {
					System.out.println("\t" + f);
				}
			}

			List<Message> subList = new ArrayList<>();
			for (int i = messageCount; i > 0 && subList.size() <= 50; i--) {
				Message m = mFolder.getMessage(i);
				// XXX check for deleted
				subList.add(m);
			}

			return subList;
		} catch (MessagingException e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mail retrieval failed."));
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
	public String submit(MessageBean mb) {
		if (!loggedIn) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login required"));
			
			return "login" + FORCE_REDIRECT;
		}
		if (mb == null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("submit(): MessageBean is null!"));
			return "";	// Stay on same page
		}
		
		Message m = new MimeMessage(mSession);
		
		try {
			// SEND IT!!!!!
			m.setFrom(new InternetAddress(getUserName() + "@" + DOMAIN));
			m.setRecipient(RecipientType.TO, new InternetAddress(mb.getRecipient()));
			m.setSubject(mb.getSubject());
			m.setText(mb.getBody());
	
			Transport.send(m);
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mail accepted by server for delivery!"));
			
			return "index" + FORCE_REDIRECT;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mail sending failed:" + e));
			e.printStackTrace();
			return "";
		}
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
		if (mFolder == null) {
			message = null;
			return;
		}
		if (number < 1) {
			System.err.println("wireMessage: invalid messageNumber " + number);
			message = null;
			return;
		}
		message = mFolder.getMessage(number);
	}
	
	public void wireMessageBean(MessageBean mb) {
		this.messageBean = mb;
	}

	@Produces
	public MessageBean getMessageBean() {
		try {
			return messageBean == null ? new MessageBean() : messageBean;
		} finally {
			this.messageBean = null;
		}
	}
	
	public Message getMessage() {
		return message;
	}
	
	/** Return the textual content of the message as a String;
	 * for now, parts containing "image", "application", etc are dropped.
	 * @return The message
	 * @throws Exception If anything goes wrong.
	 */
	public String getContent() throws Exception {
		if (message == null) {
			throw new IllegalStateException("called getContent with no Message focus");
		}
		final Object content = message.getContent();
		if (content instanceof MimeMultipart) {
			StringBuilder sb = new StringBuilder();
			Multipart multipart = (Multipart)content;
			for (int pNum = 1; pNum < multipart.getCount(); pNum++) {
				Part part = multipart.getBodyPart(pNum);

				String sct = part.getContentType();
				if (sct == null) {
					throw new IllegalArgumentException("invalid part");
				}
				ContentType ct = new ContentType(sct);
				System.out.println("Mesg " + message.getMessageNumber() + "; PART " + pNum + " ContentType " + ct);
				if (ct.getPrimaryType().equals("TEXT")) {
					InputStream is = part.getInputStream();
					int i;
					while ((i = is.read()) != -1) {
						char ch = (char)i;
						sb.append(ch == '\n' ? "<br/>" : ch);
					}
					sb.append("<br/><br/>");
				}
			}
			return sb.toString();
		}
		return message == null ? "" :
			content.
			toString().
			replaceAll("<", "&lt;").
			replaceAll("\n", "<br/>");
	}
	
	/**
	 * Add the message to the JSF context for display on the page
	 */
	public void addFacesMessage(String message) {
		System.out.println("Mail.addFacesMessage(): " + message);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(message));
	}
	
	/**
	 * Find a Message in mFolder by message id. MAY RETURN NULL!!
	 * @param messageId The id to look for
	 * @return The message, or null
	 * @throws MessagingException If JavaMail decides to have a hissy fit.
	 */
	Message findMessageById(String messageId) throws MessagingException {
		for (Message m : mFolder.getMessages()) {
			// XXX check for deleted
			String[] headers = m.getHeader("message-id");
			if (headers == null || headers.length == 0) {
				continue;
			}
			String h = headers[0];
			if (h != null && h.equals(messageId)) {
				return m;
			}
		}
		return null;
	}

	/** Deleting messages is bloody dangerous, so we delete by message ID even though it's more
	 * work, because of the possibility of concurrent updating by another client
	 * (or even another tab in the same webmail app)...
	 * @param messageId The Id of the message to delete
	 * @return The message for this messageId
	 * @throws MessagingException If things mess up
	 */
	public String delete(String messageId) throws MessagingException {
		System.err.println("Delete " + messageId);
		Message dm = findMessageById(messageId);
		if (dm == null) {
			addFacesMessage("Message Not Found!");
			// Don't return here, still go to the list.
		} else {
			dm.setFlag(Flags.Flag.DELETED, true);
			mFolder.expunge();
			addFacesMessage("Message deleted");
		}
		return LIST_PAGE + FORCE_REDIRECT;
	}

	public String reply(String messageId) throws MessagingException {
		System.err.println("Try to reply " + messageId);
		Message m = findMessageById(messageId);
		String subj = m.getSubject();
		if (!subj.startsWith("Re:")) {
			subj = "Re: " + subj;
		}
		MessageBean mb = new MessageBean();
		mb.setRecipient(m.getFrom()[0].toString());
		mb.setSubject(subj);
		wireMessageBean(mb);
		return COMPOSE_PAGE + "?recipient=" + m.getFrom()[0].toString() + "&subject=" + subj;
	}
	
	public String gotoNext() {
		System.err.println("GoTo Next ");
		return LIST_PAGE + FORCE_REDIRECT;
	}
	public String gotoPrevious() {
		System.err.println("GoTo Prev ");
		return LIST_PAGE + FORCE_REDIRECT;
	}
}
