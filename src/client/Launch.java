package client;

import client.readme.README;
import client.tasks.common.FiveSecondTimer;
import client.tasks.common.TaskThatWillFail;
import client.tasks.linux.DisableSSHRootLogin;
import client.tasks.linux.EnableLinuxFirewall;
import client.tasks.windows.EnableWindowsFirewall;

import java.io.IOException;

/**
 * Handles preparing the main window
 *
 * Initializes information needed beforehand,
 * such as tasks and readme information
 */
public class Launch {
	/**
     * Initializes information and makes the main window
	 */
	public Launch() {
		this.initializeTasks();

		try {
			README readme = README.getReadme();

			MainWindow mainWindow = new MainWindow(readme);
		} catch(IOException e) {
			e.printStackTrace();
			System.out.println("Cannot get README information");
			System.exit(1);
		}
	}

	/**
	 * Initializes the tasks that are presented to the user
	 */
	private void initializeTasks() {
		// Linux tasks
		new EnableLinuxFirewall();
		new DisableSSHRootLogin();

		// Common tasks
		new FiveSecondTimer();
		new TaskThatWillFail();

		// Windows tasks
		new EnableWindowsFirewall();
	}
}
