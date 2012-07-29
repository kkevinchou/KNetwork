package knetwork.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.message.*;
import knetwork.threads.ReceiveThread;
import knetwork.threads.SendThread;


public class ServerMain {
	public static void main(String[] args) throws IOException {
		try {
//			ServerNetworkManager serverNetworkManager = new ServerNetworkManager(8087);
//			serverNetworkManager.waitForRegistrations(2);
//			
//			while (true) {
//				Message m = serverNetworkManager.recv();
//				serverNetworkManager.broadcast(new TestMessage(1));
//				Thread.sleep(2000);
//				if (m == null) continue;
//			}
			DatagramSocket socket = new DatagramSocket();
			SendThread s = new SendThread("1", 8087, socket);
			s.start();
			while (true) {
				System.out.println("meow");
				s.queueMessage(new TestMessage(1));
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
