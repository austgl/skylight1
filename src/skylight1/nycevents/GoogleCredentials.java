package skylight1.nycevents;

/**
 * 
 * @author Rob
 *
 */
public class GoogleCredentials {
	private String gmailAccount;
	private String password;

	public GoogleCredentials() {}
	
	public GoogleCredentials(String gmailAccount, String password) {
		this.gmailAccount = gmailAccount;
		this.password = password;
	}
	
	public String getGmailAccount() {
		return gmailAccount;
	}

	public void setGmailAccount(String gmailAccount) {
		this.gmailAccount = gmailAccount;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
