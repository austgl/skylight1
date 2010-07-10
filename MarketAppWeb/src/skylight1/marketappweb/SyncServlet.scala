package skylight1.marketappweb

import javax.servlet.http._

class SyncServlet extends HttpServlet {

	override def doPost(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		import scala.xml._ 
		val theXML = XML.load(req.getInputStream)
		// TODO: this will deserialize the incoming data from the phone and build a HashSet<WatchList>.
		// It will call WatchListSynchronizer.doSynchronization, take the results and serialize them
		// and send it back over the wire.
		// For now we'll just send back what was sent:
		resp setContentType "application/xml"
		resp.getWriter().println(theXML.toString)
	}

	override def doGet(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		resp setContentType "text/plain"
		resp.getWriter().println("It's a syncing feeling, from Scala.")
	}

}