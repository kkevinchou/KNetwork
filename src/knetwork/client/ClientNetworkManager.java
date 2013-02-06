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
	
	public ClientNetworkManager() throws SocketException {
		socket = new DatagramSocket();
		inMessages = new ArrayBlockingQueue<Message>(KNetwork.clientInQueueSize);
	}
	
	public void register(String serverIp, int serverPort) throws InterruptedException {
		sendThread = new SendThread(serverIp, serverPort, socket);
		sendThread.queueMessage(new RegistrationRequest());
		sendThread.start();

		receiveThread = new ClientReceiveThread(socket, inMessages);
		receiveThread.start();
		
		Message m = null;
		
		try {
			m = recv_blocking();
			while (m.getMessageType() != MessageType.RegistrationResponse) {
				m = recv_blocking();
			}
		} catch (InterruptedException e) {
			System.out.println("[ClientNetworkManager] " + e.toString());
			System.out.println("[ClientNetworkManager] Registration interrupted");
			return;
		}
		
		RegistrationResponse regResponse = (RegistrationResponse)m;
		clientId = regResponse.getRegisteredClientId();
		
		System.out.println("[ClientNetworkManager] Registered with clientId = " + clientId);
	}

	public void send(Message m) throws IOException, ClassNotFoundException {
		m.setSenderId(clientId);
        sendThread.queueMessage(m);
	}
	
	public Message recv() {
		return inMessages.poll();
	}
	
	public Message recv_blocking() throws InterruptedException {
		return inMessages.take();
	}
	
	public void disconnect() {
		receiveThread.terminate();
		sendThread.terminate();
		socket.close();
		
		try {
			receiveThread.join();
			System.out.println("disconnect 1");
			sendThread.join();
			System.out.println("disconnect 2");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}