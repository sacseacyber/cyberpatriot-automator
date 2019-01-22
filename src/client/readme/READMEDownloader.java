package client.readme;

import client.Util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

class READMEDownloader {
	private String url;

	READMEDownloader() throws FileNotFoundException {
		if (Util.isWindows()) {
			// TODO: Get Windows README url
			// Maybe this may help?
			// https://stackoverflow.com/questions/309495/windows-shortcut-lnk-parser-in-java
		} else {
			// TODO: Find a way to get the user running the process read the correct file
			File readmeShortcut = new File("/home/arioux/Desktop/README.desktop");
//			File readmeShortcut = new File("~/Desktop/README.desktop");

			Scanner readmeShortcutScanner = new Scanner(readmeShortcut);

			while (readmeShortcutScanner.hasNextLine()) {
				String line = readmeShortcutScanner.nextLine();

				if (line.startsWith("Exec=x-www-browser")) {
					StringBuilder newUrl = new StringBuilder();
					boolean open = false;

					// 17 is the length of the string "Exec=x-www-browser"
					for (int i = 17; i < line.length(); i++) {
						char nextChar = line.charAt(i);

						if (nextChar == '"') {
							if (open) {
								break;
							}
							open = true;
						} else if (open) {
							newUrl.append(nextChar);
						}
					}

					this.url = newUrl.toString();
				}
			}
		}
	}

	String downloadReadmeHTML() throws IOException {
		StringBuilder result = new StringBuilder();
		URL readmeURL = new URL(this.url);
		HttpURLConnection connection = (HttpURLConnection)readmeURL.openConnection();
		connection.setRequestMethod("GET");

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
			result.append('\n');
		}
		reader.close();

		return result.toString();
	}
}
