package skylight1.nycevents;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;

/**
 * The city's park events RSS feed does not validate because it uses non-RSS 2.0
 * tags without a title space. The description field contains HTML mark-up, which we may
 * want to take advantage of in the display page. But it is only a snippet; i.e.,
 * no html or body tags.
 *
 * @author Rob
 *
 */
public class ParkEventLoader {
	private transient EventData currentEvent;
	private transient String currentStartTime;
	private transient String currentStartDate;
	private transient String currentEndTime;
	
	public List<EventData> getEvents() throws IOException {
		final long methodStartTime = System.currentTimeMillis(); 

		final List<EventData> l = new ArrayList<EventData>();
		URL url;
		url = new URL("http://www.nycgovparks.org/xml/events_300_rss.xml");
		final long gotISTime; 

		final long parsedTime; 

		try {
			InputStream is = url.openConnection().getInputStream();

			gotISTime = System.currentTimeMillis(); 

			RootElement root = new RootElement("rss");
			Element channel = root.getChild("channel");
			Element item = channel.getChild("item");
			Element title = item.getChild("title"); 
			Element description = item.getChild("description"); 
			Element location = item.getChild("http://www.nycgovparks.org/eventsrss_ns/", "location"); 
			Element parknames = item.getChild("parknames"); 
			Element link = item.getChild("link"); 
			Element contactPhone = item.getChild("http://www.nycgovparks.org/eventsrss_ns/", "contact_phone"); 
			Element startDate = item.getChild("http://www.nycgovparks.org/eventsrss_ns/", "startdate"); 
			Element startTime = item.getChild("http://www.nycgovparks.org/eventsrss_ns/", "starttime"); 
			Element endTime = item.getChild("http://www.nycgovparks.org/eventsrss_ns/", "endtime"); 
			// TODO why don't we support end date?????

			item.setElementListener(new ElementListener() {
				@Override
				public void start(Attributes arg0) {
					currentEvent = new EventData();
					currentEvent.setCategory(Constants.PARKS);
				}
				@Override
				public void end() {
					currentEvent.setStartTime(parseTime(currentStartDate, currentStartTime));
					if(currentEvent.getStartTime() != null) {
						currentEvent.setEndTime(parseTime(currentStartDate,currentEndTime));
						if(currentEvent.getEndTime() == null) // since we did get a start time, we can salvage it
							currentEvent.setEndTime(new Date(currentEvent.getStartTime().getTime() + EventUtilities.ONE_HOUR)); // make it an hour so it's easily viewable in the calendar
					}

					if(currentEvent.getTitle() == null || currentEvent.getStartTime() == null) {
						// this would be useless - throw it out
						return;
					}

					l.add(currentEvent);
				}});

			title.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setTitle(aBody);
				}});

			description.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setDescription(aBody);
				}});

			location.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setLocation(aBody);
				}});

			parknames.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setLocation2(aBody);
				}});

			link.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setWebsite(aBody);
				}});

			contactPhone.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEvent.setTelephone(aBody);
				}});

			startDate.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentStartDate = aBody;
				}});


			startTime.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentStartTime = aBody;
				}});

			endTime.setEndTextElementListener(new EndTextElementListener() {
				@Override
				public void end(String aBody) {
					currentEndTime = aBody;
				}});

			XMLReader reader = XMLReaderFactory.createXMLReader(org.xmlpull.v1.sax2.Driver.class.getName());
			reader.setContentHandler(root.getContentHandler());
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuilder sb = new StringBuilder();
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			line = sb.toString().replaceAll("House & Park", "House &amp; Park");
			in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(line.getBytes("UTF-8"))));
			reader.parse(new InputSource(in));
			in.close();
			parsedTime = System.currentTimeMillis(); 

		} catch (SAXException e) {
			e.printStackTrace();
			return l;
		}
		Collections.sort(l, new Comparator<EventData>() {
			public int compare(EventData e1,EventData e2) {
				return e1.getStartTime().compareTo(e2.getStartTime());
			}
		});

		final long endTime = System.currentTimeMillis();

		Log.i(ParkEventLoader.class.getName(), String.format("getting input stream took %d ms; parsing took %d ms; building took %d ms", (gotISTime - methodStartTime), (parsedTime - gotISTime), (endTime - parsedTime)));

		return l;
	}
	
	private static Date parseTime(String date, String time) {
		// The format seems very consistent: "2009-12-04" and "11:00 am" but they don't always have an end time
		if(date == null || date.length() == 0 || time == null || time.length() == 0)
			return null;
		Calendar c = new GregorianCalendar();
		try {
			String[] parts = time.split(" ");
			boolean isAM = parts[1].equals("am");
			parts = parts[0].split(":");
			int hours = Integer.parseInt(parts[0]);
			int minutes = Integer.parseInt(parts[1]);
			if(isAM && hours == 12)
				hours = 0;
			else if(! isAM && hours != 12)
				hours += 12;
			parts = date.split("-");
			c.set(Integer.parseInt(parts[0]),Integer.parseInt(parts[1]) - 1,Integer.parseInt(parts[2]),hours,minutes);
		} catch (Exception e) {
			return null;
		}
		return c.getTime();
	}
}
