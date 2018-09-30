package com.everis.fallas.operacionales.workitem.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.everis.fallas.operacionales.workitem.framework.WorkItemCommandLineException;

public class SimpleDateFormatUtil {

	public static final String DURATION_HOURS = "hours";
	public static final String DURATION_MINUTES = "minutes";
	public static final String SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z = "yyyy/MM/dd hh:mm:ss z";
	public static final String SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD = "yyyy/MM/dd";

	public static Timestamp createTimeStamp(String aDate) {
		return createTimeStamp(aDate, null);
	}

	public static Timestamp createTimeStamp(String aDate,
			String timeFormatPattern) {
		if (null == timeFormatPattern) {
			timeFormatPattern = SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z;
		}
		SimpleDateFormat sDFormat = new SimpleDateFormat(timeFormatPattern);
		try {
			Date date = sDFormat.parse(aDate);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			throw new WorkItemCommandLineException("Parse Exception! Input: "
					+ aDate + " Parsing pattern: " + timeFormatPattern, e);
		}
	}

	public static boolean sameDay(Timestamp date1, Timestamp date2) {
		SimpleDateFormat sDFormat = new SimpleDateFormat(
				SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD);
		return sDFormat.format(date1).equals(sDFormat.format(date2));
	}

	public static String getDate(Timestamp date, String timeFormatPattern) {
		if (null == timeFormatPattern) {
			timeFormatPattern = SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z;
		}
		SimpleDateFormat sDFormat = new SimpleDateFormat(timeFormatPattern);
		return sDFormat.format(date);
	}

	public static Long convertDurationToMiliseconds(String duration) {
		long time = 0;
		if (duration == null) {
			return time;
		}
		int hoursIndex = duration.indexOf(DURATION_HOURS);
		int minsIndex = duration.indexOf(DURATION_MINUTES);
		if (hoursIndex < 0 && minsIndex < 0) {
			return new Long(duration);
		}
		if (hoursIndex > 0) {
			String hours = duration.substring(0, hoursIndex - 1).trim();
			time += TimeUnit.HOURS.toMillis(new Long(hours));
		}
		if (minsIndex > 0) {
			int start = 0;
			if (hoursIndex>0){
				start = hoursIndex + DURATION_HOURS.length();
			}
			String minutes = duration.substring(
					start,
					duration.length() - DURATION_MINUTES.length()).trim();
			time += TimeUnit.MINUTES.toMillis(new Long(minutes));
		}
		return time;
	}

	public static String convertToTimeSpent(Long milliseconds) {

		long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
		long minutesleft = milliseconds - TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toDays(minutesleft);
		String result = (hours > 0) ? hours + " " + DURATION_HOURS + " " : "";
		result += (minutes > 0) ? minutes + " " + DURATION_MINUTES : "";
		return result.trim();
	}

	public static String obtenerIdTransaccion() {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssS");
		String idTransaccion = df.format(new Date()).toString();
		return idTransaccion;
	}
}
