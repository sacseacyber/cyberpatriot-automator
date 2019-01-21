package client.tasks;

import client.Util;

import java.io.IOException;

public class EnableFirewall extends Task {
	public EnableFirewall() {
		super("Enable firewall");
	}

	public void run() {
		if (Util.isWindows()) {

		} else if (Util.isLinux()) {
			try {
				Process p = Runtime.getRuntime().exec("ufw enable");

				p.waitFor();

				this.finishTask("Updated firewall", true);
			} catch(IOException | InterruptedException e) {

			}
		}
	}
}
