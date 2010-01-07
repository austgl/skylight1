package skylight1.nycevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;

import android.util.Log;

/**
 * data source: http://nyc.gov/events
 *
 */
public class ArtMusicEventLoader {

	private static final long ART_SKIP_AMOUNT = 117000l;
	private static final long MUSIC_SKIP_AMOUNT = 90000l;
	private static final int BUF_LEN = 0x10000;

	private static Pattern titlePattern = Pattern.compile("calendar_link\">(.*?)<");
	private static Pattern urlPattern = Pattern.compile("href=\"(.*?)\"");
	private static Pattern dateLocPattern = Pattern.compile("calendar_italic\">(.*?)</td>");

	private static Pattern locationPattern = Pattern.compile("Location:</span><br>(.*?)</p>");
	private static Pattern websitePattern = Pattern.compile("href=\"(.*?)\"");
	private static Pattern descriptionPattern = Pattern.compile("<p>(.*?)</span>");
	private static Pattern telephonePattern = Pattern.compile("Contact Information:</span><br>(.*?)<br");
	
	private static Pattern musicTitlePattern = Pattern.compile(";\" class=\"content_left\">(.*?)</a>");
	private static Pattern musicDateLocPattern = Pattern.compile("</em><br>(.*?)&nbsp;&nbsp;</span>");
	private static Pattern musicLocationPattern = Pattern.compile("Location:</strong><br>(.*?)<br> <br>");
	private static Pattern musicTelephonePattern = Pattern.compile("Contact Information:</strong><br>(.*?)<br");
	private static Pattern musicDescriptionPattern = Pattern.compile("Description:</strong>(.*?)</td>");

	public static final String TAG = "ArtMusicEventLoader";

	public static List<EventData> getEvents(String artOrMusic) throws IOException {
		//final long startTime = System.currentTimeMillis();
		final boolean doingArt = artOrMusic.equals(Constants.ART);
		List<EventData> resultList = new ArrayList<EventData>();

		// The music URI below is the cultural affairs version. It seems to give better results for music, but for art it gives several pages worth,
		// listed alphabetically and I can't find a way to get it to show all on one page.
		String uriString;
		if(doingArt)
			uriString = "http://nyc.gov/portal/site/nycgov/menuitem.bd175b51da17d74f472ae1852f8089a0/index.jsp?&epi-content=GENERIC&epi-process=generic_process.jsp&beanID=1622248501&viewID=calendar_process_view";
		else
			uriString = "http://www.nyc.gov/portal/site/nycgov/menuitem.6a3b6e0c54cc5afac634761056a09da0/index.jsp?&epi-content=GENERIC&epi-process=generic_process.jsp&beanID=279897244&viewID=calendar_process_view";
		final SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");

		final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		final String today = sdf.format(new Date()); 

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 30000);
		HttpConnectionParams.setSoTimeout(params, 30000);
		DefaultHttpClient client = new DefaultHttpClient(params);

		StringBuilder buffer = new StringBuilder();
		URI uri = null;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
			return resultList;
		}

		HttpPost method = new HttpPost(uri);

		method.addHeader("User-Agent", "	Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.5; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5");

		method.addHeader("Pragma", "no-cache");

		method.addHeader("Content-Type", "application/x-www-form-urlencoded");

		List <NameValuePair> postInfo = new ArrayList <NameValuePair>();

		if(doingArt) {
			postInfo.add(new BasicNameValuePair("pageop", "eventsearch"));
			postInfo.add(new BasicNameValuePair("search_term", artOrMusic.toLowerCase()));
			postInfo.add(new BasicNameValuePair("st_date", dateFormat.format(Calendar.getInstance().getTime())));
		} else {
			postInfo.add(new BasicNameValuePair("borough", "6"));
			postInfo.add(new BasicNameValuePair("category", "54"));
			postInfo.add(new BasicNameValuePair("end_date", today));
			postInfo.add(new BasicNameValuePair("pageop", "browsersearch"));
			postInfo.add(new BasicNameValuePair("st_date", today));
		}

		HttpEntity entity = null;

		entity = new UrlEncodedFormEntity(postInfo, HTTP.UTF_8);

		method.setEntity(entity);

		HttpResponse httpResponse =null;
		BufferedReader inputStream = null;

		httpResponse = client.execute(method);

		if(httpResponse!=null)
		{
			try{

				int contentLength = (int) httpResponse.getEntity().getContentLength();
				if (contentLength < 0){
					Log.e(TAG, "The HTTP response is too long:"+contentLength);
				}

				inputStream = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()),BUF_LEN);
				inputStream.skip(doingArt ? ART_SKIP_AMOUNT : MUSIC_SKIP_AMOUNT);
				String line;
				while ((line = inputStream.readLine()) != null) {
					buffer.append(line);
				}

				inputStream.close();
			} catch (IllegalStateException ise) {
				Log.e(TAG, "Could not get a HTTP response from the server.", ise);
				return resultList;
			}

		} else { //httpresponse is null
			return resultList;
		}

		if (doingArt) {
			buildArtEventList(buffer.toString(), resultList);
			finishArtEventList(resultList);
		} else {
			buildMusicEventList(buffer.toString(), resultList);
			finishMusicEventList(resultList);
		}

		//final long endTime = System.currentTimeMillis();
		//Log.e(TAG, "load took " + (endTime - startTime));
		return resultList;
	}

	private static void buildArtEventList(String theHTML,List<EventData> resultList) {

		Scanner s = new Scanner(theHTML);
		while(true) {
			if(s.findWithinHorizon(titlePattern, 0) == null)
				break;
			else {
				EventData e = new EventData();
				e.setCategory(Constants.ART);
				e.setTitle(s.match().group(1));
				e.setDescription(e.getTitle());
				String x = s.findWithinHorizon(dateLocPattern, 0);
				if(x != null) {
					x = s.match().group(1);
					String[] sa = x.split("</span><br>");
					e.setLocation(sa[1].trim());
					timesFromDateRange(e,sa[0].trim());
				}
				x = s.findWithinHorizon(urlPattern, 0);
				if(x != null) {
					e.setWebsite(URLDecoder.decode(s.match().group(1)));
					e.setWebsite( e.getWebsite().split("url=")[1]);
				}
				resultList.add(e);
			}
		}
	}

	private static void buildMusicEventList(String theHTML,List<EventData> resultList) {

		Scanner s = new Scanner(theHTML);
		while(true) {
			if(s.findWithinHorizon(musicTitlePattern, 0) == null)
				break;
			else {
				EventData e = new EventData();
				e.setCategory(Constants.MUSIC);
				e.setTitle(s.match().group(1));
				e.setDescription(e.getTitle());
				String x = s.findWithinHorizon(musicDateLocPattern, 0);
				if(x != null) {
					x = s.match().group(1);
					String[] sa = x.split("<br>");
					e.setLocation(sa[1].trim());
					timesFromDateRange(e,sa[0].trim());
				}
				x = s.findWithinHorizon(urlPattern, 0);
				if(x != null) {
					e.setWebsite(URLDecoder.decode(s.match().group(1)));
					e.setWebsite( e.getWebsite().split("url=")[1]);
				}
				resultList.add(e);
			}
		}
	}

	/**
	 * This goes out to the website that was in the original result list and
	 * gathers the details of the event. It's fairly slow because it has to
	 * download a rather large page for each event.
	 */
	private static void finishArtEventList(List<EventData> resultList) {
		for(EventData e : resultList) {
			//Log.e(TAG, "details for " + e.getTitle());
			String html = loadPage(e.getWebsite(), ART_SKIP_AMOUNT);
			Scanner s = new Scanner(html);
			if(s.findWithinHorizon("Selected event Here", 0) == null)
				continue;
			String x = s.findWithinHorizon(locationPattern, 0);
			if(x != null)
				e.setLocation2(s.match().group(1).trim().replaceAll("<br>", " ")); // I suspect this will usually be better than the first location (Event.location)

			x = s.findWithinHorizon(websitePattern, 0);
			if(x != null) { // this replaces the city's details page with the actual event's website
				x = s.match().group(1).trim();
				if(x.contains("url="))
					x = x.split("url=")[1];
				e.setWebsite(x);
			}
			x = s.findWithinHorizon(telephonePattern, 0);
			if(x != null)
				e.setTelephone(s.match().group(1).trim());
			x = s.findWithinHorizon(descriptionPattern, 0);
			if(x != null)
				e.setDescription(s.match().group(1).trim());
		}
		//Log.e(TAG, "Event list finished.");
	}

	private static void finishMusicEventList(List<EventData> resultList) {
		for(EventData e : resultList) {
			//Log.e(TAG, "details for " + e.getTitle());
			String html = loadPage(e.getWebsite(), MUSIC_SKIP_AMOUNT);
			Scanner s = new Scanner(html);
			if(s.findWithinHorizon("Selected event Here", 0) == null)
				continue;
			String x = s.findWithinHorizon(musicLocationPattern, 0);
			if(x != null)
				e.setLocation2(s.match().group(1).trim().replaceAll("<br>", " ")); // I suspect this will usually be better than the first location (Event.location)

			x = s.findWithinHorizon(websitePattern, 0);
			if(x != null) { // this replaces the city's details page with the actual event's website
				x = s.match().group(1).trim();
				if(x.contains("url="))
					x = x.split("url=")[1];
				e.setWebsite(x);
			}
			x = s.findWithinHorizon(musicTelephonePattern, 0);
			if(x != null)
				e.setTelephone(s.match().group(1).trim());
			x = s.findWithinHorizon(musicDescriptionPattern, 0);
			if(x != null)
				e.setDescription(s.match().group(1).trim().replaceAll("<br>", ""));
		}
		//Log.e(TAG, "Event list finished.");
	}

	private static String loadPage(String theUrl, long skipAmount) {
		StringBuilder retVal = new StringBuilder();
		URL u;
		HttpURLConnection uc;
		try {
			u = new URL(theUrl);
		} catch (MalformedURLException e) {
			return (theUrl + " is not a parseable URL");
		}
		try {
			uc = (HttpURLConnection) u.openConnection();
			uc.setRequestProperty("Accept",	"text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			uc.setRequestProperty("User-Agent",	"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
			uc.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()),BUF_LEN);
			in.skip(skipAmount);
			String line;
			while ((line = in.readLine()) != null) {
				retVal.append(line);
			}
			in.close();
		} catch (Exception e) {
			return "";
		}
		return retVal.toString();
	}

	/**
	 * This is just different enough from the park events format to make it not worth
	 * trying to generalize the code  and share it.
	 * @param e
	 */
	private static void timesFromDateRange(EventData e, String dateRange) {
		// 11/25/2009-1/18/2010, 8:00 am- 8:00 pm
		String startDate,endDate = null,startTime,endTime = null;
		Date sd,ed;
		String[] sa = dateRange.split(",");
		if(sa[0].contains("-")) {
			String[] sa2 = sa[0].split("-");
			startDate = sa2[0].trim();
			endDate = sa2[1].trim();
		}
		else
			startDate = sa[0].trim();
		if(sa[1].contains("ll")) // occasionally has "All Day"
			sa[1] = "9:00 am- 5:00 pm";
		if(sa[1].contains("-")) {
			String[] sa2 = sa[1].split("-");
			startTime = sa2[0].trim();
			endTime = sa2[1].trim();
		}
		else
			startTime = sa[1].trim();
		Calendar c = new GregorianCalendar();
		sa = startDate.split("/");
		c.set(Integer.parseInt(sa[2]),Integer.parseInt(sa[0]) - 1,Integer.parseInt(sa[1]));
		sd = c.getTime();
		if(endDate != null) {
			sa = endDate.split("/");
			c.set(Integer.parseInt(sa[2]),Integer.parseInt(sa[0]) - 1,Integer.parseInt(sa[1]));
			ed = c.getTime();
		} else {
			ed = sd;
		}
		e.setStartTime(addTime(sd, startTime));
		if(endTime != null)
			e.setEndTime(addTime(ed, endTime));
		else
			e.setEndTime(new Date(e.getStartTime().getTime() + EventUtilities.ONE_HOUR)); // make it an hour so it's easily viewable in the calendar
	}

	private static Date addTime(Date date, String time) {
		Calendar c = new GregorianCalendar();
		try {
			String[] parts = time.split(" ");
			boolean isAM = parts[1].trim().contains("a");
			parts = parts[0].split(":");
			int hours = Integer.parseInt(parts[0]);
			int minutes = Integer.parseInt(parts[1]);
			if(isAM && hours == 12)
				hours = 0;
			else if(! isAM && hours != 12)
				hours += 12;
			c.setTime(date);
			c.set(GregorianCalendar.HOUR_OF_DAY, hours);
			c.set(GregorianCalendar.MINUTE, minutes);
		} catch (Exception e) {
			return null;
		}
		return c.getTime();
	}

}
