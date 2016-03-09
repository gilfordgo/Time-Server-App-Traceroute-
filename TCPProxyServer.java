
/*
 * This program creates a multithreaded TCP proxy server that
 * receives messages from client/proxy-server and sends either UDP or TCP messages 
 * to the server.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
public class TCPProxyServer extends Thread {
	ServerSocket listener;
	String serverHostname;
	int serverPort;
	int clientPort;
	boolean useUdp = false;

	public TCPProxyServer(int port, String hostname, int port2) {
		try {
			listener = new ServerSocket(port);
			serverHostname = hostname;
			serverPort = port2;
			clientPort = port;
			System.out.println("Proxy Server is running");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void setUseUdp(boolean udp) {
		useUdp = udp;
	}

	public void run() {
		while (true) {
			try {
				TCPServerProxyStarter server = new TCPServerProxyStarter();
				server.setData(listener.accept(), serverHostname, serverPort, clientPort, useUdp);
				server.start();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}

	/*
	 * This program creates the necessary sockets and
	 * passes messages back and forth. 
	 */
	private static class TCPServerProxyStarter extends Thread {
		private Socket tcpSocket;
		private ObjectInputStream inputStream;
		private ObjectOutputStream outputStream;
		String serverHostname;
		int serverPort;
		int clientPort;
		boolean useUdp = false;

		public TCPServerProxyStarter() {

		}

		public void setData(Socket socket, String hostname, int port, int clientPort1,boolean udp) {
			try {
				tcpSocket = socket;
				serverHostname = hostname;
				serverPort = port;
				clientPort = clientPort1;
				useUdp=udp;
			} catch (Exception e) {
				System.out.println(e);
				System.exit(0);
			}
		}

		public void run() {
			try {
				inputStream = new ObjectInputStream(tcpSocket.getInputStream());

				Message clientMessage = (Message) inputStream.readObject();

				Message newMessage = new Message();
				newMessage.setNodeIPAddress(InetAddress.getLocalHost().getHostAddress());
				newMessage.setPort(clientPort);

				outputStream = new ObjectOutputStream(tcpSocket.getOutputStream());
				outputStream.writeObject(newMessage);

				if (!useUdp) {

					try {
						Socket newSocket = new Socket(serverHostname, serverPort);

						ObjectOutputStream outputStreamToServer = new ObjectOutputStream(newSocket.getOutputStream());

						outputStreamToServer.writeObject(clientMessage);

						ObjectInputStream inputStreamFromServer = new ObjectInputStream(newSocket.getInputStream());

						while (true) {
							Message serverMsg = (Message) inputStreamFromServer.readObject();

							outputStream.writeObject(serverMsg);

							if (serverMsg.isServerFound())
								break;

						}

						outputStreamToServer.close();
						inputStreamFromServer.close();
						newSocket.close();
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
					}
				} else {
					try {

						DatagramSocket newSocket = new DatagramSocket();
						InetAddress ip = InetAddress.getByName(serverHostname);

						ByteArrayOutputStream byteOutputStreamToServer = new ByteArrayOutputStream();
						ObjectOutputStream objectOutputToServer = new ObjectOutputStream(byteOutputStreamToServer);
						objectOutputToServer.writeObject(clientMessage);

						byte[] byteMsg = byteOutputStreamToServer.toByteArray();
						DatagramPacket packet = new DatagramPacket(byteMsg, byteMsg.length, ip, serverPort);

						newSocket.send(packet);

						byte[] buf = new byte[256];
						packet = new DatagramPacket(buf, buf.length);

						ByteArrayInputStream byteInputStreamFromServer;
						while (true) {

							newSocket.receive(packet);

							byteInputStreamFromServer = new ByteArrayInputStream(buf);
							inputStream = new ObjectInputStream(byteInputStreamFromServer);

							Message serverMsg = (Message) inputStream.readObject();

							outputStream.writeObject(serverMsg);

							if (serverMsg.isServerFound())
								break;
						}

						byteInputStreamFromServer.close();
						byteOutputStreamToServer.close();
						objectOutputToServer.close();
						newSocket.close();

					} catch (Exception e) {
						System.out.println(e);
						System.exit(0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			} finally {

				try {
					inputStream.close();
					outputStream.close();
					tcpSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}

}
