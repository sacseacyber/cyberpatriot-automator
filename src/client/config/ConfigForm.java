package client.config;

import javax.swing.*;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConfigForm extends JComponent {
	private JPanel panel1;
	private JTextField ipField;
	private JTextField portField;
	private JButton saveButton;

	private CPConfig config;

	public ConfigForm (CPConfig config) {
		this.config = config;

		this.ipField.setText(config.getRemoteDataServerHost());
		this.portField.setText(String.valueOf(config.getRemoteDataServerPort()));

		this.ipField.addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
				try {
					// Check if there are 4 parts
					String[] parts = ipField.getText().split(".");
					if (parts.length != 4) {
						throw new Exception();
					}

					int numToCheck;

					// Check that each part is an integer
					numToCheck = Integer.parseInt(parts[0]);
					if (numToCheck < 0 || numToCheck > 255) {
						throw new Exception();
					}

					numToCheck = Integer.parseInt(parts[1]);
					if (numToCheck < 0 || numToCheck > 255) {
						throw new Exception();
					}

					numToCheck = Integer.parseInt(parts[2]);
					if (numToCheck < 0 || numToCheck > 255) {
						throw new Exception();
					}

					numToCheck = Integer.parseInt(parts[3]);
					if (numToCheck < 0 || numToCheck > 255) {
						throw new Exception();
					}
				} catch(Exception ignore) {
					return;
				}

				config.setRemoteDataServerHost(ipField.getText());
			}

			@Override
			public void caretPositionChanged(InputMethodEvent inputMethodEvent) {}
		});

		this.portField.addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
				int port;

				try {
					port = Integer.parseInt(portField.getText());
				} catch(Exception ignore) {
					return;
				}

				config.setRemoteDataServerPort(port);
			}

			@Override
			public void caretPositionChanged(InputMethodEvent inputMethodEvent) {}
		});

		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);

				config.saveToFile();
			}
		});
	}
}
