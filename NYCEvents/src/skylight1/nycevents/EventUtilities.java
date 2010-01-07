package skylight1.nycevents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class OneDayRange { // Java needs tuples
	Date start;
	Date end;
}

public class EventUtilities {

	public static final long ONE_DAY = 1000 * 60 * 60 * 24 - 60000; // actually one minute less
	public static final long ONE_HOUR = 1000 * 60 * 60;

	// This takes advantage of the fact that the park events description is a snippet with HTML mark-up.
	// We make some accommodation for music and art events because they don't have it. I've noticed
	// that here and there some park events have illegal mark-up. Normal browsers handle it with no
	// problem, but on the emulator it barfs and gives a message that it can't load.
	public static String generateHTML(EventData e) {
		String html = "<html><head/><body><a href=\"" + e.getWebsite();
		html += "\">" + e.getTitle();
		html += "</a><br/>";
		if(! e.getCategory().equals(Constants.PARKS)) { // parks has dates, phone and location duplicated in the description (usually)
			html += "<p>" + displayDateRange(e) + "</p>";
		}
		if(e.getDescription()!=null) {
			html += e.getDescription();
		}
		if(! e.getCategory().equals(Constants.PARKS)) {
			if(e.getTelephone() != null && e.getTelephone().length() != 0)
				html += "<p>Contact phone: " + e.getTelephone() + "</p>";
			if(e.getLocation2() != null && e.getLocation2().length() != 0) // I think location2 has better data
				html += "<p> Location: " + e.getLocation2() + "</p>";
			else if(e.getLocation() != null && e.getLocation().length() != 0)
				html += "<p> Location: " + e.getLocation() + "</p>";
		}
		html += "</body></html>";
		// This seems to take care of the barfing problem mentioned above:
		html = html.replaceAll("%","&#37;");
		return html;
	}

	public static String displayDateRange(EventData e) {
		return displayDateRange(e.getStartTime(),e.getEndTime());
	}
	
	public static String displayDateRange(Date startTime, Date endTime) {
		if(startTime == null)
			return "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		String s = dateFormat.format(startTime);
		if(! isOneDayEvent(startTime, endTime))
			s += " - " + dateFormat.format(endTime);
		s  += " " + timeFormat.format(startTime);
		if(endTime.getTime() - startTime.getTime() != ONE_HOUR &&   // if it was an hour, it was most likely our artificially generated end time
			endTime.getTime() > makeSameDay(endTime, startTime).getTime()) // the city seems to have some problems with their calculations
			s += " - " + timeFormat.format(endTime);
		return s;
	}

	public static boolean isOneDayEvent(Date startTime, Date endTime) {
		return endTime.getTime() - startTime.getTime() < ONE_DAY;
	}

	/**	This is not ideal, but in order to add it to the calendar, we'll make
	 * it a single day - the earliest one possible that is today or later. Ideally,
	 * we would give the user the opportunity to select from a range when adding to
	 * the calendar. The problem is that we can't tell if the event is closed on weekends
	 * or Sundays, etc. The things that work out best are the one day events.
	 */
	public static OneDayRange oneDayFromRange(EventData e) {
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		final Date TODAY = c.getTime(); // very beginning of today

		OneDayRange odr = new OneDayRange();
		odr.start = e.getStartTime();
		odr.end = e.getEndTime();
		// if event was in the past we'll make it the last day:
		if(odr.end.before(TODAY))
			odr.start = makeSameDay(odr.end,odr.start);
		else if(odr.start.getTime() > TODAY.getTime() + ONE_DAY) // in the future, not today
			odr.end = makeSameDay(odr.start,odr.end); // make it the first available day
		else { // otherwise, today is in the range, make it today
			odr.start = makeSameDay(TODAY,odr.start);
			odr.end = makeSameDay(TODAY,odr.end);
		}
		return odr;
	}

	// returns the same day as the source, but retains the time of day of the target
	private static Date makeSameDay(Date source,Date target) {
		Calendar sc = new GregorianCalendar();
		sc.setTime(source);
		Calendar tc = new GregorianCalendar();
		tc.setTime(target);
		tc.set(Calendar.YEAR,sc.get(Calendar.YEAR));
		tc.set(Calendar.MONTH,sc.get(Calendar.MONTH));
		tc.set(Calendar.DAY_OF_MONTH,sc.get(Calendar.DAY_OF_MONTH));
		return tc.getTime();
	}

}
