package utils;

import net.Transmitter;
import UI.*;
import beans.LoginInformation;

public class LoginUtils {

	public static boolean loginInformationValid(LoginInformation loginInformation) {
		if (loginInformation.getUserName().trim().equals("")) {
			return false;
		}
		if (loginInformation.getPassword().trim().equals("")) {
			return false;
		}
		return true;
	}

	public static void selectSendOrReceive(LoginUI parentUI) {
		parentUI.dispose();
		LoginInformation li = parentUI.getLoginInformation();
		new MainTargetSelectingUI(li).initUI();
	}

	public static void loginToServer(LoginInformation li) {
		Transmitter.getInstance(li).loginToServer();
	}
	
	public static boolean loginServerSucceed(LoginInformation li) {
		return Transmitter.getInstance(li).loginServerSucceed();
	}
	
	private static WaitingWindow waitingWindow;
	
	public static void setWaitingWindow(WaitingWindow waitingWindow) {
		LoginUtils.waitingWindow = waitingWindow;
	}

	public static WaitingWindow getWaitingWindow() {
		return waitingWindow;
	}
	
	public static void showWaitingWindow(String content) {
		waitingWindow.setLabelContent(new StringBuffer(content));
		waitingWindow.setVisible(true);
	}
	
	public static void hideWaitingWindow() {
		waitingWindow.setVisible(false);
	}
	
	public static void main(String[] args) {
		LoginUtils.setWaitingWindow(new WaitingWindow("haha"));
		LoginUtils.showWaitingWindow("haha");
		Thread thread2 = new Thread(LoginUtils.getWaitingWindow());
		thread2.run();
	}
}
