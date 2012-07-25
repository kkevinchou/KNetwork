package network.server;

import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerNetworkManager {
	private DatagramSocket socket = null;
	
	public ServerNetworkManager(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	public void listen() {
		
	}
}
