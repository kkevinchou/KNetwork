package knetwork.threads;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.Message;
import knetwork.message.TestMessage;
import knetwork.message.Message.MessageType;

public class ReceiveThread extends Thread {
	BlockingQueue<Message> inMessages;
	private boolean finished;
	protected DatagramSocket localSocket;
	protected int lastSeqNumber;
	Map<Integer, Integer> senderSequenceNumbers;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages, List<Integer> senderIds) {
		this.inMessages = inMessages;
		this.finished = false;
		this.localSocket = localSocket;
		lastSeqNumber = 0;
		senderSequenceNumbers = new HashMap<Integer, Integer>();
		
		for (Integer senderId : senderIds) {
			senderSequenceNumbers.put(senderId, 0);
		}
	}
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages, int senderId) {
		this(localSocket, inMessages, Arrays.asList(senderId));
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] data = null;
		DatagramPacket packet = null;
		ObjectInputStream iStream = null;
		Message message = null;
		int seqNumber = 0;
		int lastSeqNumber = 0;
		int senderId = 0;
		
		while (!finished) {
			data = new byte[KNetwork.maxUdpByteReadSize];
			packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);
			iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

	        try {
				message = (Message) iStream.readObject();
			} catch (ClassCastException e) {
				System.out.println("[Receive Thread] could not cast received object into a Message");
				continue;
			} catch (EOFException e) {
				System.out.println("[Receive Thread] Could not read enough data to form an object");
				continue;
			}
	        
	        senderId = message.getSenderId();
	        lastSeqNumber = senderSequenceNumbers.get(senderId);
	        seqNumber = message.getSeqNumber();
	        if (seqNumber <= lastSeqNumber) {
	        	System.out.println("[Receive Thread] Received a message out of order");
	        	continue;
	        }
	        senderSequenceNumbers.put(message.getSenderId(), seqNumber);
	        
	        System.out.println("[Receive Thread] Received message| " + "size = " + packet.getLength() + ", sequence number = " + message.getSeqNumber());
	        
        	iStream.close();
		}
	}
	
	public void terminate() {
		this.finished = true;
		this.interrupt();
	}
	
	public void run() {
		try {
			main();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
