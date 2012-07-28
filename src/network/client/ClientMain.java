package network.client;

import java.io.IOException;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		try {
			ClientNetworkManager clientNetworkManager = new ClientNetworkManager("192.168.203.130", 8087);
			clientNetworkManager.register();
			clientNetworkManager.disconnect();
		} catch (Exception e) {
		}
	}
}
