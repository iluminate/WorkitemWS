/*******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 20015. All Rights Reserved. 
 *
 * Note to U.S. Government Users Restricted Rights:  Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/
package com.everis.fallas.operacionales.workitem.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to collect the string related methods needed across the
 * commands
 * 
 */
public class StringUtil {

	/**
	 * Utility method that splits a string into a list of strings using a
	 * specific separator string
	 * 
	 * @param value
	 *            - the original string
	 * @param itemSeparator
	 *            - the separator to be used
	 * @return a list of substrings
	 */
	public static List<String> splitStringToList(String value,
			String itemSeparator) {
		return Arrays.asList(value.split(itemSeparator));
	}

	/**
	 * Takes a list of strings and creates a string that has all the elements of
	 * the list separated by the given separator.
	 * 
	 * @param resultList
	 * @param seperator
	 * @return
	 */
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

	/**
	 * Utility method that checks if a string has a specific prefix
	 * 
	 * @param value
	 *            - the string to check
	 * @param prefix
	 *            - the prefix to look for
	 * @return true if the string has the given prefix, false otherwise
	 */
	public static boolean hasPrefix(String value, String prefix) {
		return value.startsWith(prefix);
	}

	/**
	 * Utility method that removes a prefix from a given string
	 * 
	 * @param value
	 *            - the string
	 * @param prefix
	 *            - the prefix to remove
	 * @return the string with the prefix removed. The original string if the
	 *         prefix was not found.
	 */
	public static String removePrefix(String value, String prefix) {
		return value.substring(prefix.length());
	}

	/**
	 * Removes Prefixes from strings in a list and return the list
	 * 
	 * @param values
	 * @param prefixExistingworkitem
	 * @return
	 */
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
