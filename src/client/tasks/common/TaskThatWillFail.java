package client.tasks.common;

import client.tasks.Task;
import client.tasks.taskannotations.LinuxTask;
import client.tasks.taskannotations.WindowsTask;

/**
 * A test task that demonstrates the ability for the TaskRunner to display failures
 * that may occur
 */
@LinuxTask(disabled = true)
@WindowsTask(disabled = true)
public class TaskThatWillFail extends Task {
	public TaskThatWillFail () {
		super("Task that will fail");
	}

	public void run() {
		finishTask("Failed", false);
	}
}
