package client;

import client.config.CPConfig;
import client.config.ConfigForm;
import client.readme.README;
import client.readme.READMEInformationDisplay;
import client.tasks.TaskRunner;

import javax.swing.*;

public class MainWindow extends JFrame {
	private JTabbedPane tabbedPane;
	private JPanel panel;
	private JPanel taskRunnerTab;
	private TaskRunner taskRunnerPanel;
	private READMEInformationDisplay readmeDisplay;
	private ConfigForm configForm;

	private README readme;
	private CPConfig cpconfig;

	MainWindow(README readme, CPConfig cpconfig) {
		super("CP Automator");

		this.readme = readme;
		this.cpconfig = cpconfig;

		this.setContentPane(panel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
	}

	private void createUIComponents() {
		this.taskRunnerPanel = new TaskRunner(this.readme);
		this.readmeDisplay = new READMEInformationDisplay(this.readme);
		this.configForm = new ConfigForm(this.cpconfig);
	}
}
