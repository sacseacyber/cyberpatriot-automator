import client.Main;
import client.Util;
import server.Server;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Used as a single entry point for multiple programs, as there needs to be a server to handle the client
 *
 * The server just stores information, acting almost like an NFS or Samba server with Git
 */
public class Entry {
	private enum RunMode {
		SERVER,
		CLIENT
	}

	/**
	 * Runs either the server or the client
	 *
	 * @param args Command line arguments, including the flag for the server or client
	 */
	public static void main (String args[]) {
		// Figures out what it should run
		RunMode mode;
		if (args.length == 0) {
			mode = RunMode.CLIENT;
		} else if (args[0].equals("client")) {
			mode = RunMode.CLIENT;
		} else if (args[0].equals("server")) {
			mode = RunMode.SERVER;
		} else {
			mode = RunMode.CLIENT;
		}

		// Start the client
		if (mode == RunMode.CLIENT) {
			if (!Util.isAdministrator()) {
				// If windows, use this
				// https://stackoverflow.com/questions/30082838/elevate-java-application-while-running

				Util.elevate();
			}

			// It won't get here unless admin user
			Main mainWindow = new Main();
		}
		// Start the server
		else {
			Path dataPath = Paths.get("user.home");
			Path location = Paths.get(dataPath.toString(), "data");

			int port = 0xCCCC;

			if (args.length == 2) {
				location = Paths.get(args[1]);
			}

			Server server = new Server(location, port);
		}
	}
}
