package network.client;

import java.io.IOException;

import network.message.*;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		try {
			ClientNetworkManager clientNetworkManager = new ClientNetworkManager();
			clientNetworkManager.register("192.168.203.130", 8087);
			
	        BigMessage b1 = new BigMessage(9000);
			clientNetworkManager.send(b1);
		} catch (Exception e) {
		}
	}
}
