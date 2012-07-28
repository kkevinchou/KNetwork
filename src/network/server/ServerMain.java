package network.server;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) throws IOException {
		try {
			ServerNetworkManager serverNetworkManager = new ServerNetworkManager(8087);
			serverNetworkManager.waitForRegistrations(4);
			serverNetworkManager.disconnect();
		} catch (Exception e) {
		}
	}
}
