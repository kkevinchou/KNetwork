package knetwork.server;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import knetwork.KNetwork;
import knetwork.message.*;
import knetwork.threads.*;

public class ServerNetworkManager {
	private DatagramSocket socket;
	private ReceiveThread receiveThread;
	private ServerRegistrationThread registrationThread;
	private ConcurrentMap<Integer, SendThread> clientSendThreads;
	private BlockingQueue<Message> inMessages;
	
	public ServerNetworkManager(int port) throws SocketException {
		socket = new DatagramSocket(port);
		inMessages = new ArrayBlockingQueue<Message>(KNetwork.serverInQueueSize);
	}
	
	public void waitForRegistrations(int numRegistrations) throws InterruptedException {
		registrationThread = new ServerRegistrationThread(socket, numRegistrations);
		registrationThread.start();
		registrationThread.join();
		
		clientSendThreads = registrationThread.getClientSendThreads();
		startSendThreads(clientSendThreads);
		
		receiveThread = new ServerReceiveThread(socket, inMessages, registrationThread.getClientIds());
		receiveThread.start();
	}
	
	private void startSendThreads(ConcurrentMap<Integer, SendThread> sendThreads) {
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : sendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.start();
		}
	}
	
	public Message recv() throws IOException, ClassNotFoundException {
		return inMessages.poll();
	}
	
	public void send(int clientId, Message m) {
		m.setSenderId(KNetwork.serverSenderId);
		SendThread sendThread = clientSendThreads.get(clientId);
		sendThread.queueMessage(m);
	}
	
	public void broadcast(Message m) {
		m.setSenderId(KNetwork.serverSenderId);
		
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.queueMessage(m);
		}
	}
	
	public void disconnect() {
		receiveThread.terminate();
		
		SendThread sendThread = null;
		for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
			sendThread = entry.getValue();
			sendThread.terminate();
		}
		
		try {
			receiveThread.join();
			for (ConcurrentMap.Entry<Integer, SendThread> entry : clientSendThreads.entrySet()) {
				sendThread = entry.getValue();
				sendThread.join();
			}
		} catch (InterruptedException e) {
		}
	}
}
