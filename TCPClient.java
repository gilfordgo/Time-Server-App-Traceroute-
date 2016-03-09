
/*
 * This program creates a TCP client that
 * sends and receives messages from the server
 */

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */

public class TCPClient {
	private Socket socket;
	private String hostname;
	private int port;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private long startTime;
	private Message message;
	private int numOfQuerries = 1;
	private boolean useUtcTime = false;

	public TCPClient(String hostIp, int port) {
		this.hostname = hostIp;
		this.port = port;
	}

	public void setUseUtcTime(boolean utc) {
		useUtcTime = utc;
	}

	public void setMessage(Message msg) {
		message = msg;
	}

	public void setnumOfQuerries(int querries) {
		numOfQuerries = querries;
	}

	/**
	 * This method creates a tcp socket, connects with the server
	 * and sends and receives messages from the server.
	 */
	public void connect() {
		try {

			for (int i = 0; i < numOfQuerries; i++) {
				System.out.println();
				
				socket = new Socket(hostname, port);

				outputStream = new ObjectOutputStream(socket.getOutputStream());

				startTime = System.currentTimeMillis();

				outputStream.writeObject(message);

				inputStream = new ObjectInputStream(socket.getInputStream());

				while (true) {
					Message serverMsg = (Message) inputStream.readObject();

					System.out.println(serverMsg.getNodeIPAddress() + ":" + serverMsg.getPort() + " - "
							+ (System.currentTimeMillis() - startTime) + " ms");

					if (serverMsg.isServerFound()) {
						if (!useUtcTime) {
							try {
									SimpleDateFormat gmtFormat = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy");
									TimeZone utcTime = TimeZone.getTimeZone("UTC");
									gmtFormat.setTimeZone(utcTime);
									SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss zzz yyyy");
									sdf.setTimeZone(utcTime);
									String s= new Date(serverMsg.getTime()*1000) + "";
									System.out.println("Time: " + gmtFormat.format(sdf.parse(s)));
								} catch (ParseException e) {
									e.printStackTrace();
								}
						} else {
							System.out.println("Time: " + serverMsg.getTime());
						}
						break;
					}
				}
				
				outputStream.close();
				inputStream.close();
				socket.close();
			}

		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}
}
