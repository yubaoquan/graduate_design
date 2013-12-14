package thread;

import UI.LoginUI;

public class LoginThread extends Thread{

	private LoginUI loginUI;
	public LoginThread(LoginUI loginUI) {
		this.loginUI = loginUI;
	}
	@Override
	public void run() {
		loginUI.initUI();
		
	}

}
