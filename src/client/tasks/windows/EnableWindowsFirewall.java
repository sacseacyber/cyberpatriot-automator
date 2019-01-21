package client.tasks.windows;

import client.tasks.Task;
import client.tasks.taskannotations.WindowsTask;

/**
 * A task to enable the firewall on Windows
 */
@WindowsTask(disabled = true)
public class EnableWindowsFirewall extends Task {
	public EnableWindowsFirewall() {
		super("Enable Windows firewall");
	}

	public void run() {
		// TODO: Implement a way to enable Windows firewall
		// https://helpdeskgeek.com/networking/windows-firewall-command-prompt-netsh/
	}
}
