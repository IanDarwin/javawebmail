package jmail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Singleton
public class Settings {

	@PersistenceContext EntityManager em;
	
	List<User> allowedUsers = new ArrayList<>();
	
	@PostConstruct
	public void readUserList() {
		em.createQuery("from User").getResultList();
	}

	public boolean okHtml(User u, String fromAddress) {
		return false;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void setOkHtml(User u, String fromAddress) {
		// EMPTY
	}
}
