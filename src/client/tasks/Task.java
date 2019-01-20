package client.tasks;

import java.util.ArrayList;
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
	 * @param name the name of the task
	 */
	Task(String name) {
		this.name = name;
		this.progressCallbacks = new ArrayList<>();
		this.finishCallbacks = new ArrayList<>();
	}

	/**
	 * Gets the progress
	 *
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Adds a callback for when the task is finished
	 *
	 * @param callback The callback for being finished
	 */
	public void addFinishCallback(Consumer<TaskUpdate> callback) {
		this.finishCallbacks.add(callback);
	}

	/**
	 * Adds a callback for when the task decides to update its progress
	 *
	 * @param callback The callback containing the progress information
	 */
	public void addProgressCallback(Consumer<TaskUpdate> callback) {
		this.progressCallbacks.add(callback);
	}

	/**
	 * Starts this task
	 *
	 * @return A thread for the task
	 */
	public Thread runTask () {
		return new Thread(this);
	}

	/**
	 * A method used by the task to signal an update in the progress
	 *
	 * @param progress the new progress, from 0 to 1
	 * @param text a message that can go with the progress update
	 */
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

		for (Consumer<TaskUpdate> callback : this.progressCallbacks) {
			callback.accept(status);
		}
	}

	/**
	 * A method used by the task to signal completion
	 *
	 * @param text Completion message
	 * @param success true if the task succeeded, false for failure
	 */
	protected void finishTask(
			String text,
			boolean success
	) {
		// We're done
		this.progress = 1;

		TaskUpdate status = new TaskUpdate(
				success ? TaskStatus.SUCCEEDED : TaskStatus.FAILED,
				this.name,
				text,
				1
		);

		for (Consumer<TaskUpdate> callback : this.finishCallbacks) {
			callback.accept(status);
		}
	}
}
