package client;

import client.config.CPConfig;
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

//		try {
			CPConfig config = CPConfig.ReadFromFile(CPConfig.GetDefaultFileLocation());
//			README readme = README.getReadme();
			README readme = new README(
					new String[] { "ballen" },
					new String[] { "ballen", "apennyworth" },
					new String[] {},
					""
			);

			MainWindow mainWindow = new MainWindow(readme, config);
//		} catch(IOException e) {
//			e.printStackTrace();
//			System.out.println("Cannot get README information");
//			System.exit(1);
//		}
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
