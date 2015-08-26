package com.darwinsys.jmail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
	LocalDateTime date = LocalDateTime.now();
	String sender = "A Luser <lusr@a.com>";
	String subject = "Want your unclaimed fund in a chest from a diplomat?";
	String id = "123456789abcdef@localhost.dom";
	
	public LocalDateTime getDate() {
		return date;
	}
	
	public String getDateString() {
		return date.format(DateTimeFormatter.ofPattern("yyyy MM dd hh:mm"));
	}
	
	public String getSender() {
		return sender;
	}
	
	public String getSubject() {
		return subject;
	}

	/** Should be getMessageId() */
	public String getId() {
		return id;
	}
}
	
