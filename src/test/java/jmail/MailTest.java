package jmail;

import static org.junit.Assert.*;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class MailTest {
	
	private static final String MESSAGE_ID_HEADER = "message-id";
	private static final String WANTED_MESSAGE_ID = "<JavaMail.Message.12345.GOTYA.COM.erewhon.slugvax>";
	private Mail target;
	
	@Before
	public void setup() {
		target = new Mail();
	}

	@Test
	public void testFindMessageById() throws MessagingException {
		Folder f = mock(Folder.class);
		target.mFolder = f;
		Message m1 = mock(Message.class);
		Message m2 = mock(Message.class);
		when (m1.getHeader(MESSAGE_ID_HEADER)).thenReturn(new String[]{"bleah"});
		when (m2.getHeader(MESSAGE_ID_HEADER)).thenReturn(new String[]{WANTED_MESSAGE_ID});
		when (f.getMessages()).thenReturn(new Message[]{m1, m2});
		Message message = target.findMessageById(WANTED_MESSAGE_ID);
		assertNotNull(message);
		assertSame(message, m2);
	}
	
	@Test
	public void testFindMessageByIdBad() throws MessagingException {
		Folder f = mock(Folder.class);
		target.mFolder = f;
		Message m1 = mock(Message.class);
		Message m2 = mock(Message.class);
		when (m1.getHeader(MESSAGE_ID_HEADER)).thenReturn(new String[]{"bleah"});
		when (m2.getHeader(MESSAGE_ID_HEADER)).thenReturn(new String[]{"meh"});
		when (f.getMessages()).thenReturn(new Message[]{m1, m2});
		Message message = target.findMessageById(WANTED_MESSAGE_ID);
		assertNull(message);
	}

}
