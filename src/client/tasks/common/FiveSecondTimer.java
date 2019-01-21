package client.tasks.common;

import client.tasks.Task;
import client.tasks.taskannotations.LinuxTask;
import client.tasks.taskannotations.WindowsTask;

import javax.swing.*;

/**
 * A five second timer to demonstrate the capabilities of the progress bar
 */
@LinuxTask(disabled = true)
@WindowsTask(disabled = true)
public class FiveSecondTimer extends Task {
	private float count = 0;
	private Timer timer;

	public FiveSecondTimer() {
		super("Five second timer");
	}

	public void run() {
		this.timer = new Timer(500, actionEvent -> {
			String message;
			if (getCount() < 5) {
				message = "First part";
			} else {
				message = "Second part";
			}

			if (getCount() >= 10) {
				finishTask("Finished timer", true);
				stop();
			} else {
				updateProgress(count / 10, message);
			}

			incrementCount();
		});

		this.timer.start();
	}

	/**
	 * Private methods for accessing information outside of the timer
	 */

	private float getCount() {
		return this.count;
	}
	private void incrementCount() {
		this.count++;
	}
	private void stop() {
		this.timer.stop();
	}
}
