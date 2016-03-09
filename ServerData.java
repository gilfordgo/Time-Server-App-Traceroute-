/*
 * This class represents the data that the server holds
 * including the username, password and the time
 */

import java.io.Serializable;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */

public class ServerData implements Serializable {
	private static volatile long time;
	private static volatile String username;
	private static volatile String password;
	private static volatile boolean isTimeModifiable=false;

	
	public boolean isTimeModifiable() {
		return isTimeModifiable;
	}

	public void setTimeModifiable(boolean isTimeModifiable) {
		ServerData.isTimeModifiable = isTimeModifiable;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		ServerData.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		ServerData.password = password;
	}

	public void setTime(long t) {
		time = t;
	}

	public long getTime() {
		return time;
	}

}
