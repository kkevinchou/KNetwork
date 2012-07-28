package network.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import network.message.*;
import network.threads.*;

public class ClientNetworkManager {
	private DatagramSocket socket;
	private SendThread sendThread;
	private ReceiveThread receiveThread;
	private ClientRegistrationThread registrationThread;
	private BlockingQueue<Message> inMessages;
	private int clientId;
	
	public ClientNetworkManager(String serverIp, int serverPort) throws SocketException {
		socket = new DatagramSocket();
		registrationThread = new ClientRegistrationThread(serverIp, serverPort, socket);
		sendThread = new SendThread(serverIp, serverPort, socket);

		inMessages = new ArrayBlockingQueue<Message>(100);
		receiveThread = new ReceiveThread(socket, inMessages);
	}
	
	public void register() throws InterruptedException {
		registrationThread.start();
		registrationThread.join();
		clientId = registrationThread.getClientId();
		System.out.println("Registered with clientId = " + clientId);
		
		sendThread.start();
		receiveThread.start();
	}

	public void send(Message m) throws IOException, ClassNotFoundException {
		m.setClientId(clientId);
        sendThread.queueMessage(m);
	}
	
	public Message recv() {
		return inMessages.poll();
	}
	
	public void disconnect() {
		sendThread.terminate();
		receiveThread.terminate();
		
		try {
			sendThread.join();
			receiveThread.join();
		} catch (InterruptedException e) {
		}
	}
}