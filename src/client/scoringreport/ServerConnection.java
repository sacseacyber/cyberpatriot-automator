package client.scoringreport;

import lib.DataItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Runnable {
	private Socket socket;
	private StringBuilder receivedData = new StringBuilder();

	private PrintWriter out;
	private BufferedReader in;

	ServerConnection(String host, int port, String image) {
		try {
			this.socket = new Socket(host, port);

			this.out = new PrintWriter(this.socket.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

			this.out.write(image);

			this.run();
		} catch(IOException ignore) {}
	}

	public String getReceivedData() {
		return receivedData.toString();
	}

	public void sendDataItem(DataItem item) {
		this.write(item.serialize());
	}

	private void write (String data) {
		this.out.write(data);
	}

	public void run() {
		String input = "";

		do {
			try {
				input = in.readLine();

				if (
					input.charAt(input.length() - 2) == (char)0 &&
					input.charAt(input.length() - 1) == (char)0
				) {
					char[] serverResult = new char[input.length() - 2];
					input.getChars(0, input.length() - 2, serverResult, 0);
					this.receivedData.append(serverResult);

					input = null;
				} else {
					this.receivedData.append(input);
				}
			} catch(IOException ignore) {}
		} while(input != null);
	}
}
