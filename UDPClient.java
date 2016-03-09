
/*
 * This program creates a UDP client that
 * sends and receives messages from the server
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

public class UDPClient {
	DatagramSocket socket;
	private String hostname;
	private int port;
	private ObjectInputStream inputStream;
	private ByteArrayInputStream byteInputStream;
	private ByteArrayOutputStream byteOutputStream;
	private ObjectOutput objectOutput;
	private DatagramPacket packet;
	private long startTime;
	private Message message;
	private int numOfQuerries = 1;
	private boolean useUtcTime = false;

	public UDPClient(String hostIp, int port) {
		this.hostname = hostIp;
		this.port = port;
	}

	public void setMessage(Message msg) {
		message = msg;
	}

	public void setUseUtcTime(boolean utc) {
		useUtcTime = utc;
	}

	public void setnumOfQuerries(int querries) {
		numOfQuerries = querries;
	}

	/**
	 * This method creates a datagram socket and sends and receives messages
	 * from the server.
	 */
	public void connect() {
		try {

			for (int i = 0; i < numOfQuerries; i++) {

				socket = new DatagramSocket();
				InetAddress ip = InetAddress.getByName(hostname);

				byteOutputStream = new ByteArrayOutputStream();
				objectOutput = new ObjectOutputStream(byteOutputStream);
				objectOutput.writeObject(message);

				byte[] byteMsg = byteOutputStream.toByteArray();
				packet = new DatagramPacket(byteMsg, byteMsg.length, ip, port);

				System.out.println();

				startTime = System.currentTimeMillis();

				socket.send(packet);

				byte[] buf = new byte[256];

				packet = new DatagramPacket(buf, buf.length);

				while (true) {

					socket.receive(packet);

					byteInputStream = new ByteArrayInputStream(buf);
					inputStream = new ObjectInputStream(byteInputStream);

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

				byteInputStream.close();
				byteOutputStream.close();
				inputStream.close();
				objectOutput.close();
				socket.close();
			}

		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}

}
