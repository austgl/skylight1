package skylight1.servlet;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * A convenience class for unmarshalling XML representing an ExceptionClass.
 * 
 * @author Richard Ledley
 *
 */
public class ExceptionUnmarshaller {

	private static JAXBContext context;
	private static final Object contextLock = new Object();

	/**
	 * Unmarshalls supplied String of xml.
	 * @param xml the XML to be unmarshalled
	 * @return the created ExceptionClass instance
	 * @throws JAXBException if an error occurs while unmarshalling, most likely sue to invalid XML.
	 */
	public ExceptionClass unmarshall(String xml) throws JAXBException {
		Unmarshaller um = getUnmarshaller();
		return (ExceptionClass)um.unmarshal(new StringReader(xml));
	}

	/**
	 * Private method to get an unmarshaller.
	 * @return an unmarshaller
	 */
	private static Unmarshaller getUnmarshaller() {
		ensureContext();
		Unmarshaller unmarshaller;
		synchronized(contextLock) {
			try {
				 unmarshaller = context.createUnmarshaller();
			} catch (JAXBException e) {
				unmarshaller = null;
			}
		}
		return unmarshaller;
	}

	/**
	 * Private method to ensure we have our context object.
	 */
	private static void ensureContext() {
		if (context == null) {
			synchronized(contextLock) {
				if (context == null)
					try {
						context = JAXBContext.newInstance(ExceptionClass.class);
					} catch (JAXBException e) {
						e.printStackTrace();
						context = null;
					}
			}
		}
	}
}
