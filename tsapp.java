
/*
 * This program takes the user input and calls 
 * the specific class (server, proxy, client).
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 * CSCI651 Project #1
 * 
 * @author Gilford Fernandes
 * @version 1.0
 * @since 2016-02-20
 */
public class tsapp {
	
	/**
	 * This main method
	 * 
	 * @param  args  command line arguments
	 * 
	 * @return
	 */
	public static void main(String[] args) {

		ArrayList<String> arguments = new ArrayList<String>(Arrays.asList(args));
		try {
			if (arguments.contains("-c")) {

				String serverIp = arguments.get(arguments.indexOf("-c") + 1);
				boolean useUdp = true;
				boolean useUtcTime = false;
				int noOfQuerries = 1;
				Message msg = new Message();
				int port = Integer.parseInt(args[args.length - 1]);

				if (arguments.contains("-t")) {
					useUdp = false;
				}

				if (arguments.contains("-z")) {
					useUtcTime = true;
				}

				if (arguments.contains("-T")) {
					msg.setSetTime(true);
					msg.setTime(Long.parseLong(arguments.get(arguments.indexOf("-T") + 1)));
					msg.setUsername(arguments.get(arguments.indexOf("--user") + 1));
					msg.setPassword(arguments.get(arguments.indexOf("--pass") + 1));
				}

				if (arguments.contains("-n")) {
					noOfQuerries = Integer.parseInt(arguments.get(arguments.indexOf("-n") + 1));
				}

				if (useUdp) {
					UDPClient client = new UDPClient(serverIp, port);
					client.setMessage(msg);
					client.setUseUtcTime(useUtcTime);
					client.setnumOfQuerries(noOfQuerries);
					client.connect();
				} else {
					TCPClient client = new TCPClient(serverIp, port);
					client.setMessage(msg);
					client.setUseUtcTime(useUtcTime);
					client.setnumOfQuerries(noOfQuerries);
					client.connect();
				}

			} else if (arguments.contains("-s")) {
				int udpPort = Integer.parseInt(args[args.length - 2]);
				int tcpPort = Integer.parseInt(args[args.length - 1]);
				ServerData data = new ServerData();

				if (arguments.contains("-T")) {
					data.setTime(Long.parseLong(arguments.get(arguments.indexOf("-T") + 1)));
				}

				if (arguments.contains("--user")) {
					data.setTimeModifiable(true);
					data.setUsername(arguments.get(arguments.indexOf("--user") + 1));
				}

				if (arguments.contains("--pass")) {
					data.setPassword(arguments.get(arguments.indexOf("--pass") + 1));
				}

				new UDPServer(udpPort, data).start();
				new TCPServer(tcpPort, data).start();

			} else if (arguments.contains("-p")) {
				int udpPort = Integer.parseInt(args[args.length - 2]);
				int tcpPort = Integer.parseInt(args[args.length - 1]);
				String serverIp = arguments.get(arguments.indexOf("-p") + 1);
				int udpServerPort = 0, tcpServerPort = 0;
				boolean useUdp = false;
				boolean useTcp = false;

				if (arguments.contains("--proxy-udp")) {
					udpServerPort = Integer.parseInt(arguments.get(arguments.indexOf("--proxy-udp") + 1));
				}

				if (arguments.contains("--proxy-tcp")) {
					tcpServerPort = Integer.parseInt(arguments.get(arguments.indexOf("--proxy-tcp") + 1));
				}

				if (arguments.contains("-t")) {
					useTcp = true;
				}

				if (arguments.contains("-u")) {
					useUdp = true;
				}

				TCPProxyServer tcpProxy;
				UDPProxyServer udpProxy;

				if (useUdp) {
					tcpProxy = new TCPProxyServer(tcpPort, serverIp, udpServerPort);
					udpProxy = new UDPProxyServer(udpPort, serverIp, udpServerPort);
					tcpProxy.setUseUdp(true);
				} else if (useTcp) {
					tcpProxy = new TCPProxyServer(tcpPort, serverIp, tcpServerPort);
					udpProxy = new UDPProxyServer(udpPort, serverIp, tcpServerPort);
					udpProxy.setUseTcp(true);
				} else {
					tcpProxy = new TCPProxyServer(tcpPort, serverIp, tcpServerPort);
					udpProxy = new UDPProxyServer(udpPort, serverIp, udpServerPort);
				}
				tcpProxy.start();
				udpProxy.start();

			}
		} catch (Exception e) {
			System.out.println("Enter correct details");
			System.out.println(e);
			System.exit(0);
		}
	}
}
