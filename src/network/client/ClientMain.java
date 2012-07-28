package network.client;

import java.io.IOException;
import network.message.TestMessage;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		try {
			ClientNetworkManager clientNetworkManager = new ClientNetworkManager("192.168.203.130", 8087);
			clientNetworkManager.register();
			
	        TestMessage t1 = new TestMessage(99);
			TestMessage t2 = new TestMessage(88);
			clientNetworkManager.send(t1);
			clientNetworkManager.send(t2);
		} catch (Exception e) {
		}
	}
}
