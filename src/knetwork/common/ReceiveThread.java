package knetwork.common;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.*;

public class ReceiveThread extends Thread {
	private BlockingQueue<Message> inMessages;
	private DatagramSocket localSocket;
	protected Map<Integer, Integer> senderSequenceNumbers;
	public boolean executionFinished;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages) {
		this.localSocket = localSocket;
		this.inMessages = inMessages;
		senderSequenceNumbers = new HashMap<Integer, Integer>();
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] data = null;
		DatagramPacket packet = null;
		ObjectInputStream iStream = null;
		Message message = null;
		int seqNumber = 0;
		int senderId = 0;
		
		while (true) {
			data = new byte[KNetwork.MAX_UDP_BYTE_READ_SIZE];
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
	        seqNumber = message.getSeqNumber();
	        
	        // For now, we don't check for valid sequence numbers
	        // I haven't thought of a good way to handle wrapping around yet
	        
	        senderSequenceNumbers.put(senderId, seqNumber);
	        inMessages.add(message);
	        System.out.println("[Receive Thread] Received message [" + senderId + "]| " + "size = " + packet.getLength() + ", seq# = " + seqNumber);
	        
        	iStream.close();
		}
	}
	
	public void interrupt() {
		super.interrupt();
	    localSocket.close();
	}
	
	public void run() {
		try {
			main();
		} catch (SocketException e) {
//			System.out.println("[Receive Thread] " + e.toString());
		} catch (ClassNotFoundException e) {
			System.out.println("[Receive Thread] " + e.toString());
		} catch (IOException e) {
			System.out.println("[Receive Thread] " + e.toString());
		}
	}
}
