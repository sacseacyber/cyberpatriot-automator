package client.tasks;

/**
 * A class to hold the information for the task
 */
class TaskUpdate {
	private TaskStatus status;
	private String name;
	private String statusText;
	private int progress;

	TaskUpdate(TaskStatus status, String name, String statusText, int progress) {
		this.status = status;
		this.name = name;
		this.statusText = statusText;
		this.progress = progress;
	}
}
