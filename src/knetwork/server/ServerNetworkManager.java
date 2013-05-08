package knetwork.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import knetwork.Constants;
import knetwork.common.BaseNetworkingManager;
import knetwork.common.Helper;
import knetwork.message.*;
import knetwork.message.Message.MessageType;
import knetwork.threads.SendThread;

public class ServerNetworkManager extends BaseNetworkingManager {
	private DatagramSocket socket;
	private ReceiveThread receiveThread;
	private ConcurrentMap<Integer, SendThread> clientSendThreads;
	
	public ServerNetworkManager() {
		super(Constants.SERVER_IN_QUEUE_SIZE);
	}
	
	public boolean waitForRegistrations(int port, int numRegistrations) {
		try {
			socket = new DatagramSocket(port);
			clientSendThreads = new ConcurrentHashMap<Integer, SendThread>();
			
			int nextClientId = 0;
			int numCurrentRegistrations = 0;
			
			while (numCurrentRegistrations < numRegistrations) {
				try {
					boolean success = registerUser(nextClientId);
					
					if (success == true) {
						nextClientId++;
						numCurrentRegistrations++;
					}
				} catch(Exception e) {
					System.out.println("[ServerNetworkManager] failed to register user: " + e);
				}
			}
			
			startSendThreads();
			
			List<Integer> clientIds = new ArrayList<Integer>();
			for (int i = 0; i < numRegistrations; i++) {
				clientIds.add(i);
			}
			
			receiveThread = new ReceiveThread(socket, inMessages, inAcknowledgements);
			receiveThread.start();
			
			return true;
		} catch (Exception e) {
		}
		
		return false;
	}

	private boolean registerUser(int clientId) throws IOException {
		byte[] recvData = new byte[Constants.MAX_UDP_BYTE_READ_SIZE];
		DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
		socket.receive(recvPacket);
		
		Message message = Helper.getMessageFromPacket(recvPacket);
		if (message == null || message.getMessageType() != MessageType.RegistrationRequest) {
			return false;
		}
    	
		String clientIp = recvPacket.getAddress().getHostAddress();
		int clientPort = recvPacket.getPort();
		sendRegistrationResponse(clientIp, clientPort, clientId);

		clientSendThreads.put(clientId, new SendThread(clientIp, clientPort, socket));
		
		InetAddress clientAddress = InetAddress.getByName(clientIp);
        System.out.println("[ServerNetworkManager] Registered User - " + clientAddress + " " + clientPort);
        
        return true;
	}
	
	private void sendRegistrationResponse(String clientIp, int clientPort, int clientId) throws IOException {
		RegistrationResponse registrationResponse = new RegistrationResponse(clientId);
        registrationResponse.setSenderId(Constants.SERVER_SENDER_ID);
        
        byte[] sendData = Helper.convertMessageToByteArray(registrationResponse);
    	InetAddress clientAddress = InetAddress.getByName(clientIp);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        socket.send(sendPacket);
	}
	
	private void startSendThreads() {
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.start();
		}
	}
	
	protected void sendMessageAcknowledgement(Message m) {
		send(m.getSenderId(), new AckMessage(m));
	}
	
	public void send(int clientId, Message m) {
		m.setReceiverId(clientId);
		m.setSenderId(Constants.SERVER_SENDER_ID);
		SendThread sendThread = clientSendThreads.get(clientId);
		sendThread.queueMessage(m);
	}
	
	public void send_reliable(int clientId, Message m) {
		m.setReceiverId(clientId);
		m.reliable = true;
		send(clientId, m);
		outAcknowledgements.put(m.getMessageId(), m);
	}
	
	public void broadcast(Message m) {
		m.setSenderId(Constants.SERVER_SENDER_ID);
		
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.queueMessage(m);
		}
	}
	
	public void disconnect() {
		receiveThread.interrupt();
		
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			SendThread sendThread = entry.getValue();
			sendThread.interrupt();
		}
		
		try {
			receiveThread.join();
			for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
				SendThread sendThread = entry.getValue();
				sendThread.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void reSendReliableMessage(Message m) {
		send(m.getReceiverId(), m);
	}
}
