package skylight1.nycevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author Rob
 *
 */
public class GoogleCalendarUtilities {
	public static String myToken = "";

	public enum ResultCode {
		AuthenticationFailed, ProcessFailed, ProcessSucceeded // needs more than this
	}

	public static ResultCode addCalendarEvent(GoogleCredentials googleCredentials, EventData eventData) {
		try {
			googleLogin(googleCredentials);  // shouldn't have to do this every time
		} catch (IOException e) {
			return ResultCode.AuthenticationFailed;
		}
		try {
			addEventGuts(generateEventXML(eventData),"http://www.google.com/calendar/feeds/default/private/full");
		} catch (IOException e) {
			return ResultCode.ProcessFailed;
		}
		return ResultCode.ProcessSucceeded;
	}

	private static String generateEventXML(EventData e) {
		String theXML = "<entry xmlns='http://www.w3.org/2005/Atom' " +
	    "xmlns:gd='http://schemas.google.com/g/2005'> " +
	    "<category scheme='http://schemas.google.com/g/2005#kind' " +
	     " term='http://schemas.google.com/g/2005#event'></category> " +
	   " <title type='text'>";
		theXML += stringToHTMLString(e.getTitle());
		theXML += "</title> <content type='text'>";

		// stringToHTMLString must be missing something because some items are rejected -
		// using CDATA for now is more reliable, but neither one looks very good in the Google
		// web calendar. Also to do: add the website link and the phone number. The problem is
		// that they often include them in the description text.
		//theXML += stringToHTMLString(e.getDescription());
		theXML += "<![CDATA[";
		if(e.getDescription()!=null) {
			theXML += e.getDescription();
		}
		theXML += "]]>";

		theXML += "</content> " +
		  " <gd:transparency " +
		    " value='http://schemas.google.com/g/2005#event.opaque'> " +
		  " </gd:transparency> " +
		  " <gd:eventStatus " +
		  "   value='http://schemas.google.com/g/2005#event.confirmed'> " +
		  " </gd:eventStatus> " +
		  " <gd:where valueString='";
		theXML += stringToHTMLString(e.getLocation());
		theXML += "'></gd:where> <gd:when startTime='";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
		TimeZone tz = TimeZone.getTimeZone("GMT");
		sdf.setTimeZone(tz);
		OneDayRange odr = EventUtilities.oneDayFromRange(e);
		theXML += sdf.format(odr.start);
		theXML += "' endTime='";
		theXML += sdf.format(odr.end);
		theXML += "'></gd:when> </entry>";
		return theXML;
	}

	// from http://www.rgagnon.com/javadetails/java-0306.html
	// I had to add the case for apostrophe, but it still doesn't
	// always work for description.
	private static String stringToHTMLString(String string) {
		if(string==null) {
			return null;
		}
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    boolean lastWasBlankChar = false;
	    int len = string.length();
	    char c;

	    for (int i = 0; i < len; i++)
	        {
	        c = string.charAt(i);
	        if (c == ' ') {
	            // blank gets extra work,
	            // this solves the problem you get if you replace all
	            // blanks with &nbsp;, if you do that you loss
	            // word breaking
	            if (lastWasBlankChar) {
	                lastWasBlankChar = false;
	                sb.append("&nbsp;");
	                }
	            else {
	                lastWasBlankChar = true;
	                sb.append(' ');
	                }
	            }
	        else {
	            lastWasBlankChar = false;
	            //
	            // HTML Special Chars
	            if (c == '"')
	                sb.append("&quot;");
	            else if (c == '&')
	                sb.append("&amp;");
	            else if (c == '<')
	                sb.append("&lt;");
	            else if (c == '>')
	                sb.append("&gt;");
	            else if (c == '\n')
	                // Handle Newline
	                sb.append("&lt;br/&gt;");
	            else if (c == '\'') // had to add this because the string is delimited by apostrophes
	            	sb.append("&#39;");
	            else {
	                int ci = 0xffff & c;
	                if (ci < 160 )
	                    // nothing special only 7 Bit
	                    sb.append(c);
	                else {
	                    // Not 7 Bit use the unicode system
	                    sb.append("&#");
	                    sb.append(new Integer(ci).toString());
	                    sb.append(';');
	                    }
	                }
	            }
	        }
	    return sb.toString();
	}

	private static String addEventGuts(String postContents,String theURL) throws IOException {
		String response = "";

        URL u;
        HttpURLConnection uc;
        byte[] payload = postContents.getBytes();
        try {
        	u = new URL(theURL);
        } catch (MalformedURLException e) {
        	return null;
        }
        uc = (HttpURLConnection) u.openConnection();
        uc.setInstanceFollowRedirects(false); // because when it follows, it does a GET!!! We have to repost manually
        uc.setRequestProperty("Authorization", "GoogleLogin auth=" + myToken);
        uc.setRequestProperty("GData-Version", "2");
        uc.setRequestProperty("Content-type", "application/atom+xml");
        uc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
        uc.setRequestMethod("POST");
        uc.setRequestProperty("Content-Length",Integer.toString(payload.length));
        uc.setDoOutput(true);
        uc.getOutputStream().write(payload);
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
        	response += line;
        }
        if(uc.getResponseCode() == 302) {
        	String newURL = response.split("gsessionid=")[1].split("\"")[0];
        	newURL = theURL + "?gsessionid=" + newURL;
        	return addEventGuts(postContents,newURL); // try again
        }
        return response;
	}

	// We'll probably never need this
	public static String getMyCalendars() {
        String response = "";
        URL u;
        HttpURLConnection uc;
        try {
            u = new URL("http://www.google.com/calendar/feeds/default/owncalendars/full");
        } catch (MalformedURLException e) {
            return null;
        }
        try {
            uc = (HttpURLConnection) u.openConnection();
            uc.setRequestProperty("Authorization", "GoogleLogin auth=" + myToken);
            uc.setRequestProperty("GData-Version", "2");
            uc.setRequestProperty("Content-type", "application/atom+xml");
            uc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
            uc.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response += line;
            }
        } catch (Exception e) {
            return null;
        }
        return response;
	}


    private static String googleLogin(GoogleCredentials googleCredentials) throws IOException {
    	String postContents = "Email=" + googleCredentials.getGmailAccount() + "&Passwd=" + googleCredentials.getPassword() + "&source=NYCJava-AndroidApp-1&service=cl";
    	String response = "";
    	URL u;
    	HttpURLConnection uc;
    	byte[] payload = postContents.getBytes();
    	try {
    		u = new URL("https://www.google.com/accounts/ClientLogin");
    	} catch (MalformedURLException e) {
    		return null;
    	}
    	uc = (HttpURLConnection) u.openConnection();
    	uc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
    	uc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.6) Gecko/20060728 Firefox/1.5.0.6");
    	uc.setRequestMethod("POST");
    	uc.setRequestProperty("Content-Length",Integer.toString(payload.length));
    	uc.setDoOutput(true);
    	uc.getOutputStream().write(payload);

    	BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
    	String line;
    	while ((line = in.readLine()) != null) {
    		response += line;
    	}
    	String[] auth = response.split("Auth=");
    	myToken = auth[1]; // class member or pass it around?
    	return myToken;
    }
}
