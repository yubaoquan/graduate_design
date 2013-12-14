package net;

import java.io.*;
import java.nio.file.Path;
import java.text.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import beans.LoginInformation;

/**
 * 有一封邮件就需要建立一个ReciveMail对象
 */
public class ReceiveMail {
	private MimeMessage mimeMessage = null;
	private String saveAttachPath = ""; // 附件下载后的存放目录
	private StringBuffer bodyText = new StringBuffer();// 存放邮件内容
	private String dateFormat = "yy-MM-dd HH:mm"; // 默认的日前显示格式
	private BufferedWriter bw;
	private FileWriter fw;
	private static String filePathPrefix;

	public ReceiveMail(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	public void setMimeMessage(MimeMessage mimeMessage) {
		this.mimeMessage = mimeMessage;
	}

	/**
	 * 获得发件人的地址和姓名
	 */
	public String getFrom() throws Exception {
		InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
		String from = address[0].getAddress();
		if (from == null)
			from = "";
		String personal = address[0].getPersonal();
		if (personal == null)
			personal = "";
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * 获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同 "to"----收件人 "cc"---抄送人地址 "bcc"---密送人地址
	 */
	public String getMailAddress(String type) throws Exception {
		String mailaddr = "";
		String addtype = type.toUpperCase();
		InternetAddress[] address = null;
		if (addtype.equals("TO") || addtype.equals("CC") || addtype.equals("BCC")) {
			if (addtype.equals("TO")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.TO);
			} else if (addtype.equals("CC")) {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.CC);
			} else {
				address = (InternetAddress[]) mimeMessage.getRecipients(Message.RecipientType.BCC);
			}
			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String email = address[i].getAddress();
					if (email == null)
						email = "";
					else {
						email = MimeUtility.decodeText(email);
					}
					String personal = address[i].getPersonal();
					if (personal == null)
						personal = "";
					else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + email + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new Exception("Error emailaddr type!");
		}
		return mailaddr;
	}

	/**
	 * 获得邮件主题
	 */
	public String getSubject() throws MessagingException {
		String subject = "";
		try {
			subject = MimeUtility.decodeText(mimeMessage.getSubject());
			if (subject == null)
				subject = "";
		} catch (Exception exce) {
		}
		return subject;
	}

	/**
	 * 获得邮件发送日期
	 */
	public String getSentDate() throws Exception {
		Date sentdate = mimeMessage.getSentDate();
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(sentdate);
	}

	/**
	 * 获得邮件正文内容
	 */
	public String getBodyText() {
		return bodyText.toString();
	}

	/**
	 * 解析邮件，把得到的邮件内容保存到一个StringBuffer对象中，解析邮件 主要是根据MimeType类型的不同执行不同的操作，一步一步的解析
	 */
	public void getMailContent(Part part) throws Exception {
		String contentType = part.getContentType();
		int nameIndex = contentType.indexOf("name");
		boolean conname = false;
		if (nameIndex != -1)
			conname = true;
		System.out.println("CONTENTTYPE: " + contentType);
		if (part.isMimeType("text/plain") && !conname) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodyText.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
	}

	/**
	 * 判断此邮件是否需要回执，如果需要回执返回"true",否则返回"false"
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replySign = false;
		String needReply[] = mimeMessage.getHeader("Disposition-Notification-To");
		if (needReply != null) {
			replySign = true;
		}
		return replySign;
	}

	/**
	 * 获得此邮件的Message-ID
	 */
	public String getMessageId() throws MessagingException {
		return mimeMessage.getMessageID();
	}

	/**
	 * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】 pop3不提供flag功能
	 */
	public boolean isOld() throws MessagingException {
		boolean isOld = false;
		Flags flags = ((Message) mimeMessage).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isOld = true;
				System.out.println("seen Message.......");
				break;
			}
		}
		return isOld;
	}

	/**
	 * 判断此邮件是否包含附件
	 */
	public boolean isContainAttach(Part part) throws Exception {
		boolean attachFlag = false;
		String contentType = part.getContentType();
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE))))
					attachFlag = true;
				else if (mpart.isMimeType("multipart/*")) {
					attachFlag = isContainAttach((Part) mpart);
				} else {
					String contype = mpart.getContentType();
					if (contype.toLowerCase().indexOf("application") != -1)
						attachFlag = true;
					if (contype.toLowerCase().indexOf("name") != -1)
						attachFlag = true;
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			attachFlag = isContainAttach((Part) part.getContent());
		}
		return attachFlag;
	}

	/**
	 * 【保存附件】
	 */
	public void saveAttachment(Part part) throws Exception {
		String fileName = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String disposition = mpart.getDisposition();
				if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
					fileName = mpart.getFileName();
					if (fileName.toLowerCase().indexOf("gb2312") != -1 || fileName.toLowerCase().indexOf("gb18030") != -1|| fileName.toLowerCase().indexOf("gbk") != -1) {
						fileName = MimeUtility.decodeText(fileName);
					}
					fileName = "附件" + fileName;
					fileName = replaceIllegalCharacters(fileName);
					saveFile(fileName, mpart.getInputStream());
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttachment(mpart);
				} else {
					fileName = mpart.getFileName();
					if ((fileName != null) && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
						fileName = MimeUtility.decodeText(fileName);
						fileName = "附件" + fileName;
						fileName = replaceIllegalCharacters(fileName);
						saveFile(fileName, mpart.getInputStream());
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			saveAttachment((Part) part.getContent());
		}
	}

	/**
	 * 【设置附件存放路径】
	 */

	public void setAttachPath(String attachpath) {
		this.saveAttachPath = attachpath;
	}

	/**
	 * 【设置日期显示格式】
	 */
	public void setDateFormat(String format) throws Exception {
		this.dateFormat = format;
	}

	/**
	 * 【获得附件存放路径】
	 */
	public String getAttachPath() {
		return saveAttachPath;
	}

	/**
	 * 【真正的保存附件到指定目录里】
	 */
	private void saveFile(String fileName, InputStream in) throws Exception {
		String osName = System.getProperty("os.name");
		String storeDir = getAttachPath();
		String separator = "";
		if (osName == null)
			osName = "";
		if (osName.toLowerCase().indexOf("win") != -1) {
			separator = "\\";
			if (storeDir == null || storeDir.equals(""))
				storeDir = "E:\\receive";
		} else {
			separator = "/";
			storeDir = "/tmp";
		}
		File storeFolder = new File(storeDir);
		storeFolder.mkdirs();
		File storeFile = new File(storeDir + separator + fileName);
		System.out.println("storefile's path: " + storeFile.toString());
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(storeFile));
			bis = new BufferedInputStream(in);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("文件保存失败!");
		} finally {
			bos.close();
			bis.close();
		}
	}

	/**
	 * PraseMimeMessage类测试
	 */
	public static void loginAndReceiveMail(LoginInformation li) throws Exception {

		String smtpServerAddress = li.getSmtpServerName();
		String pop3ServerAddress = li.getPop3ServerName();
		String userName = li.getUserName();
		String password = li.getPassword();
		
		Store store = initStore(smtpServerAddress, pop3ServerAddress, userName, password);
		store.connect();

		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_ONLY);
		Message message[] = folder.getMessages();
		System.out.println("Messages's length: " + message.length);
		ReceiveMail pmm = null;
		for (int i = 0; i < message.length; i++) {
			pmm = new ReceiveMail((MimeMessage) message[i]);
			saveNewMail(message, pmm, i);
		}
	}

	private static Store initStore(String smtpServerAddress, String pop3ServerAddress, String userName, String password) throws NoSuchProviderException {
		int pop3ServerPort = 110;
		int smtpServerPort = 25;
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpServerAddress);
		props.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(props, null);
		URLName urlName = new URLName("pop3", pop3ServerAddress, pop3ServerPort, null, userName, password);
		setFilePathPrefix("E:\\receive\\" + userName + File.separator);
		Store store = session.getStore(urlName);
		return store;
	}
	
	private static void saveNewMail(Message[] message, ReceiveMail pmm, int i) throws MessagingException, Exception {
		String subjectName = pmm.getSubject();
		int subjectNameLength = 20;
		subjectName = replaceIllegalCharacters(subjectName);
		subjectName = subjectName.length() < subjectNameLength ? subjectName : subjectName.substring(0, subjectNameLength);
		String singleMailFolderPath = filePathPrefix + subjectName;
		makeFolder(singleMailFolderPath);
		String filePath = singleMailFolderPath + File.separator + subjectName + ".txt";

		System.out.println(filePath);
		pmm.initDos(filePath);
		pmm.setAttachPath(singleMailFolderPath);
		pmm.saveMail(message, pmm, i);
		pmm.closeDos();
	}

	private static String replaceIllegalCharacters(String subjectName) {
		char[] illegalCharacters = { ':', '/', '\\', '?', '*', '<', '>', '|', '\"',' ' };
		for (char ch : illegalCharacters) {
			subjectName = subjectName.replace(ch, '_');
		}
		return subjectName;
	}

	private static void makeFolder(String folderPath) {
		File folder = new File(folderPath);
		folder.mkdirs();
	}

	private void initDos(String filePath) {

		File file = new File(filePath);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveMail(Message[] message, ReceiveMail pmm, int i) throws MessagingException, Exception {
		writeLine("subject: " + pmm.getSubject() + "\n");
		System.out.println(pmm.getSubject());
		writeLine("sentdate: " + pmm.getSentDate() + "\n");
		writeLine("form: " + pmm.getFrom() + "\n");
		writeLine("to: " + pmm.getMailAddress("to") + "\n");
		pmm.setDateFormat("yy年MM月dd日 HH:mm");
		writeLine("sentdata: " + pmm.getSentDate() + "\n");
		pmm.getMailContent((Part) message[i]);
		writeLine("content: " + pmm.getBodyText() + "\n");
		pmm.saveAttachment((Part) message[i]);
	}

	private void closeDos() {
		try {
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bw = null;
			fw = null;
		}
	}

	private void writeLine(String str) {
		try {
			bw.write(str, 0, str.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setFilePathPrefix(String filePathPrefix) {
		ReceiveMail.filePathPrefix = filePathPrefix;
	}
}