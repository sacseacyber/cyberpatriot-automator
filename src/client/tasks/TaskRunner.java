package client.tasks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TaskRunner extends JPanel {
	/**
	 * A custom renderer for the Task objects
	 *
	 * Basically displays the text for the task, maybe a reason for why it failed (if applicable),
	 * and background colors based on if it is selected, has failed, or has succeeded
	 */
	private class TaskCellRenderer extends JLabel implements ListCellRenderer<Task> {
		@Override
		public Component getListCellRendererComponent(
				JList<? extends Task> jList,
				Task task,
				int i,
				boolean isSelected,
				boolean hasFocus
		) {
			this.setText(
					task.getStatus() == TaskStatus.FAILED
							? (task.name + ": " + task.getStatusMessage())
							: task.name
			);
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

	/**
	 * Fields to control UI elements
	 */
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

	/**
	 * Fields that control the tasks
	 */
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

	/**
	 * Initializes the UI components
	 *
	 * Fills the data in the different JLists
	 */
	public void createUIComponents() {
		this.availableTasks = Task.GetTaskList();

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

	/**
	 * Changes the labels to show the information for the specified task
	 *
	 * @param task The Task to display info for
	 */
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

	/**
	 * Changes the labels to show the information for the current task
	 */
	private void updateTaskInfo() {
		this.updateTaskInfo(this.currentTask);
	}

	/**
	 * Updates the JList components to match the internal Task lists
	 */
	private void updateDisplays() {
		this.availableTasksDisplay.setListData(this.taskArray(this.availableTasks));
		this.queuedTasksDisplay.setListData(this.taskArray(this.queuedTasks));
		this.finishedTasksDisplay.setListData(this.taskArray(this.finishedTasks));

		availableTasksDisplay.clearSelection();
		queuedTasksDisplay.clearSelection();
	}

	/**
	 * Updates the buttons to make them disabled when there are tasks running
	 */
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

	/**
	 * Adds the button listeners to the different buttons
	 */
	private void addQueueButtonListeners() {
		// Adds a function to handle queueing all the available tasks
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
		// Moves the queued tasks back to the available list
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
		// Takes the currently selected queued task and moves it to the available list
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
		// Takes the currently selected task and queues it
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
		// Takes queued tasks and runs them, freezing the UI
		runQueuedTasksButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				if (isNotFrozen()) {
					runTask();
				}
			}
		});
	}

	/**
	 * Simple callbacks to ensure there is only one element selected at a time
	 */
	private void addSelectionListeners() {
		availableTasksDisplay.addListSelectionListener(listSelectionEvent -> queuedTasksDisplay.clearSelection());
		queuedTasksDisplay.addListSelectionListener(listSelectionEvent -> availableTasksDisplay.clearSelection());
	}

	/**
	 * A function that will "recursively/iteratively" execute each task, freezing the UI
	 *
	 * After all tasks are executed the UI is unfrozen
	 */
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
