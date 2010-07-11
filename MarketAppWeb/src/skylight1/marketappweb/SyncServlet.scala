package skylight1.marketappweb

import javax.servlet.http._
import java.util._
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Date;
import scala.xml._ 

import skylight1.marketappweb.model._

class SyncServlet extends HttpServlet {

	override def doPost(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		val theXML = XML.load(req.getInputStream)
		resp setContentType "application/xml"
		val inList = new HashSet[WatchList]
		val user = theXML \ "@user" text
		val password = theXML \ "@password" text;
		for(l <- theXML \ "list") processXMLList(l,inList,user)
		val outList = WatchListSynchronizer.doSynchronization(inList, user)
		resp setContentType "application/xml"
		resp.getWriter().println("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>" + generateXML(outList).toString)
	}

	override def doGet(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		resp setContentType "text/plain"
		resp.getWriter().println("It's a syncing feeling, from Scala.")
	}

	val formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	def processXMLList(theXML: Node, list: Set[WatchList], user: String ) = {
		val wl = new WatchList(theXML \ "@listName" text,new TreeSet[WatchListItem],user)
		list add wl
		wl.setDeleted({if((theXML \ "@deleted").text.equals("true")) true else false})
		wl.setLastEdited(formatter.parse(theXML \ "@lastEdited" text, new ParsePosition(0)))
		for(i <- theXML \ "item") processXMLItem(i,wl)
	}

	def processXMLItem(theXML: Node, wl: WatchList) = {
		val i = new WatchListItem(theXML \ "@ticker" text)
		wl.getItems().add(i)
		i.setDeleted({if((theXML \ "@deleted").text.equals("true")) true else false})
		i.setLastEdited(formatter.parse(theXML \ "@lastEdited" text, new ParsePosition(0)))
	}
	
	def generateItemXML(i: WatchListItem) = <item ticker={i.getTicker}/>
		
	def generateWatchListXML(wl: WatchList) = {
		<list listName={wl.getListName}>{for(val i <- wl.getItems.toArray) yield generateItemXML(i.asInstanceOf[WatchListItem])}</list>
	}

	// At least for now, the outgoing XML won't need the user, since since the identity is known by the caller.
	// The deleted and lastEdited attributes are not needed either, since the default of false and the current time are correct.
	def generateXML(synced: Set[WatchList]) = {
		<lists>{for(val wl <- synced.toArray) yield generateWatchListXML(wl.asInstanceOf[WatchList])}</lists>
	}
}