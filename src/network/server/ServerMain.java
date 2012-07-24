package network.server;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) throws IOException {
		try {
			ServerNetworkManager s = new ServerNetworkManager(9001);
			s.listen();
		} catch (Exception e) {
		}
	}
}
