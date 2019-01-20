package client.tasks;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a task that the program can do, e.g. enable the firewall
 */
public abstract class Task implements Runnable {
	/**
	 * The name of the task
	 */
	public final String name;
	/**
	 * For executing the task, also for using as a separate thread
	 */
	public abstract void run();

	/**
	 * The current progress of the task
	 */
	private int progress = 0;
	/**
	 * A list of callbacks for while the task is progressing, e.g. to update a progress bar
	 */
	private List<Consumer<TaskUpdate>> progressCallbacks;
	/**
	 * A list of callbacks for when the task is done
	 */
	private List<Consumer<TaskUpdate>> finishCallbacks;

	/**
	 * Used to set the name of the task
	 *
	 * @param name
	 */
	protected Task(String name) {
		this.name = name;
	}

	/**
	 * Starts this task
	 *
	 * @return A thread for the task
	 */
	public Thread runTask () {
		return new Thread(this);
	}

	protected void updateProgress(
		int progress,
		String text
	) {
		this.progress = progress;

		TaskUpdate status = new TaskUpdate(
				TaskStatus.RUNNING,
				this.name,
				text,
				progress
		);

		int size = this.progressCallbacks.size();

		for (int i = 0; i < size; i++) {
			this.progressCallbacks.get(i).accept(status);
		}
	}

	/**
	 * Gets the progress
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}
}
