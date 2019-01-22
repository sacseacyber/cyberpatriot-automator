package client.tasks;

import client.Util;
import client.readme.README;
import client.tasks.taskannotations.LinuxTask;
import client.tasks.taskannotations.WindowsTask;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a task that the program can do, e.g. enable the firewall
 *
 * To create a task, just extend this class and add either the LinuxTask annotation,
 * the WindowsTask annotation, or both
 * If it does not have an annotation it is assumed that the task is not meant for either Linux
 * or Windows and will not show up for either
 *
 * This new task then has to have two methods, a constructor and a `public void run()` method
 *
 * The constructor has to call the superclass constructor with a String representing the name
 * of the task
 *
 * To register the task, go to cpautomator.Entry.initializeTasks and construct your task
 *
 * The `public void run()` task can do whatever AS LONG AS IT CALLS
 * `this.finishTask(String desc, boolean success)`
 *
 * IT IS VERY IMPORTANT THAT IT CALLS THIS FUNCTION, AS OTHERWISE THE PROGRAM WILL NOT
 * PROCEED DUE TO WAITING FOR YOUR TASK TO FINISH
 */
public abstract class Task implements Runnable {
	/**
	 * A list of tasks that can be used
	 *
	 * To be added to by the Task constructor
	 */
	private static List<Task> TaskList = new ArrayList<>();
	/**
	 * Used to get the task list
	 */
	static List<Task> GetTaskList() {
		return new ArrayList<>(Task.TaskList);
	}

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
	private float progress = 0;
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
	 * The README information some tasks require
	 */
	private README readme;

	/**
	 * Used to set the name of the task
	 *
	 * @param name the name of the task
	 */
	protected Task(String name) {
		this.name = name;
		this.progressCallbacks = new ArrayList<>();
		this.finishCallbacks = new ArrayList<>();

		Class taskClass = this.getClass();
		Annotation[] annotations = taskClass.getAnnotations();

		for (Annotation annotation : annotations) {
			if (annotation instanceof LinuxTask && !((LinuxTask) annotation).disabled() && Util.isLinux()) {
				Task.TaskList.add(this);
			}
			if (annotation instanceof WindowsTask && !((WindowsTask) annotation).disabled() && Util.isWindows()) {
				Task.TaskList.add(this);
			}
		}
	}

	/**
	 * Gets the progress
	 *
	 * @return the progress
	 */
	final float getProgress() {
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
	 * Used by the actual tasks to get readme information
	 */
	protected README getReadme() {
		return this.readme;
	}

	/**
	 * Adds a callback for when the task is finished
	 *
	 * @param callback The callback for being finished
	 */
	final void addFinishCallback(Consumer<Task> callback) {
		this.finishCallbacks.add(callback);
	}

	/**
	 * Adds a callback for when the task decides to update its progress
	 *
	 * @param callback The callback containing the progress information
	 */
	final void addProgressCallback(Consumer<Task> callback) {
		this.progressCallbacks.add(callback);
	}

	/**
	 * Starts this task
	 *
	 * @return A thread for the task
	 */
	Thread runTask (README readmeInformation) {
		this.progress = 0;
		this.status = TaskStatus.RUNNING;
		this.readme = readmeInformation;

		return new Thread(this);
	}

	/**
	 * A method used by the task to signal an update in the progress
	 *
	 * @param progress the new progress, from 0 to 1
	 * @param text a message that can go with the progress update
	 */
	protected final void updateProgress(float progress, String text) {
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
	protected final void finishTask(String text, boolean success) {
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
