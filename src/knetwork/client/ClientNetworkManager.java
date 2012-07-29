package knetwork.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.*;
import knetwork.threads.*;


public class ClientNetworkManager {
	private DatagramSocket socket;
	private SendThread sendThread;
	private ReceiveThread receiveThread;
	private ClientRegistrationThread registrationThread;
	private BlockingQueue<Message> inMessages;
	private int clientId;
	
	public ClientNetworkManager() throws SocketException {
		socket = new DatagramSocket();
		inMessages = new ArrayBlockingQueue<Message>(KNetwork.clientInQueueSize);
		
		receiveThread = new ClientReceiveThread(socket, inMessages);
	}
	
	public void register(String serverIp, int serverPort) throws InterruptedException {
		registrationThread = new ClientRegistrationThread(serverIp, serverPort, socket);
		registrationThread.start();
		registrationThread.join();

		clientId = registrationThread.getClientId();
		System.out.println("Registered with clientId = " + clientId);

		sendThread = new SendThread(serverIp, serverPort, socket);
		sendThread.start();
		
		receiveThread.start();
	}

	public void send(Message m) throws IOException, ClassNotFoundException {
		m.setSenderId(clientId);
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