package UI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import beans.LoginInformation;

import utils.LoginUtils;


public class LoginUI {
	public static enum LoginCommandCode {CONFIRM,REST}
	private JFrame frame = new JFrame("邮件代理系统");
	private JPanel centerPanel = new JPanel();
	private JPanel southPanel = new JPanel();
	private JPanel serverNamePanel = new JPanel();
	private JPanel userNamePanel = new JPanel();
	private JPanel passwordPanel = new JPanel();

	private BorderLayout frameLayout = new BorderLayout();
	private GridLayout centerLayout = new GridLayout(3, 1);
	private FlowLayout dataLayout = new FlowLayout();
	private GridLayout southLayout = new GridLayout(1, 2);

	private JLabel serverNameLabel = new JLabel("server name:");
	private JLabel userNameLabel = new JLabel("user name:");
	private JLabel passwordLabel = new JLabel("password:");

	private JComboBox<String> serverNameSelector = new JComboBox<String>();
	private JTextField userNameTextField = new JTextField(20);
	private JPasswordField passwordTextField = new JPasswordField(20);

	private JButton confirmButton = new JButton("login");
	private JButton resetButton = new JButton("reset");

	private LoginUIMonitor monitor = new LoginUIMonitor();

	private LoginInformation loginInformation = new LoginInformation();
	
	public void launch() {
		this.initUI();
		this.frame.setVisible(true);
	}
	
	private void initUI() {
		this.setAttributes();
		this.addComponents();
		this.frame.setVisible(true);
	}

	private void setAttributes() {
		configureFrame();
		configurePanels();
		configureOthercomponents();
	}

	private void configureFrame() {
		this.frame.setLayout(this.frameLayout);
		this.frame.setLocation(400, 200);
		this.frame.setSize(500, 350);
		this.frame.setResizable(false);
		//this.frame.setVisible(true);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void configurePanels() {
		this.centerPanel.setLayout(this.centerLayout);
		this.centerPanel.setBorder(BorderFactory.createEtchedBorder());
		this.southPanel.setLayout(this.southLayout);
		this.southLayout.setHgap(60);

		this.serverNamePanel.setLayout(this.dataLayout);
		this.userNamePanel.setLayout(this.dataLayout);
		this.passwordPanel.setLayout(this.dataLayout);
	}

	private void configureOthercomponents() {
		this.serverNameSelector.addItem("");
		this.serverNameSelector.addItem("163");
		this.serverNameSelector.addItem("QQ");
		this.confirmButton.setActionCommand(LoginCommandCode.CONFIRM.toString());
		this.resetButton.setActionCommand(LoginCommandCode.REST.toString());
	}

	private void addComponents() {
		this.serverNamePanel.add(this.serverNameLabel);
		this.serverNamePanel.add(this.serverNameSelector);
		this.userNamePanel.add(this.userNameLabel);
		this.userNamePanel.add(this.userNameTextField);
		this.passwordPanel.add(this.passwordLabel);
		this.passwordPanel.add(this.passwordTextField);
		
		this.confirmButton.addActionListener(this.monitor);
		this.resetButton.addActionListener(this.monitor);
		
		this.centerPanel.add(this.serverNamePanel);
		this.centerPanel.add(this.userNamePanel);
		this.centerPanel.add(this.passwordPanel);
		
		this.southPanel.add(this.confirmButton);
		this.southPanel.add(this.resetButton);
		
		this.frame.add(this.centerPanel, BorderLayout.CENTER);
		this.frame.add(this.southPanel, BorderLayout.SOUTH);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LoginUI().initUI();
	}

	private class LoginUIMonitor implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			LoginCommandCode commandCode = LoginCommandCode.valueOf(e.getActionCommand());
			switch (commandCode) {
				case CONFIRM:
					onConfirmButtonClick();
					break;
				case REST:
					onResetButtonClick();
					break;
				default:
					System.exit(-1);
			}
		}

		private void onResetButtonClick() {
			cleanTextField();
		}

		private void onConfirmButtonClick() {
			fillLoginInformation();
			if (!informationValid()) {
				return;
			}
			LoginUtils.loginToServer(loginInformation);
			if (LoginUtils.loginServerSucceed(loginInformation)) {
				LoginUtils.selectSendOrReceive(LoginUI.this);
			} else {
				JOptionPane.showMessageDialog(frame, (String)"登录失败.请确认用户名和密码填写正确并且网络连接正常.", "错误", JOptionPane.WARNING_MESSAGE);
			}
		}
		
		private boolean informationValid() {
			if (LoginUI.this.serverNameSelector.getSelectedIndex() == 0) {
				JOptionPane.showMessageDialog(frame, (String)"请选择邮件服务器.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			if (!LoginUtils.loginInformationValid(loginInformation)) {
				JOptionPane.showMessageDialog(frame, (String)"用户名和密码不能为空,请检查后重新输入.", "错误", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			return true;
		}
	}

	private void fillLoginInformation() {
		this.loginInformation.setServerName(this.getServerNameFromTextField());
		this.loginInformation.setUserName(this.getUserNameFromTextField());
		this.loginInformation.setPassword(this.getPasswordFromTextField());
	}
	
	public void cleanTextField() {
		this.serverNameSelector.setSelectedIndex(0);
		this.userNameTextField.setText("");
		this.passwordTextField.setText("");
	}
	
	public String getServerNameFromTextField() {
		String selection = (String) this.serverNameSelector.getSelectedItem();
		if (selection.equals("")) {
			return "";
		} else {
			return "smtp." + selection + ".com"; 
		}
	}
	
	public String getUserNameFromTextField() {
		return this.userNameTextField.getText();
	}
	
	public String getPasswordFromTextField() {
		return new String(this.passwordTextField.getPassword());
	}
	
	public void dispose() {
		this.frame.dispose();
	}

	public LoginInformation getLoginInformation() {
		return loginInformation;
	}
}
