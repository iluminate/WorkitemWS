package com.everis.fallas.operacionales.workitem.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringUtil {

											public static List<String> splitStringToList(String value,
			String itemSeparator) {
		return Arrays.asList(value.split(itemSeparator));
	}

									public static String listToString(List<String> resultList, String seperator) {
		String result = "";
		for (int i = 0; i < resultList.size(); i++) {
			if (i > 0) {
				result += seperator;
			}
			result += resultList.get(i);
		}
		return result;
	}

										public static boolean hasPrefix(String value, String prefix) {
		return value.startsWith(prefix);
	}

											public static String removePrefix(String value, String prefix) {
		return value.substring(prefix.length());
	}

								public static List<String> removePrefixes(List<String> values,
			String prefixExistingworkitem) {
		List<String> newValues = new ArrayList<String>(values.size());
		for (String value : values) {
			String newValue = value;
			if (value
					.startsWith("#")) {
				newValue = StringUtil.removePrefix(value,
						"#");
			}
			newValues.add(newValue);
		}
		return newValues;
	}
}
