package knetwork.threads;

import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.Message;

public class ClientReceiveThread extends ReceiveThread {
	public ClientReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages) {
		super(localSocket, inMessages);
		senderSequenceNumbers.put(KNetwork.serverSenderId, 0);
	}
}
