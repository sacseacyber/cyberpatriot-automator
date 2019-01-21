package client;

import client.tasks.TaskRunner;

import javax.swing.*;

public class Main extends JFrame {
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JPanel taskRunnerTab;
	private TaskRunner taskRunnerPanel;

	public Main() {
		super("CP Automator");

		this.setContentPane(panel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}
}
