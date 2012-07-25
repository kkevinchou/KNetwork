package network.client;

import java.io.IOException;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		try {
			ClientNetworkManager c = new ClientNetworkManager();
			c.connect();
		} catch (Exception e) {
			int a = 5;
			a++;
		}
	}
}
