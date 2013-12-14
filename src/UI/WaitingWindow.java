package UI;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class WaitingWindow extends JFrame implements Runnable{

	private JPanel panel = new JPanel();
	private JLabel label = new JLabel();
	private StringBuffer labelContent;
	int counter = 0;
	
	public WaitingWindow(String labelContent) {
		this.setTitle("状态栏");
		this.labelContent = new StringBuffer(labelContent);
		label.setText(labelContent.toString());
		panel.add(label);
		this.add(panel);
		setLocation(500, 250);
		setSize(200, 120);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Thread(new WaitingWindow("正在连接到服务器")).run();// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while (true) {
			changeLabel();
			try {
				Thread.sleep(250);
				this.repaint();
				this.panel.repaint();
				this.label.repaint();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	private void changeLabel() {
		// TODO Auto-generated method stub
		if (counter < 6) {
			labelContent.append(" .");
			counter ++;
		} else {
			counter = 0;
			int start = labelContent.indexOf(" ");
			int end = labelContent.length();
			labelContent.delete(start, end);
		}
		label.setText(labelContent.toString());
	}
	public void setLabelContent(StringBuffer labelContent) {
		this.labelContent = labelContent;
	}

}
