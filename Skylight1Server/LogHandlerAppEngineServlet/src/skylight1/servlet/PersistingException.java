package skylight1.servlet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class PersistingException {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Text stackTrace;
	@Persistent
	private String context;
	@Persistent
	private String log;
	@Persistent
	private String packageName;
	@Persistent
	private Integer versionCode;
	@Persistent
	private String versionName;
	@Persistent
	private String threadName;
	@Persistent
	private XMLGregorianCalendar time;
	@Persistent
	private String phoneIdHash;
	@Persistent
	private String device;
	@Persistent
	private String configuration;

	public PersistingException(ExceptionClass data) {
		this.configuration = data.getConfiguration();
		this.context = data.getContext();
		this.device = data.getDevice();
		this.log = data.getLog();
		this.phoneIdHash = data.getPhoneIdHash();
		this.time = data.getTime();
		this.threadName = data.getThreadName();
		this.versionCode = data.getVersionCode();
		this.versionName = data.getVersionName();
		this.packageName = data.getPackageName();
		this.stackTrace = new Text(data.getStackTrace());
	}

	// Accessors for the fields.  JDO doesn't use these, but your application does.

	public Key getKey() {
		return key;
	}

	public String getExceptionName() {
		if (stackTrace == null)
			return "Unknown";
		String trace = getStackTrace().getValue();
		int colonIndex = trace.indexOf(':');
		return new String(trace.toString().substring(0, colonIndex));
	}

	/**
	 * @return the stackTrace
	 */
	public Text getStackTrace() {
		return stackTrace;
	}

	/**
	 * @return the context
	 */
	public String getContext() {
		return context;
	}

	/**
	 * @return the log
	 */
	public String getLog() {
		return log;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * @return the versionCode
	 */
	public Integer getVersionCode() {
		return versionCode;
	}

	/**
	 * @return the versionName
	 */
	public String getVersionName() {
		return versionName;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @return the time
	 */
	public XMLGregorianCalendar getTime() {
		return time;
	}

	/**
	 * @return the phoneIdHash
	 */
	public String getPhoneIdHash() {
		return phoneIdHash;
	}

	/**
	 * @return the device
	 */
	public String getDevice() {
		return device;
	}

	/**
	 * @return the configuration
	 */
	public String getConfiguration() {
		return configuration;
	}



}