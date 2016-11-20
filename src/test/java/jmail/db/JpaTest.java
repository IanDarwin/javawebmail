package jmail.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JpaTest {

	private static EntityManagerFactory emf;
	private EntityManager em;

	@BeforeClass
	public static void setupJpaFactory() {
		emf = Persistence.createEntityManagerFactory("javawebmail");
	}

	@Before
	public void setupJpa() {
		em = emf.createEntityManager();
	}

	@Test
	public void noOp() {
		// empty, if we get here, JPA setup is OK
	}
}
