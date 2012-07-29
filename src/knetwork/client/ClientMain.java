package knetwork.client;

import java.io.IOException;

import knetwork.message.*;


public class ClientMain {
	public static void main(String[] args) throws IOException {
		try {
			ClientNetworkManager clientNetworkManager = new ClientNetworkManager();
			clientNetworkManager.register("192.168.203.130", 8087);
			
			Thread.sleep(5000);
			for (int i = 0; i < 10; i++) {
		        BigMessage b1 = new BigMessage(1300);
				clientNetworkManager.send(b1);
				Thread.sleep(2000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
