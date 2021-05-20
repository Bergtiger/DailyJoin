package de.bergtiger.dailyjoin.utils;

import de.bergtiger.dailyjoin.DailyJoin;
import de.bergtiger.dailyjoin.utils.lang.Lang;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.logging.Level;

public class TimeUtils {

	/**
	 * checks if two calendars represent the same day ignoring time.
	 * @param cal1 the first {@link Calendar}
	 * @param cal2 the second {@link Calendar}
	 * @return true if they represent the same day
	 */
	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if(cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
				cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * checks if two Timestamps represent the same day ignoring time.
	 * @param t1 the first {@link Timestamp}
	 * @param t2 the second {@link Timestamp}
	 * @return true if they represent the same day
	 */
	public static boolean isSameDay(Timestamp t1, Timestamp t2) {
		// get instances
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		// set time
		cal1.setTimeInMillis(t1.getTime());
		cal2.setTimeInMillis(t2.getTime());
		// check same day
		return isSameDay(cal1, cal2);
	}
	
	/**
	 * checks if a calendar date is today.
	 * @param cal the {@link Calendar}
	 * @return true if cal date is today
	 */
	public static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	/**
	 * checks if a Timestamp is today.
	 * @param t the {@link Timestamp} to check
	 * @return true if {@link Timestamp} date is today
	 */
	public static boolean isToday(Timestamp t) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(t.getTime());
		return isToday(cal);
	}

	/**
	 * checks if a Timestamp is yesterday.
	 * @param t the {@link Timestamp} to check
	 * @return true if {@link Timestamp} date is yesterday
	 */
	public static boolean isYesterday(Timestamp t) {
		return isDaysBetween(t, new Timestamp(System.currentTimeMillis()), 1);
	}

	/**
	 * checks if x days are between the calendar dates.
	 * @param cal1 first {@link Calendar} date (days will be added)
	 * @param cal2 second {@link Calendar} date
	 * @param days days between the dates
	 * @return true if exact x days are between the dates
	 */
	public static boolean isDaysBetween(Calendar cal1, Calendar cal2, int days) {
		cal1.add(Calendar.DAY_OF_YEAR, days);
		return isSameDay(cal1, cal2);
	}

	/**
	 * checks if x days are between the timestamps.
	 * @param t1 first {@link Timestamp}
	 * @param t2 second {@link Timestamp}
	 * @param days days between the timestamps.
	 * @return true if exact x days are between the timestamps
	 */
	public static boolean isDaysBetween(Timestamp t1, Timestamp t2, int days) {
		// get instances
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		// set time
		cal1.setTimeInMillis(t1.getTime());
		cal2.setTimeInMillis(t2.getTime());
		// check days between
		return isDaysBetween(cal1, cal2, days);
	}

	/**
	 * returns the given calendar date with time values cleared.
	 * @param cal {@link Calendar} date
	 * @return date without time
	 */
	public static Calendar clearTime(Calendar cal) {
		if(cal != null) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
		}
		return cal;
	}

	/**
	 * Timestamp as String formatted with default time format.
	 * @param t {@link Timestamp} to format
	 * @return String
	 */
	public static String formatted(Timestamp t) {
		return formatted(t, Lang.FORMAT_TIME.get());
	}

	/**
	 * Timestamp as String formatted with given time format.
	 * @param t {@link Timestamp} to format
	 * @param format time format as String
	 * @return String
	 */
	public static String formatted(Timestamp t, String format) {
		if(t != null) {
			try {
				return DateTimeFormatter.ofPattern(format).format(t.toLocalDateTime());
			} catch(Exception e) {
				DailyJoin.getDailyLogger().log(Level.SEVERE, String.format("formated: could not format Timestamp(%s) with format(%s)", t, format),e);
			}
		} else {
			DailyJoin.getDailyLogger().log(Level.WARNING, "formated: Timestamp null");
		}
		return "-";
	}
}
