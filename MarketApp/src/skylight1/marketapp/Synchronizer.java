package skylight1.marketapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

import skylight1.marketapp.model.WatchList;
import skylight1.marketapp.model.WatchListItem;

public class Synchronizer {
	public static List<WatchList> generateDummyWatchLists() {
		List<WatchList> l = new ArrayList<WatchList>();
		WatchList wl = new WatchList("Tech Stocks",new TreeSet<WatchListItem>());
		wl.getItems().add(new WatchListItem("GOOG"));
		wl.getItems().add(new WatchListItem("MSFT"));
		wl.getItems().add(new WatchListItem("IBM"));
		l.add(wl);

		wl = new WatchList("Energy Stocks",new TreeSet<WatchListItem>());
		wl.getItems().add(new WatchListItem("BP"));
		wl.getItems().add(new WatchListItem("XOM"));
		wl.getItems().add(new WatchListItem("GOOG"));
		l.add(wl);
		return l;
	}
	
/**
 * 
 * @param androidLists  provide the data stored on the phone
 * @param synchronizedLists  provide an empty list which will be filled with the synchronized data 
 * @return true on success
 */
	public static boolean doSynchronization(List<WatchList> androidLists, List<WatchList> synchronizedLists) {
		String uriString = "http://10.0.2.2:8888/sync";
		URI uri = null;
		try {
			uri = new URI(uriString);

			HttpPost postRequest = new HttpPost(uri);

			postRequest.setHeader("User-Agent",
							"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.1) Gecko/20091102 Firefox/3.5.5 (Debian-2.0.0.1+dfsg-2)");
			postRequest.setHeader("Content-Type", "application/xml");
			postRequest.setHeader("Accept",
							"text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
			HttpEntity entity = null;

			entity = new StringEntity(generateOutgoingXML(androidLists), HTTP.UTF_8);

			postRequest.setEntity(entity);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse = null;

			httpResponse = client.execute(postRequest);
														
			parseIncomingXML(httpResponse.getEntity().getContent(),synchronizedLists);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	static private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	private static String generateOutgoingXML(List<WatchList> l)
			throws IllegalArgumentException, IllegalStateException, IOException {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		serializer.startDocument("UTF-8", true);
		serializer.startTag("", "lists");
		serializer.attribute("", "user", "aardvark"); // We better do something
														// better than this!!
		serializer.attribute("", "password", "");

		for (WatchList wl : l) {
			serializer.startTag("", "list");
			serializer.attribute("", "listName", wl.getListName());
			if (wl.getDeleted())
				serializer.attribute("", "deleted", "true");
			else
				serializer.attribute("", "deleted", "false");
			serializer.attribute("", "lastEdited", formatter.format(wl
					.getLastEdited()));
			for (WatchListItem i : wl.getItems())
				addItem(serializer, i);
			serializer.endTag("", "list");
		}
		serializer.endTag("", "lists");
		serializer.endDocument();
		return writer.toString();
	}

	private static void addItem(XmlSerializer serializer, WatchListItem i)
			throws IllegalArgumentException, IllegalStateException, IOException {
		serializer.startTag("", "item");
		serializer.attribute("", "ticker", i.getTicker());
		if (i.getDeleted())
			serializer.attribute("", "deleted", "true");
		else
			serializer.attribute("", "deleted", "false");
		serializer.attribute("", "lastEdited", formatter.format(i
				.getLastEdited()));
		serializer.endTag("", "item");
	}

	static class WatchListHandler extends DefaultHandler {
		WatchListHandler(List<WatchList> synchronizedLists) {
			this.synchronizedLists = synchronizedLists;
		}

		private List<WatchList> synchronizedLists;
		WatchList currentList;

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			if (localName.equals("list")) {
				currentList = new WatchList(attributes.getValue("listName"),
						new TreeSet<WatchListItem>());
				synchronizedLists.add(currentList);
				// at least for now we can ignore deleted and lastEdited because
				// they will always
				// be false and the current time
			} else if (localName.equals("item")) {
				currentList.getItems().add(
						new WatchListItem(attributes.getValue("ticker")));
				// Date d = formatter.parse(attributes.getValue("lastEdited"),
				// new ParsePosition(0));
				// String s = formatter.format(d);
				// boolean deleted =
				// attributes.getValue("deleted").equals("true");
				// at least for now we can ignore deleted and lastEdited because
				// they will always
				// be false and the current time
			}
		}

	}

	public static void parseIncomingXML(InputStream is,	List<WatchList> synchronizedLists) throws IOException, SAXException {
		Xml.parse(is, Xml.Encoding.UTF_8, new WatchListHandler(
				synchronizedLists));
	}

}
