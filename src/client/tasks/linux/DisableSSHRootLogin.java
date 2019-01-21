package client.tasks.linux;

import client.tasks.Task;
import client.tasks.taskannotations.LinuxTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A Linux task that will disable the root login through SSH
 */
@LinuxTask
public class DisableSSHRootLogin extends Task {
	public DisableSSHRootLogin() {
		super("Disable SSH Root Login");
	}

	public void run() {
		List<String> fileContents = new ArrayList<>();
		boolean hasChangedRootLogin = false;

		// Get the SSH configuration file
		File sshd_config = new File("/etc/ssh/sshd_config");

		try {
			// Read the file
			Scanner reader = new Scanner(sshd_config);

			while (reader.hasNextLine()) {
				String nextLine = reader.nextLine();

				// If there is a configuration option for PermitRootLogin, override
				// with the value 'no'
				if (nextLine.startsWith("PermitRootLogin")) {
					fileContents.add("PermitRootLogin no");

					hasChangedRootLogin = true;
				} else {
					// Otherwise, preserve configuration - not our job
					fileContents.add(nextLine);
				}
			}

			// Close system resources
			reader.close();
		} catch (FileNotFoundException e) {
			// Won't get here on linux, but handle it
			this.finishTask("SSH config not found", false);
		}

		// One of two cases:
		// a) PermitRootLogin is there, but commented out
		// b) PermitRootLogin is not there
		// Either way add it as no
		if (!hasChangedRootLogin) {
			fileContents.add("PermitRootLogin no");
		}

		try {
			// Open the file again to write to it
			PrintWriter writer = new PrintWriter("/etc/ssh/sshd_config");

			for (String line : fileContents) {
				writer.write(line + "\n");
			}

			// Very important, without this the SSH configuration would be a blank file
			// This comes from experience
			writer.close();
		} catch(FileNotFoundException e) {
			// Won't get here on linux, but handle it
			this.finishTask("SSH config not found", false);
		}

		// Mark task as finished
		this.finishTask("Updated SSH config", true);
	}
}
