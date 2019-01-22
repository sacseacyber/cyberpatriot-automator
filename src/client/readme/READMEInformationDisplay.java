package client.readme;

import javax.swing.*;

public class READMEInformationDisplay extends JComponent {
	private JPanel panel1;
	private JList<String> validUserDisplay;
	private JList<String> adminUserDisplay;

	public READMEInformationDisplay(README readme) {
		this.validUserDisplay.setListData(readme.getAuthorizedUsers());
		this.adminUserDisplay.setListData(readme.getAuthorizedAdmins());
	}

	private void createUIComponents() {
		this.validUserDisplay = new JList<>();
		this.adminUserDisplay = new JList<>();
	}
}
