package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import network.message.AddressedMessage;
import network.message.Message;

public class ServerNetworkManager {
	private ServerSocket serverSocket = null;
	private ConnectionThread listeningThread = null;
	private BlockingQueue<Message> inMessages = null;
	private BlockingQueue<AddressedMessage> outMessages = null;
	private ConcurrentMap<Integer, Client> clients = null;
	
	public ServerNetworkManager(int port) throws IOException {
		serverSocket = new ServerSocket(9001);
		serverSocket.setSoTimeout(2000);
		
		inMessages = new ArrayBlockingQueue<Message>(100);
		outMessages = new ArrayBlockingQueue<AddressedMessage>(100);
		clients = new ConcurrentHashMap<Integer, Client>();
	}

	public void listen() throws IOException, InterruptedException {
		listeningThread = new ConnectionThread(2, serverSocket, clients, inMessages);
		listeningThread.start();

		listeningThread.join();
		serverSocket.close();
	}
	
	// May need to clone the message to avoid concurrent modification
	public void broadcast(Message m) {
		int clientId;
		for (ConcurrentMap.Entry<Integer, Client> entry : clients.entrySet()) {
			clientId = entry.getKey();
		    outMessages.add(new AddressedMessage(clientId, m));
		}
	}
	
	// May need to clone the message to avoid concurrent modification
	public void send(int clientId, Message m) {
		outMessages.add(new AddressedMessage(clientId, m));
	}
}
