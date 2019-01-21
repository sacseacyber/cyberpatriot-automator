package client.tasks;

import javax.swing.*;

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
