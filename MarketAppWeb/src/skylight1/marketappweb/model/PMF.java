package skylight1.marketappweb.model;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * 
 * @author Rob
 * 
 * Directly "borrowed" from Google's documentation.
 *
 */
public final class PMF {
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {}

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
}