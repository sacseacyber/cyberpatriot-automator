package client.tasks;

public class TaskThatWillFail extends Task {
	public TaskThatWillFail () {
		super("Task that will fail");
	}

	public void run() {
		finishTask("Failed", false);
	}
}
