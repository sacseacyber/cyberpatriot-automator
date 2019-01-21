package client.tasks.linux;

import client.tasks.Task;
import client.tasks.taskannotations.LinuxTask;

import java.io.IOException;

/**
 * A Linux task to enable the firewall
 */
@LinuxTask
public class EnableLinuxFirewall extends Task {
	public EnableLinuxFirewall() {
		super("Enable firewall");
	}

	public void run() {
		try {
			// Execute the command to enable the firewall
			Process p = Runtime.getRuntime().exec("ufw enable");

			p.waitFor();

			this.finishTask("Updated firewall", true);
		} catch(IOException | InterruptedException e) {
			this.finishTask("Failed to update firewall", false);
		}
	}
}
