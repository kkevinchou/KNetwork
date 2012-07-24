package network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import network.message.Message;

public class ServerNetworkManager {
	private ServerSocket serverSocket = null;
	private ListenerThread listeningThread;
	private BlockingQueue<Message> inMessages = null;
	private ConcurrentMap<Integer, BlockingQueue<Message>> outMessagesMap = null;
	private ConcurrentMap<Integer, ClientInfo> clients;
	
	public ServerNetworkManager(int port) throws IOException {
		serverSocket = new ServerSocket(9001);
		serverSocket.setSoTimeout(2000);
		
		inMessages = new ArrayBlockingQueue<Message>(100);
		outMessagesMap = new ConcurrentHashMap<Integer, BlockingQueue<Message>>();
		clients = new ConcurrentHashMap<Integer, ClientInfo>();
	}
	
	public void listen() throws IOException, InterruptedException {
		listeningThread = new ListenerThread(serverSocket, 2, clients);
		// begin waiting for client connections
		listeningThread.start();

		listeningThread.join();
		serverSocket.close();
	}
}
