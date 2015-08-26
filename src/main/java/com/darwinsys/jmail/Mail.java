package com.darwinsys.jmail;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.bean.ManagedBean;
import javax.mail.Session;

@ManagedBean
public class Mail {

	@Resource(mappedName="java:jboss/mail/Default")
	Session mSession;
	
	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	private List<Message> list = Arrays.asList(
		new Message[] { new Message(), new Message(), new Message() }
	);

	public List<Message> getList() {
		System.out.println("Mail Session = " + mSession);
		System.out.println("List size = " + list.size());
		return list;
	}
}
