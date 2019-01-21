package client.tasks;

import client.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TaskRunner extends JPanel {

	private class TaskCellRenderer extends JLabel implements ListCellRenderer<Task> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Task> jList, Task task, int i, boolean b, boolean b1) {
			this.setText(task.name);

			switch (task.getStatus()) {
				case FAILED:
					setBackground(Color.RED);
					break;

				case SUCCEEDED:
					setBackground(Color.GREEN);
					break;

				default:
					setBackground(Color.WHITE);
			}

			return this;
		}
	}

	private JPanel taskPanel;
	private JPanel taskStatusPanel;
	private JProgressBar taskProgress;
	private JPanel taskTextPanel;
	private JLabel taskStatus;
	private JLabel taskName;
	private JButton runQueuedTasksButton;
	private JList<Task> availableTasksDisplay;
	private JList<Task> queuedTasksDisplay;
	private JList<Task> finishedTasksDisplay;
	private JButton queueAllButton;
	private JButton queueSelectedButton1;
	private JButton dequeueSelectedButton;
	private JButton dequeueAllButton;

	private List<Task> availableTasks;
	private List<Task> queuedTasks;
	private List<Task> finishedTasks;

	private Task currentTask;

	public void createUIComponents() {
		if (Util.isWindows()) {
			this.availableTasks = new ArrayList<>();

			// Not all tasks work across OSes like EnableFirewall
			this.availableTasks.add(new EnableFirewall());
		} else {
			this.availableTasks = new ArrayList<>();

			this.availableTasks.add(new EnableFirewall());
		}

		this.queuedTasks = new ArrayList<>(this.availableTasks.size());
		this.finishedTasks = new ArrayList<>(this.availableTasks.size());

		this.availableTasksDisplay = new JList<>();
		this.queuedTasksDisplay = new JList<>();
		this.finishedTasksDisplay = new JList<>();

		this.availableTasksDisplay.setCellRenderer(new TaskCellRenderer());
		this.queuedTasksDisplay.setCellRenderer(new TaskCellRenderer());
		this.finishedTasksDisplay.setCellRenderer(new TaskCellRenderer());

		this.taskName = new JLabel();
		this.taskStatus = new JLabel();
		this.taskProgress = new JProgressBar();

		this.updateDisplays();
		this.updateTaskInfo();
	}

	private void updateTaskInfo() {
		if (this.currentTask != null) {
			this.taskName.setText(this.currentTask.name);
			this.taskStatus.setText(this.currentTask.getStatusMessage());
			this.taskProgress.setValue(this.currentTask.getProgress() * 100);
		} else {
			this.taskName.setText("No task");
			this.taskStatus.setText("No status");
			this.taskProgress.setValue(10);
		}
	}

	private void updateDisplays() {
		this.availableTasksDisplay.setListData(this.taskArray(this.availableTasks));
		this.queuedTasksDisplay.setListData(this.taskArray(this.queuedTasks));
		this.finishedTasksDisplay.setListData(this.taskArray(this.finishedTasks));
	}

	/**
	 * For some reason, (Task[])(...).toArray() fails
	 *
	 * @param taskList the list to convert
	 * @return an array of the list
	 */
	private Task[] taskArray(List<Task> taskList) {
		Task[] returnValue = new Task[taskList.size()];

		for (int i = 0; i < returnValue.length; i++) {
			returnValue[i] = taskList.get(i);
		}

		return returnValue;
	}
}
