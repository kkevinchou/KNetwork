package knetwork.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import knetwork.Constants;
import knetwork.common.Helper;
import knetwork.common.ReceiveThread;
import knetwork.common.SendThread;
import knetwork.message.*;
import knetwork.message.Message.MessageType;

public class ServerNetworkManager {
	private DatagramSocket socket;
	private ReceiveThread receiveThread;
	private ConcurrentMap<Integer, SendThread> clientSendThreads;
	private BlockingQueue<Message> inMessages;
	
	public ServerNetworkManager(int port) throws SocketException {
		socket = new DatagramSocket(port);
		inMessages = new ArrayBlockingQueue<Message>(Constants.SERVER_IN_QUEUE_SIZE);
		clientSendThreads = new ConcurrentHashMap<Integer, SendThread>();
	}
	
	public void waitForRegistrations(int numRegistrations) throws InterruptedException {
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
		
		receiveThread = new ReceiveThread(socket, inMessages);
		receiveThread.start();
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
        
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    	ObjectOutputStream oStream = new ObjectOutputStream(bStream);
        oStream.writeObject(registrationResponse);
        
        byte[] sendData = bStream.toByteArray();
    	InetAddress clientAddress = InetAddress.getByName(clientIp);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        socket.send(sendPacket);

        bStream.close();
        oStream.close();
	}
	
	private void startSendThreads() {
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.start();
		}
	}
	
	public Message recv() throws IOException, ClassNotFoundException {
		return inMessages.poll();
	}
	
	public void send(int clientId, Message m) {
		m.setSenderId(Constants.SERVER_SENDER_ID);
		SendThread sendThread = clientSendThreads.get(clientId);
		sendThread.queueMessage(m);
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
}
