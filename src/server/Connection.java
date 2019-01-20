package server;

import java.net.Socket;

/**
 * A client-server connection
 */
public class Connection implements Runnable {
	private Socket sock;

	public Connection(Socket sock) {
		this.sock = sock;
	}

	public void run() {

	}
}
