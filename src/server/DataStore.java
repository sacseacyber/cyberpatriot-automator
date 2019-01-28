package server;

import client.Util;
import lib.DataItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to add, remove, and get entries
 */
class DataStore {
	private File location;
	private List<DataItem> items = new ArrayList<>();

	DataStore(File location) {
		this.location = location;

		String fileText;

		if (!location.exists()) {
			System.out.println("Using empty data list");
			return;
		}

		try {
			fileText = Util.readFromFile(location);
		} catch(IOException ex) {
			ex.printStackTrace();

			System.out.println("Using empty data list");

			return;
		}

		DataItem[] items = DataItem.parse(fileText);
		for (DataItem item : items) {
			this.items.add(item);
		}
	}

	void addItem(DataItem item) {
		this.items.add(item);
	}

	void save() throws IOException {
		String fileText = this.serialize();

		Util.writeToFile(this.location, fileText);
	}

	String serialize() {
		StringBuilder fileText = new StringBuilder();

		for (int i = 0; i < this.items.size(); i++) {
			fileText.append(this.items.get(i).serialize());
			if (i != this.items.size() - 1) {
				// Null is used to separate the strings
				fileText.append((char)0x00);
			}
		}

		return fileText.toString();
	}
}
