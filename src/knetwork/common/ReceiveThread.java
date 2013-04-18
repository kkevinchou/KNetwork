package knetwork.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import knetwork.Constants;
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
		while (true) {
			byte[] data = new byte[Constants.MAX_UDP_BYTE_READ_SIZE];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);

			Message message = Helper.getMessageFromPacket(packet);
			if (message == null) {
				continue;
			}
	        
	        int senderId = message.getSenderId();
	        int seqNumber = message.getSeqNumber();
	        
	        // For now, we don't check for valid sequence numbers
	        // I haven't thought of a good way to handle wrapping around yet
	        
	        senderSequenceNumbers.put(senderId, seqNumber);
	        inMessages.add(message);
	        System.out.println("[Receive Thread] Received message [" + senderId + "]| " + "size = " + packet.getLength() + ", seq# = " + seqNumber);
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
