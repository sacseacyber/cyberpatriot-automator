package client.scoringreport;

import client.Util;
import client.config.CPConfig;
import lib.DataItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoringReport {
	private class FoundScoredItem {
		private String description;
		private int score;

		FoundScoredItem(String description, int score) {
			this.description = description;
			this.score = score;
		}

		public String getDescription() {
			return description;
		}

		public int getScore() {
			return score;
		}
	}
	private static final String ScoringReportLocation = Util.isWindows()
			? "C:\\CyberPatriot\\ScoringReport.html"
			: "/opt/CyberPatriot/ScoringReport.html";

	private CPConfig config;
	private ServerConnection conn;

	private String id;
	private String imageType;
	private List<DataItem> scoredItems = new ArrayList<>();
	private int totalScore;
	private int totalItems;
	private int foundItems;

	private int start;

	ScoringReport(CPConfig config) {
		this.config = config;

		this.parse();

		this.conn = new ServerConnection(
				config.getRemoteDataServerHost(),
				config.getRemoteDataServerPort(),
				this.imageType
		);
	}

	private void parse() {
		try {
			String fileText = Util.readFromFile(new File(ScoringReport.ScoringReportLocation));

			String[] lines = fileText.split("\n");

			Pattern teamID = Pattern.compile("<h3 class=\"center\">Current Team ID: <span style=\"color:green\">(.*?)<\\/span><\\/h3>");
			Pattern imageInformation = Pattern.compile("<h1>(.*?)<\\/h1>");
			Pattern scoreCounts = Pattern.compile("<.*?>(\\d*) out of (\\d*) scored security issues fixed, for a gain of (\\d*) points");

			this.start = 0;

			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				Matcher teamIDResult = teamID.matcher(line);
				if (teamIDResult.matches()) {
					this.id = teamIDResult.group(1);
				}

				Matcher imageResult = imageInformation.matcher(line);
				if (imageResult.matches()) {
					this.imageType = imageResult.group(1);
				}

				Matcher scoreResults = scoreCounts.matcher(line);
				if (scoreResults.matches()) {
					try {
						this.foundItems = Integer.parseInt(scoreResults.group(1));
						this.totalItems = Integer.parseInt(scoreResults.group(2));
						this.totalScore = Integer.parseInt(scoreResults.group(3));
						start = i;
					} catch(Exception e) {
						// Ignore, as there might be a problem with CCS connection
						// In that case, the results won't exist and will be '???'
					}
				}
			}

			this.start += 2;
			int end;

			Pattern startOfHTML = Pattern.compile("^<");

			for (end = start; end < lines.length; end++) {
				if (startOfHTML.matcher(lines[end]).matches()) {
					break;
				}
			}

			Pattern startOfAlphaNumeric = Pattern.compile("^[A-Za-z0-9]");

			FoundScoredItem[] items = new FoundScoredItem[this.foundItems];
			int itemCount = 0;

			for (int i = start; i < end; i++) {
				if (startOfAlphaNumeric.matcher(lines[i]).matches()) {
					String cleanLine = lines[i].replaceAll("<\\/.*?>", "");
					String[] parts = cleanLine.split("-");

					int points = Integer.parseInt(parts[parts.length-1].trim());
					String[] description = Arrays.copyOfRange(parts, 0, parts.length - 1);
					StringBuilder fullDescription = new StringBuilder();

					for (int j = 0; j < parts.length; j++) {
						fullDescription.append(parts[j]);
						if (j != parts.length - 1) {
							fullDescription.append("-");
						}
					}

					items[itemCount++] = new FoundScoredItem(
							fullDescription.toString(),
							points
					);
				}
			}
		} catch(IOException ignore) {}
	}

	public int getFoundItems() {
		return foundItems;
	}

	public int getTotalItems() {
		return totalItems;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public List<DataItem> getScoredItems() {
		return scoredItems;
	}

	public String getId() {
		return id;
	}

	public String getImageType() {
		return imageType;
	}

	private void update() {
		try {
			String fileText = Util.readFromFile(new File(ScoringReport.ScoringReportLocation));

			String[] lines = fileText.split("\n");

			int end;

			Pattern startOfHTML = Pattern.compile("^<");

			for (end = start; end < lines.length; end++) {
				if (startOfHTML.matcher(lines[end]).matches()) {
					break;
				}
			}

			Pattern startOfAlphaNumeric = Pattern.compile("^[A-Za-z0-9]");

			FoundScoredItem[] items = new FoundScoredItem[this.foundItems];
			int itemCount = 0;

			for (int i = start; i < end; i++) {
				if (startOfAlphaNumeric.matcher(lines[i]).matches()) {
					String cleanLine = lines[i].replaceAll("<\\/.*?>", "");
					String[] parts = cleanLine.split("-");

					int points = Integer.parseInt(parts[parts.length - 1].trim());
					String[] description = Arrays.copyOfRange(parts, 0, parts.length - 1);
					StringBuilder fullDescription = new StringBuilder();

					for (int j = 0; j < description.length; j++) {
						fullDescription.append(description[j]);
						if (j != description.length - 1) {
							fullDescription.append("-");
						}
					}

					items[itemCount++] = new FoundScoredItem(
							fullDescription.toString(),
							points
					);
				}
			}
		} catch(Exception ignore) {}
	}
}
