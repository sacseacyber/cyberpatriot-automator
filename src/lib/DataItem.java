package lib;

enum DataItemState {
	ADDED,
	REMOVED
}

public class DataItem {
	private String description;
	private DataItemState state;
	private int score;
	private long timestamp;

	public static int calculateTotal(DataItem[] items) {
		int score = 0;

		for (DataItem item : items) {
			score = item.getState() == DataItemState.ADDED
					? score + item.getScore()
					: score - item.getScore();
		}

		return score;
	}

	public static DataItem[] parse(String fileText) {
		String[] splitItems = fileText.split(new String(new char[] { 0 }));
		DataItem[] items = new DataItem[splitItems.length];

		for (int i = 0; i < splitItems.length; i++) {
			items[i] = parseDataItemText(splitItems[i]);
		}

		return items;
	}

	public static DataItem parseDataItemText(String text) {
		// Basic data structure is this:
		// 1 byte for score
		// 1 byte for state: 0 for added, 1 for removed
		// 8 bytes for UNIX timestamp of when the item was scored
		// Rest is for description, with the expectation there is no 0x00

		int score = text.charAt(0);
		DataItemState state = text.charAt(1) == 0 ? DataItemState.ADDED : DataItemState.REMOVED;
		long timestamp = (
				(long)text.charAt(2) << 56 |
				(long)text.charAt(3) << 48 |
				(long)text.charAt(4) << 40 |
				(long)text.charAt(5) << 32 |
				(long)text.charAt(6) << 24 |
				(long)text.charAt(7) << 16 |
				(long)text.charAt(8) <<  8 |
				(long)text.charAt(9)
		);
		String description = text.substring(10);

		return new DataItem(description, state, score, timestamp);
	}

	public DataItem(String description, DataItemState state, int score, long timestamp) {
		this.description = description;
		this.state = state;
		this.score = score;
		this.timestamp = timestamp;
	}

	public int getScore() {
		return score;
	}

	public String getDescription() {
		return description;
	}

	public DataItemState getState() {
		return state;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	public String serialize() {
		StringBuilder result = new StringBuilder();
		
		result.append((char)this.getScore());
		result.append(this.getState() == DataItemState.ADDED ? (char)1 : (char)2);
		result.append(new char[] {
				(char)(this.getTimestamp() >> 56 & 0xFF),
				(char)(this.getTimestamp() >> 48 & 0xFF),
				(char)(this.getTimestamp() >> 40 & 0xFF),
				(char)(this.getTimestamp() >> 32 & 0xFF),
				(char)(this.getTimestamp() >> 24 & 0xFF),
				(char)(this.getTimestamp() >> 16 & 0xFF),
				(char)(this.getTimestamp() >>  8 & 0xFF),
				(char)(this.getTimestamp()       & 0xFF)
		});
		result.append(this.getDescription());

		return result.toString();
	}
}
