package knetwork.server;

import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import knetwork.common.ReceiveThread;
import knetwork.message.Message;

public class ServerReceiveThread extends ReceiveThread {
	public ServerReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages, List<Integer> clientIds) {
		super(localSocket, inMessages);
		
		for (Integer clientId : clientIds) {
			senderSequenceNumbers.put(clientId, 0);
		}
	}
}
