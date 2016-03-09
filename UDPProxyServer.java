/*

 * This program creates a multithreaded UDP proxy server that
 * receives messages from client/proxy-server and sends either UDP or TCP messages 
 * to the server.
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */
public class UDPProxyServer extends Thread {
	DatagramPacket incomingPacket;
	DatagramSocket udpSocket;
	int clientPort;
	String serverHostname;
	int serverPort;
	boolean useTcp = false;

	public UDPProxyServer(int port, String hostname, int port2) {
		try {
			udpSocket = new DatagramSocket(port);
			clientPort = port;
			serverHostname = hostname;
			serverPort = port2;
			System.out.println("Proxy Server is running");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void setUseTcp(boolean tcp) {
		useTcp = tcp;
	}

	public void run() {
		while (true) {
			try {
				byte[] incomingData = new byte[256];
				incomingPacket = new DatagramPacket(incomingData, incomingData.length);
				udpSocket.receive(incomingPacket);
				new UDPProxyServerStarter(udpSocket, incomingPacket, serverHostname, serverPort, clientPort, useTcp).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * This program creates the necessary sockets and
	 * passes messages back and forth. 
	 */
	private static class UDPProxyServerStarter extends Thread {
		private DatagramSocket udpSocket;
		private ObjectInputStream inputStream;
		private ByteArrayInputStream byteInputStream;
		private ByteArrayOutputStream byteOutputStream;
		private ObjectOutput objectOutput;
		private DatagramPacket incomingPacket;
		String serverHostname;
		int serverPort;
		int clientPort;
		boolean useTcp = false;

		public UDPProxyServerStarter(DatagramSocket socket, DatagramPacket packet, String hostname, int port,
				int clientport1, boolean tcp) {
			try {
				udpSocket = socket;
				incomingPacket = packet;
				serverHostname = hostname;
				serverPort = port;
				clientPort = clientport1;
				useTcp = tcp;
			} catch (Exception e) {
				System.out.println(e);
			}
		}

		public void run() {
			try {
				byteInputStream = new ByteArrayInputStream(incomingPacket.getData());
				inputStream = new ObjectInputStream(byteInputStream);

				Message clientMessage = (Message) inputStream.readObject();

				Message newMessage = new Message();
				newMessage.setPort(clientPort);
				newMessage.setNodeIPAddress(InetAddress.getLocalHost().getHostAddress());

				InetAddress address = incomingPacket.getAddress();
				int port = incomingPacket.getPort();

				byteOutputStream = new ByteArrayOutputStream();
				objectOutput = new ObjectOutputStream(byteOutputStream);
				objectOutput.writeObject(newMessage);

				byte[] reply = byteOutputStream.toByteArray();
				DatagramPacket outgoingPacket = new DatagramPacket(reply, reply.length, address, port);

				udpSocket.send(outgoingPacket);

				if (useTcp) {

					try {
						Socket newSocket = new Socket(serverHostname, serverPort);

						ObjectOutputStream outputStreamToServer = new ObjectOutputStream(newSocket.getOutputStream());

						outputStreamToServer.writeObject(clientMessage);

						ObjectInputStream inputStreamFromServer = new ObjectInputStream(newSocket.getInputStream());

						while (true) {
							Message serverMsg = (Message) inputStreamFromServer.readObject();

							byteOutputStream = new ByteArrayOutputStream();
							objectOutput = new ObjectOutputStream(byteOutputStream);
							objectOutput.writeObject(serverMsg);

							reply = byteOutputStream.toByteArray();
							outgoingPacket = new DatagramPacket(reply, reply.length, address, port);

							udpSocket.send(outgoingPacket);

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

							byteOutputStream = new ByteArrayOutputStream();
							objectOutput = new ObjectOutputStream(byteOutputStream);
							objectOutput.writeObject(serverMsg);

							reply = byteOutputStream.toByteArray();
							outgoingPacket = new DatagramPacket(reply, reply.length, address, port);

							udpSocket.send(outgoingPacket);

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
					byteInputStream.close();
					byteOutputStream.close();
					inputStream.close();
					objectOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
				}

			}
		}
	}
}
