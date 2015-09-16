package jmail;

import javax.faces.bean.ManagedBean;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ManagedBean(name="mb") @Getter @Setter @ToString @EqualsAndHashCode
public class MessageBean {
	String sender;
	String recipient;
	String subject;
	String body;
}
