
/*
 * This class represents a message that will be passed 
 * around in the application between client and server.
 */

import java.io.Serializable;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */
public class Message implements Serializable {

	private String username;
	private boolean setTime = false;
	private long time;
	private int port;
	private String password;
	private boolean serverFound = false;
	private String nodeIPAddress;	

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isServerFound() {
		return serverFound;
	}

	public void setServerFound(boolean serverFound) {
		this.serverFound = serverFound;
	}

	public String getNodeIPAddress() {
		return nodeIPAddress;
	}

	public void setNodeIPAddress(String nodeIPAddress) {
		this.nodeIPAddress = nodeIPAddress;
	}

	public boolean isSetTime() {
		return setTime;
	}

	public void setSetTime(boolean setTime) {
		this.setTime = setTime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long timeInUnix) {
		this.time = timeInUnix;
	}

}
