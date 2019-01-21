package client.tasks;

import client.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TaskRunner extends JPanel {

	private class TaskCellRenderer extends JLabel implements ListCellRenderer<Task> {
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Task> jList,
				Task task,
				int i,
				boolean isSelected,
				boolean hasFocus
		) {
			this.setText(task.name);
			this.setOpaque(true);

			switch (task.getStatus()) {
				case FAILED:
					this.setBackground(Color.RED);
					break;

				case SUCCEEDED:
					this.setBackground(Color.GREEN);
					break;

				default:
					this.setBackground((isSelected || hasFocus) ? Color.BLUE : Color.WHITE);
					break;
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
	private JButton queueSelectedButton;
	private JButton dequeueSelectedButton;
	private JButton dequeueAllButton;

	private List<Task> availableTasks;
	private List<Task> queuedTasks;
	private List<Task> finishedTasks;

	private Task currentTask;

	/**
	 * Whether or not the task runner is running tasks and not open to user input
	 *
	 * @return whether or not the GUI should be frozen
	 */
	private boolean isNotFrozen() {
		return this.currentTask == null;
	}

	public TaskRunner() {
		this.addQueueButtonListeners();
		this.addSelectionListeners();
	}

	public void createUIComponents() {
		if (Util.isWindows()) {
			this.availableTasks = new ArrayList<>();

			// Not all tasks work across OSes like EnableFirewall
			this.availableTasks.add(new EnableFirewall());
			this.availableTasks.add(new FiveSecondTimer());
			this.availableTasks.add(new TaskThatWillFail());
		} else {
			this.availableTasks = new ArrayList<>();

			this.availableTasks.add(new EnableFirewall());
			this.availableTasks.add(new FiveSecondTimer());
			this.availableTasks.add(new TaskThatWillFail());
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

	private void updateTaskInfo(Task task) {
		if (task != null) {
			this.taskName.setText(task.name);
			this.taskStatus.setText(task.getStatusMessage());
			this.taskProgress.setValue((int)(task.getProgress() * 100));
		} else {
			this.taskName.setText("");
			this.taskStatus.setText("");
			this.taskProgress.setValue(0);
		}
	}

	private void updateTaskInfo() {
		this.updateTaskInfo(this.currentTask);
	}

	private void updateDisplays() {
		this.availableTasksDisplay.setListData(this.taskArray(this.availableTasks));
		this.queuedTasksDisplay.setListData(this.taskArray(this.queuedTasks));
		this.finishedTasksDisplay.setListData(this.taskArray(this.finishedTasks));

		availableTasksDisplay.clearSelection();
		queuedTasksDisplay.clearSelection();
	}

	private void updateButtons() {
		this.dequeueSelectedButton.setEnabled(this.isNotFrozen());
		this.queueSelectedButton.setEnabled(this.isNotFrozen());
		this.dequeueAllButton.setEnabled(this.isNotFrozen());
		this.queueAllButton.setEnabled(this.isNotFrozen());
		this.runQueuedTasksButton.setEnabled(this.isNotFrozen());
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

	private void addQueueButtonListeners() {
		queueAllButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					for (int i = availableTasks.size() - 1; i >= 0; i--) {
						queuedTasks.add(availableTasks.remove(i));
					}

					updateDisplays();
				}
			}
		});
		dequeueAllButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					for (int i = queuedTasks.size() - 1; i >= 0; i--) {
						availableTasks.add(queuedTasks.remove(i));
					}

					updateDisplays();
				}
			}
		});
		queueSelectedButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					int index = availableTasksDisplay.getSelectedIndex();
					if (index != -1) {
						queuedTasks.add(availableTasks.remove(index));
					}

					updateDisplays();
				}
			}
		});
		dequeueSelectedButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					int index = queuedTasksDisplay.getSelectedIndex();
					if (index != -1) {
						availableTasks.add(queuedTasks.remove(index));
					}

					updateDisplays();
				}
			}
		});
		runQueuedTasksButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					runTask();
				}
			}
		});
	}

	private void addSelectionListeners() {
		availableTasksDisplay.addListSelectionListener(listSelectionEvent -> queuedTasksDisplay.clearSelection());
		queuedTasksDisplay.addListSelectionListener(listSelectionEvent -> availableTasksDisplay.clearSelection());
	}

	private void runTask() {
		// By some extension, the exit condition of a recursive function
		// This function starts a task -> task finishes -> calls onTaskFinish -> calls this function
		if (this.queuedTasks.size() > 0) {
			this.currentTask = this.queuedTasks.remove(0);

			this.currentTask.addFinishCallback(this::onTaskFinish);
			this.currentTask.addProgressCallback(this::onTaskUpdate);

			this.currentTask.runTask().run();
		} else {
			// If the current task is nothing, the task runner is doing nothing
			// Therefore, re-enable queue operations
			this.currentTask = null;
		}

		this.updateButtons();
	}

	/**
	 * Updates the task information
	 *
	 * @param task the task that has the information to update
	 */
	private void onTaskUpdate(Task task) {
		// Just call the function as task points towards this.currentTask
		this.updateTaskInfo(task);
	}

	/**
	 * Handles when a task is finished
	 *
	 * @param task the task to move to the finished stack
	 */
	private void onTaskFinish(Task task) {
		this.finishedTasks.add(task);

		this.updateTaskInfo();
		this.updateDisplays();

		this.runTask();
	}
}
