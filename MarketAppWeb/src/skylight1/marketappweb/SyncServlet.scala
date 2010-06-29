package skylight1.marketappweb

import javax.servlet.http._

class SyncServlet extends HttpServlet {

	override def doPost(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		resp setContentType "text/plain"
		resp.getWriter().println("It worked.")
		// TODO: this will deserialize the incoming data from the phone and build a HashSet<WatchList>.
		// It will call WatchListSynchronizer.doSynchronization, take the results and serialize them
		// and send it back over the wire.
	}

	override def doGet(req: HttpServletRequest,resp: HttpServletResponse): Unit  = {
		resp setContentType "text/plain"
		resp.getWriter().println("It's a syncing feeling, from Scala.")
	}

}