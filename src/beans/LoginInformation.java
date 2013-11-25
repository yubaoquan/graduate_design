package beans;

public class LoginInformation {

	private String serverName;
	private String userName;
	private String password;
	
	public LoginInformation() {}
	
	public LoginInformation(String serverName, String userName, String password) {
		this.serverName = serverName;
		this.userName = userName;
		this.password = password;
	}
	public String getServerName() {
		return serverName;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
