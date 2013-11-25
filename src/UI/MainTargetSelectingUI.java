package UI;

import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;

import beans.LoginInformation;


public class MainTargetSelectingUI {

	public MainTargetSelectingUI(LoginInformation li) {
		super();
		this.loginInformation = li;
	}

	public static enum Selection {SEND,RECEIVE}
	private JFrame frame = new JFrame("主任务选择");
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel("你想收邮件还是发邮件?");
	private JComboBox<Selection> box = new JComboBox<Selection>();
	private JButton confirmBotton = new JButton("OK");
	private FlowLayout layout = new FlowLayout();
	private MainTargetSelectingUIMonitor mainTargetSelectingUIMonitor = new MainTargetSelectingUIMonitor();
	private LoginInformation loginInformation;
	
	public void initUI() {
		this.setAttributes();
		this.addComponents();
	}
	
	public void setAttributes() {
		this.frame.setLocation(500, 200);
		this.frame.setSize(250, 150);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.panel.setLayout(this.layout);
		this.box.addItem(Selection.SEND);
		this.box.addItem(Selection.RECEIVE);
		this.frame.setVisible(true);
	}
	
	public void addComponents() {
		this.confirmBotton.addActionListener(this.mainTargetSelectingUIMonitor);
		this.panel.add(this.label);
		this.panel.add(this.box);
		this.panel.add(this.confirmBotton);
		this.frame.add(this.panel);
	}
	public static void main(String[] args) {
		LoginInformation testLi = new LoginInformation();
		new MainTargetSelectingUI(testLi).initUI();
	}
	
	private class MainTargetSelectingUIMonitor implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Selection selection = (Selection) box.getSelectedItem();
			switch (selection) {
				case SEND:
					onSendOptionSelected();
					break;
				case RECEIVE:
					onReceiveOptionSelected();
					break;
				default:
					System.exit(-1);
			}
		}

		private void onReceiveOptionSelected() {
			System.out.println("receive");
		}

		private void onSendOptionSelected() {
			MainTargetSelectingUI.this.frame.dispose();
			new EditMailUI(MainTargetSelectingUI.this.loginInformation).launch();
		}
		
	}
}
