package skylight1.marketappweb;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;




import skylight1.marketappweb.model.*;

@SuppressWarnings({"unchecked","serial"})
public class SyncServlet extends HttpServlet {
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.getWriter().println("It worked.");
		// TODO: this will deserialize the incoming data from the phone and build a HashSet<WatchList>.
		// It will call WatchListSynchronizer.doSynchronization, take the results and serialize them
		// and send it back over the wire.
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("It's a syncing feeling.");
		}
}
