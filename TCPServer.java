
/*
 * This program creates a TCP server that
 * receives and sends messages from the client
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */

public class TCPServer extends Thread {
	private ServerSocket serverSocket;
	private Socket tcpSocket;
	private ServerData serverData;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private int clientPort;

	public TCPServer(int port, ServerData data) {
		try {
			serverSocket = new ServerSocket(port);
			serverData = data;
			clientPort = port;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * This method verifies the clients credentials
	 * 
	 * @param user
	 *            username.
	 * @param pass
	 *            password.
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
				tcpSocket = serverSocket.accept();
				inputStream = new ObjectInputStream(tcpSocket.getInputStream());

				Message clientMessage = (Message) inputStream.readObject();

				if (clientMessage.isSetTime()) {
					if (serverData.isTimeModifiable()) {
						if (verifyCredentials(clientMessage.getUsername(), clientMessage.getPassword())) {
							serverData.setTime(clientMessage.getTime());
						}
					}
				}

				Message newMessage = new Message();
				newMessage.setTime(serverData.getTime());
				newMessage.setNodeIPAddress(InetAddress.getLocalHost().getHostAddress());
				newMessage.setServerFound(true);
				newMessage.setPort(clientPort);

				outputStream = new ObjectOutputStream(tcpSocket.getOutputStream());
				outputStream.writeObject(newMessage);

				tcpSocket.close();
				inputStream.close();
				outputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
}
