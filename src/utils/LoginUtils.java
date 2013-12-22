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
	
}
