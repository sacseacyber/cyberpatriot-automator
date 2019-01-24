package client;

import java.io.*;
import java.util.Scanner;

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
			// TODO: Implement a UAC interface for Windows
			// https://stackoverflow.com/questions/30082838/elevate-java-application-while-running
		} else {
			// We don't care about Linux users, they're most likely smart enough if they're assigned
			// to Linux to use sudo
			System.out.println("Run command with sudo dummy");
		}

		System.exit(0);
	}

	public static String readFromFile(File file) throws IOException {
		Scanner fileReader = new Scanner(file);

		StringBuilder results = new StringBuilder();

		while (fileReader.hasNext()) {
			results.append(fileReader.next());
		}

		fileReader.close();

		return results.toString();
	}

	public static void writeToFile(File file, String data) throws IOException {
		writeToFile(file, data.getBytes());
	}

	public static void writeToFile(File file, byte[] data) throws IOException {
		FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
		out.write(data);
		out.close();
	}
}
