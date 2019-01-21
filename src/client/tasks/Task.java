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
	final String name;
	/**
	 * For executing the task, also for using as a separate thread
	 */
	@Override
	public abstract void run();

	/**
	 * The current progress of the task
	 */
	private int progress = 0;
	/**
	 * The current status of the task
	 */
	private TaskStatus status = TaskStatus.NOTSTARTED;
	/**
	 * A status message for the task
	 */
	private String statusMessage = "";
	/**
	 * A list of callbacks for while the task is progressing, e.g. to update a progress bar
	 */
	private List<Consumer<Task>> progressCallbacks;
	/**
	 * A list of callbacks for when the task is done
	 */
	private List<Consumer<Task>> finishCallbacks;

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
	int getProgress() {
		return progress;
	}

	/**
	 * Gets the current task status
	 *
	 * @return the status
	 */
	TaskStatus getStatus() {
		return status;
	}

	/**
	 * Gets the current task message
	 *
	 * @return the task message
	 */
	String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * Adds a callback for when the task is finished
	 *
	 * @param callback The callback for being finished
	 */
	public void addFinishCallback(Consumer<Task> callback) {
		this.finishCallbacks.add(callback);
	}

	/**
	 * Adds a callback for when the task decides to update its progress
	 *
	 * @param callback The callback containing the progress information
	 */
	public void addProgressCallback(Consumer<Task> callback) {
		this.progressCallbacks.add(callback);
	}

	/**
	 * Starts this task
	 *
	 * @return A thread for the task
	 */
	public Thread runTask () {
		this.progress = 0;
		this.status = TaskStatus.RUNNING;

		return new Thread(this);
	}

	/**
	 * A method used by the task to signal an update in the progress
	 *
	 * @param progress the new progress, from 0 to 1
	 * @param text a message that can go with the progress update
	 */
	protected void updateProgress(int progress,	String text) {
		if (this.status == TaskStatus.SUCCEEDED || this.status == TaskStatus.FAILED) {
			return;
		}

		this.progress = progress;
		this.statusMessage = text;

		for (Consumer<Task> callback : this.progressCallbacks) {
			callback.accept(this);
		}
	}

	/**
	 * A method used by the task to signal completion
	 *
	 * @param text Completion message
	 * @param success true if the task succeeded, false for failure
	 */
	protected void finishTask(String text, boolean success) {
		if (this.status == TaskStatus.SUCCEEDED || this.status == TaskStatus.FAILED) {
			return;
		}

		// We're done
		this.progress = 1;
		this.status = success ? TaskStatus.SUCCEEDED : TaskStatus.FAILED;
		this.statusMessage = text;

		for (Consumer<Task> callback : this.finishCallbacks) {
			callback.accept(this);
		}
	}
}
