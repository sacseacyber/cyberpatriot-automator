package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

public class Server implements Runnable {
	private File folderLocation;
	private int port;

	public Server(Path folderLocation, int port) {
		File dataPath = new File(folderLocation.toString());

		if (!dataPath.exists()) {
			dataPath.mkdir();
		}

		this.folderLocation = dataPath;
		this.port = port;

		this.run();
	}

	public void run() {
		// Will handle accepting connections and will delegate connections to Connection threads
		try {
			ServerSocket server = new ServerSocket(this.port);

			while (true) {
				Socket client = server.accept();

				new Connection(client, this.folderLocation.toString()).run();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}