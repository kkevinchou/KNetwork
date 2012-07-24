package network.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import network.message.*;

public class ListenerThread implements Runnable {
	private boolean finished;
	private int acceptedConnections;
	private int availableClientIds;
	private int numExpectedConnections;
	
	private Thread thread;
	private ServerSocket serverSocket;
	private ConcurrentMap<Integer, ClientInfo> clients;
	
	public ListenerThread(ServerSocket serverSocket, int numExpectedConnections, ConcurrentMap<Integer, ClientInfo> clients) throws IOException {
		finished = false;
		availableClientIds = 0;
		acceptedConnections = 0;
		thread = new Thread(this);
		
		this.serverSocket = serverSocket;
		this.clients = clients;
		this.numExpectedConnections = numExpectedConnections;
	}
	
	public void start() {
		thread.start();
	}
	
	public void join() throws InterruptedException {
		thread.join();
	}
	
	public void stop() {
		finished = true;
	}
	
	public void run() {
		while (!finished && acceptedConnections < numExpectedConnections) {
			try {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Connection accepted");
				
				ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				outputStream.writeObject(new RegistrationResponse(availableClientIds));
				clients.put(availableClientIds, new ClientInfo(clientSocket));
				
				
				acceptedConnections++;
				availableClientIds++;
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
	}
}