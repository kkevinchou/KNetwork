package knetwork.client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.common.ReceiveThread;
import knetwork.common.SendThread;
import knetwork.message.*;
import knetwork.message.Message.MessageType;


public class ClientNetworkManager {
	private DatagramSocket socket;
	private SendThread sendThread;
	private ReceiveThread receiveThread;
	private BlockingQueue<Message> inMessages;
	private int clientId;
	
	private static final int REGISTRATION_TIMEOUT = 500;
	
	public ClientNetworkManager() throws SocketException {
		socket = new DatagramSocket();
		inMessages = new ArrayBlockingQueue<Message>(KNetwork.clientInQueueSize);
		receiveThread = new ClientReceiveThread(socket, inMessages);
	}
	
	public void register(String serverIp, int serverPort) throws InterruptedException {
		sendThread = new SendThread(serverIp, serverPort, socket);
		sendThread.start();
		sendThread.queueMessage(new RegistrationRequest());

		receiveThread.start();
		
		Message m = recv();
		while (m == null || m.getMessageType() != MessageType.RegistrationResponse) {
			Thread.sleep(REGISTRATION_TIMEOUT);
			m = recv();
		}
		
		RegistrationResponse regResponse = (RegistrationResponse)m;
		clientId = regResponse.getRegisteredClientId();
		
		System.out.println("Registered with clientId = " + clientId);
	}

	public void send(Message m) throws IOException, ClassNotFoundException {
		m.setSenderId(clientId);
        sendThread.queueMessage(m);
	}
	
	public Message recv() {
		return inMessages.poll();
	}
	
	public void disconnect() {
		receiveThread.terminate();
		sendThread.terminate();
		socket.close();
		
		try {
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}