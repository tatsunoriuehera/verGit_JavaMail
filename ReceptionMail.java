
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;
import java.util.Scanner;

public class ReceptionMail {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = "hostaddress";
		String user = "acount";
		String password = "password";

		Session session = null;
		Store store = null;
		Folder folder = null;

		try {
			session = Session.getDefaultInstance(System.getProperties(), null);
			store = session.getStore("pop3");
			store.connect(host, -1, user, password);

			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);

			if (folder.getMessageCount() == 0) {
				System.out.println("no message");
				return;
			}

			Message[] messages = folder.getMessages();
			for (int i = 0; i < messages.length; i++) {
				dumpPart(messages[i]);
			}

		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (folder != null)
					folder.close(false);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			try {
				if (store != null)
					store.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	public static void dumpPart(Part part) throws Exception {
		String html = "";
		boolean attachment = false;
		String subFlag = null;

		if (part instanceof Message) {
			Message message = (Message) part;
			Address[] address;
			if ((address = message.getFrom()) != null) {
				for (int j = 0; j < address.length; j++) {
					System.out.println("FROM: "
							+ MimeUtility.decodeText(address[j].toString()));
				}
			}
			System.out.println("title: " + message.getSubject());
		}

		if (part.isMimeType("text/plain")) {
		} else if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				dumpPart(mp.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			dumpPart((Part) part.getContent());
		} else if (part.isMimeType("text/html")) {
			html = ".html";
		} else{
			attachment = true;
		}

		if (attachment) {
			String disp = part.getDisposition();
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				String filename = part.getFileName();
				if (filename != null) {
					filename = MimeUtility.decodeText(filename);
				}
				else {
					filename = "attached file" + html;
				}
				OutputStream os = null;
				InputStream is = null;
				try {
					File file = new File(filename);
					if (file.exists()) {
						throw new IOException("same name file existence");
					}
					if(filename.matches(".*aaa.*")
					||filename.matches(".*bbb.*")
					||filename.matches(".*ccc.*")
					||filename.matches(".*ddd.*")){
						os = new BufferedOutputStream(new FileOutputStream(file));
						is = part.getInputStream();
						int c;
						while ((c = is.read()) != -1) {
							os.write(c);
						}
						System.out.println(filename + "save complete");
					}

				} catch (IOException e) {
					System.out.println("attached file save faild" + e);
				} finally {
					if (os != null)
						os.close();
					if (is != null)
						is.close();
				}
			}
			BatExecute bat =new BatExecute();
			bat.start();
		}
	}
}
