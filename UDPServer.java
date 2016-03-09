
/*
 * This program creates a UDP server that
 * receives and sends messages from the client
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */

public class UDPServer extends Thread {
	private DatagramSocket udpSocket;
	private ObjectInputStream inputStream;
	private ByteArrayInputStream byteInputStream;
	private ByteArrayOutputStream byteOutputStream;
	private ObjectOutput objectOutput;
	private DatagramPacket incomingPacket;
	private ServerData serverData;
	private int clientPort;

	public UDPServer(int port, ServerData data) {
		try {
			udpSocket = new DatagramSocket(port);
			System.out.println("Server is running");
			serverData = data;
			clientPort = port;
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}

	/**
	 * This method verifies the clients credentials
	 * 
	 * @param user  username.
	 * @param pass  password.
	 * 
	 * @return true if credentials are correct else false.
	 */
	public boolean verifyCredentials(String user, String pass) {
		if (serverData.getUsername().equals(user) && serverData.getPassword().equals(pass))
			return true;
		return false;
	}

	public void run() {

		try {

			while (true) {
				byte[] incomingData = new byte[256];
				incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				udpSocket.receive(incomingPacket);

				byteInputStream = new ByteArrayInputStream(incomingPacket.getData());
				inputStream = new ObjectInputStream(byteInputStream);

				Message clientMessage = (Message) inputStream.readObject();

				if (clientMessage.isSetTime()) {
					if (serverData.isTimeModifiable()) {
						if (verifyCredentials(clientMessage.getUsername(), clientMessage.getPassword())) {
							serverData.setTime(clientMessage.getTime());
						}
					}
				}

				Message newMessage = new Message();
				newMessage.setTime(new ServerData().getTime());
				newMessage.setNodeIPAddress(InetAddress.getLocalHost().getHostAddress());
				newMessage.setServerFound(true);
				newMessage.setPort(clientPort);

				InetAddress address = incomingPacket.getAddress();
				int port = incomingPacket.getPort();

				byteOutputStream = new ByteArrayOutputStream();
				objectOutput = new ObjectOutputStream(byteOutputStream);
				objectOutput.writeObject(newMessage);

				byte[] reply = byteOutputStream.toByteArray();
				DatagramPacket outgoingPacket = new DatagramPacket(reply, reply.length, address, port);

				udpSocket.send(outgoingPacket);

				byteInputStream.close();
				byteOutputStream.close();
				inputStream.close();
				objectOutput.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			udpSocket.close();
			System.exit(0);
		}
	}
}
