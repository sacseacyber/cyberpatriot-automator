package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A cross platform collection of utilities
 */
public class Util {
	/**
	 * Checks whether or not the host computer is a Windows computer
	 *
	 * @return Whether or not the computer is Windows
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}

	/**
	 * Checks if the OS is Windows, which it checks by seeing if it is not Windows
	 *
	 * @return Whether or not the computer is not Windows
	 */
	public static boolean isLinux() {
		return !Util.isWindows();
	}

	/**
	 * Checks if the user is an administrator
	 *
	 * @return if the user is an administrator
	 */
	public static boolean isAdministrator() {
		if (Util.isLinux()) {
			try {
				Process proc = Runtime.getRuntime().exec("whoami");

				proc.waitFor();

				InputStream stdout = proc.getInputStream();

				BufferedReader results = new BufferedReader(new InputStreamReader(stdout));

				String result = "", out;

				while ((out = results.readLine()) != null) {
					result += out;
				}

				return result.equals("root");
			} catch (InterruptedException | IOException e) {
				return false;
			}
		} else {
			// TODO: Implement for Windows
			return false;
		}
	}

	public static void elevate() {
		if (Util.isWindows()) {
			// https://stackoverflow.com/questions/30082838/elevate-java-application-while-running
		} else {
			System.out.println("Run command with sudo dummy");
		}

		System.exit(0);
	}
}
