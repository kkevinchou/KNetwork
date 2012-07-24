package network.server;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import network.message.Message;

public class ClientInfo {
	Socket socket;
	BlockingQueue<Message> outMessages;
	
	public ClientInfo(Socket socket) {
		this.socket = socket;
		outMessages = new ArrayBlockingQueue<Message>(100); 
	}
}
