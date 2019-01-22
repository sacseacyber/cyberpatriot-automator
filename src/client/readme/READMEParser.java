package client.readme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class READMEParser {
	static README parse(String readmeText) {
		final Pattern authorizedUsersPattern = Pattern.compile("^<(span|b)>Authorized Users.*");
		final Pattern usernameLine = Pattern.compile("^[a-z].*");

		List<String> lines = toStringList(readmeText.split("\n"));

		int allowedUsersIndexStart;

		for (
				allowedUsersIndexStart = 0;
				allowedUsersIndexStart < lines.size();
				allowedUsersIndexStart++
		) {
			Matcher result = authorizedUsersPattern.matcher(lines.get(allowedUsersIndexStart));
			boolean matches = result.matches();
			if (matches) {
				break;
			}
		}

		final int allowedAdminsIndex = allowedUsersIndexStart;
		int allowedUsersIndex;

		for (
				allowedUsersIndex = allowedUsersIndexStart - 1;
				allowedUsersIndex > 0;
				allowedUsersIndex--
		) {
			String lineToTest = lines.get(allowedUsersIndex);
			if (lineToTest.contains("<")) {
				break;
			}
		}

		int allowedUsersIndexEnd;

		for (
				allowedUsersIndexEnd = allowedUsersIndexStart + 1;
				allowedUsersIndexEnd < lines.size();
				allowedUsersIndexEnd++
		) {
			String lineToTest = lines.get(allowedUsersIndexEnd);
			if (lineToTest.contains("<")) {
				break;
			}
		}

		List<String> validAdminUsers = lines
				.subList(allowedUsersIndex, allowedUsersIndexEnd)
				.stream()
				.filter(s -> usernameLine.matcher(s).matches())
				.map(s -> s.replace(" (you)", "").trim())
				.collect(Collectors.toList());

		List<String> validNames = lines
				.subList(allowedUsersIndex, allowedAdminsIndex)
				.stream()
				.filter(s -> usernameLine.matcher(s).matches())
				.map(s -> s.replace(" (you)", "").trim())
				.collect(Collectors.toList());

		return new README(
				toStringArray(validNames),
				toStringArray(validAdminUsers),
				new String[] {},
				""
		);
	}

	private static String[] toStringArray(List<String> list) {
		String[] newList = new String[list.size()];

		for (int i = 0; i < newList.length; i++) {
			newList[i] = list.get(i);
		}

		return newList;
	}

	private static List<String> toStringList(String[] list) {
		return new ArrayList<>(Arrays.asList(list));
	}
}
