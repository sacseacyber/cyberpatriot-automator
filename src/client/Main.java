package client;

import javax.swing.*;

public class Main extends JFrame {
	private JTabbedPane tabbedPane;
	private JPanel panel;

	public Main() {
		super("CP Automator");

		this.setContentPane(panel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
}
