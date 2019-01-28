package server;

import lib.DataItem;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

/**
 * A client-server connection
 */
public class Connection implements Runnable {
	private Socket sock;
	private String dataDirectory;

	public Connection(Socket sock, String dataDirectory) {
		this.sock = sock;
		this.dataDirectory = dataDirectory;

		this.run();
	}

	public void run() {
		new Thread(() -> {
			try {
				PrintWriter out = new PrintWriter(this.sock.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));

				String inputLine;

				DataStore store = null;
				boolean sentDataStore = false;

				while ((inputLine = in.readLine()) != null) {
					if (!sentDataStore) {
						File path = new File(Paths.get(this.dataDirectory, inputLine).toString());

						store = new DataStore(path);
						out.write(store.serialize());

						// Double null signifies the end of the data
						out.write(new char[] {
								(char)0, (char)0
						});

						sentDataStore = true;
					} else {
						DataItem newDataItem = DataItem.parseDataItemText(inputLine);

						try {
							store.addItem(newDataItem);
							store.save();
						} catch(NullPointerException ignore) {}
					}
				}
			} catch(IOException e) {
				e.printStackTrace();
				System.out.println("There was an issue when communicating with a client");
				try {
					this.sock.close();
				} catch (IOException ignore) {
					System.out.println("Additionally, there was a problem when trying to gracefully close the connection");
				}
			}
		}).run();
	}
}
