package net;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import beans.*;

public class Transmitter {

	private Properties props;
	private Session session;
	private Transport transport = null;
	private static final int PORT = 25;
	private boolean loginSucceed = false;
	private LoginInformation loginInformation;
	private static Transmitter transmitter = null;
	private boolean sendSucceed = false;
	private MimeMessage msg;
	
	public static Transmitter getInstance(LoginInformation loginInformation) {
		if (Transmitter.transmitter == null) {
			transmitter = new Transmitter(loginInformation);
		}
		return transmitter;
	}
	
	private Transmitter(LoginInformation loginInformation) {
		this.loginInformation = loginInformation;
		props = new Properties();
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.transport.protocol", "smtp");
		session = Session.getDefaultInstance(props);
		msg = new MimeMessage(session);
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
	}
	public boolean loginServerSucceed() {
		return this.loginSucceed;
	}
	
	public void loginToServer() {
		try {
			this.transport.connect(loginInformation.getSmtpServerName(), loginInformation.getUserName(), loginInformation.getPassword());
		} catch (AuthenticationFailedException e) {
			System.out.println("认证失败!用户名或密码错误");
			this.loginSucceed = false;
			return;
		} catch (MessagingException e) {
			e.printStackTrace();
			this.loginSucceed = false;
			return;
		}
		this.loginSucceed = true;
	}
	
	public void fillMsg(MailBean mb) {
		try {
			InternetAddress fromAddress = new InternetAddress(transmitter.loginInformation.getUserName());
			msg.setFrom(fromAddress);
			msg.setSubject(mb.getSubject());
			msg.setText(mb.getText());
			if (mb.getExtraItemsAmount() > 0) {
				msg.setContent(mb.getMutipart());
			}
			msg.setSentDate(new Date());
			msg.setRecipients(Message.RecipientType.TO, mb.getReceiverAddresses());
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMail(MailBean mb) {
		this.fillMsg(mb);
		this.sendMail();
	}
	
	public void sendMail() {
		try {
			transport.sendMessage(msg,msg.getAllRecipients());
		} catch (MessagingException e) {
			this.sendSucceed = false;
			e.printStackTrace();
			closeConnection();
			return;
		}
		this.sendSucceed = true;
	}
	
	public void closeConnection() {
		if (transport.isConnected()) {
			try {
				transport.close();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean sendSucceed() {
		return sendSucceed;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
