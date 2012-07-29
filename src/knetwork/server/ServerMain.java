package knetwork.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.message.Message;
import knetwork.threads.ReceiveThread;


public class ServerMain {
	public static void main(String[] args) throws IOException {
		try {
			ServerNetworkManager serverNetworkManager = new ServerNetworkManager(8087);
			serverNetworkManager.waitForRegistrations(1);
			
			while (true) {
				Message m = serverNetworkManager.recv();
				if (m == null) continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
