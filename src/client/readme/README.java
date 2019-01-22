package client.readme;

import java.io.IOException;

public class README {
	public static README getReadme() throws IOException  {
		READMEDownloader downloader = new READMEDownloader();
		String readmeText = downloader.downloadReadmeHTML();
		return READMEParser.parse(readmeText);
	}

	private String[] authorizedAdmins;
	private String[] authorizedUsers;
	private String[] requiredServices;
	private String initialRootPassword;

	public README(
			String[] authorizedAdmins,
			String[] authorizedUsers,
			String[] requiredServices,
			String initialRootPassword
	) {
		this.authorizedAdmins = authorizedAdmins;
		this.authorizedUsers = authorizedUsers;
		this.requiredServices = requiredServices;
		this.initialRootPassword = initialRootPassword;
	}

	public String getInitialRootPassword() {
		return initialRootPassword;
	}

	public String[] getAuthorizedAdmins() {
		return authorizedAdmins;
	}

	public String[] getAuthorizedUsers() {
		return authorizedUsers;
	}

	public String[] getRequiredServices() {
		return requiredServices;
	}

	public boolean isValidUser(String name) {
		for (String username : this.authorizedUsers) {
			if (name.equals(username)) {
				return true;
			}
		}

		return false;
	}

	public boolean isValidAdmin(String name) {
		for (String adminName : this.authorizedAdmins) {
			if (name.equals(adminName)) {
				return true;
			}
		}

		return false;
	}
}
