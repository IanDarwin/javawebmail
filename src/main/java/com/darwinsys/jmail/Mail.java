package com.darwinsys.jmail;

import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;

@ManagedBean
public class Mail {

	public Mail() {
		System.out.println("Mail.Mail()");
	}
	
	private List<Message> list = Arrays.asList(
		new Message[] { new Message(), new Message(), new Message() }
	);

	public List<Message> getList() {
		System.out.println("List size = " + list.size());
		return list;
	}
}
