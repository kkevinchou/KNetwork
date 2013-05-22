package knetwork.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import knetwork.Constants;
import knetwork.common.BaseNetworkingManager;
import knetwork.common.Helper;
import knetwork.message.*;
import knetwork.threads.ReceiveThread;
import knetwork.threads.SendThread;

public class ServerNetworkManager extends BaseNetworkingManager {
	private DatagramSocket socket;
	private ReceiveThread receiveThread;
	private ConcurrentMap<Integer, SendThread> sendThreads;
	
	public ServerNetworkManager() {
		super(Constants.SERVER_IN_QUEUE_SIZE);
	}
	
	public boolean waitForRegistrations(int port, int numRegistrations) {
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e1) {
			e1.printStackTrace();
			return false;
		}
		
		receiveThread = new ReceiveThread(this, socket, inMessages, inAcknowledgements);
		receiveThread.start();
		
		sendThreads = new ConcurrentHashMap<Integer, SendThread>();
		
		int nextClientId = 1;
		int numSuccessfulRegistrations = 0;
		
		while (numSuccessfulRegistrations < numRegistrations) {
			boolean success;
			
			try {
				success = registerUser(nextClientId);
			} catch (IOException e) {
				e.printStackTrace();
				success = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				success = false;
			}
			
			if (success) {
				nextClientId++;
				numSuccessfulRegistrations++;
			}
		}
		
		return true;
	}

	private boolean registerUser(int clientId) throws IOException, InterruptedException {
		Message message = recv_blocking();
		
		if (!(message instanceof RegistrationRequest)) {
			return false;
		}
		
		DatagramPacket packet = ((RegistrationRequest)message).getPacket();
    	
		String clientIp = packet.getAddress().getHostAddress();
		int clientPort = packet.getPort();
		
		SendThread sendThread = new SendThread(clientIp, clientPort, socket);
		sendThread.start();
		sendThreads.put(clientId, sendThread);
		
		send(new RegistrationResponse(clientId));
		
		InetAddress clientAddress = InetAddress.getByName(clientIp);
		Helper.log("[ServerNetworkManager] Registered User - " + clientAddress + " " + clientPort);
        
        return true;
	}
	
	public void sendMessageAcknowledgement(Message m) {
		send(new AckMessage(m));
	}
	
	public void send(Message m) {
		if (m.getReceiverId() == Constants.SERVER_ID) {
			System.out.println("WARNING - SENDING MESSAGE FROM SERVER TO SERVER");
		}
		
		m.setSenderId(Constants.SERVER_ID);
		SendThread sendThread = sendThreads.get(m.getReceiverId());
		sendThread.queueMessage(m);
	}
	
	public void send_reliable(Message message) {
		message.setReliable(true);
		send(message);
		outAcknowledgements.put(message.getMessageId(), message);
	}
	
	public void send(int clientId, Message message) {
		message.setReceiverId(clientId);
		send(message);
	}
	
	public void send_reliable(int clientId, Message message) {
		message.setReceiverId(clientId);
		send_reliable(message);
	}
	
	public void broadcast(Message message) {
		for (Integer clientId : sendThreads.keySet()) {
			send(clientId, message);
		}
	}
	
	public void broadcast_reliable(Message message) {
		for (Integer clientId : sendThreads.keySet()) {
			send_reliable(clientId, message);
		}
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		receiveThread.interrupt();
		
		for (ConcurrentMap.Entry<Integer, SendThread> entry : sendThreads.entrySet()) {
			SendThread sendThread = entry.getValue();
			sendThread.interrupt();
		}
		
		try {
			receiveThread.join();
			for (ConcurrentMap.Entry<Integer, SendThread> entry : sendThreads.entrySet()) {
				SendThread sendThread = entry.getValue();
				sendThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void reSendReliableMessage(Message message) {
		send(message);
	}
}
