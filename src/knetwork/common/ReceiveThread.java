package knetwork.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import knetwork.Constants;
import knetwork.message.*;
import knetwork.message.Message.MessageType;

public class ReceiveThread extends Thread {
	private BlockingQueue<Message> inMessages;
	private BlockingQueue<Message> inAcknowledgements;
	
	private DatagramSocket localSocket;
	private Map<Integer, Integer> senderSequenceNumbers;
	public boolean executionFinished;
	
	private Set<Message> reliablyReceivedMessages;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages, BlockingQueue<Message> inAcknowledgements) {
		this.localSocket = localSocket;
		this.inMessages = inMessages;
		this.inAcknowledgements = inAcknowledgements;
		
		reliablyReceivedMessages = new HashSet<Message>();
		senderSequenceNumbers = new HashMap<Integer, Integer>();
	}
	
	private void main() throws IOException {
		while (true) {
			byte[] data = new byte[Constants.MAX_UDP_BYTE_READ_SIZE];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);

			Message message = Helper.getMessageFromPacket(packet);
			if (message == null) {
				continue;
			}
			
			if (message.getMessageType() == MessageType.Acknowledge) {
				inAcknowledgements.add(message);
				System.out.println("[Receive Thread] Received ACK");
			} else {
				if (message.reliable && reliablyReceivedMessages.contains(message)) {
					// Received a duplicate reliable message
				} else {
					int senderId = message.getSenderId();
			        int seqNumber = message.getSeqNumber();
			        
			        // For now, we don't check for valid sequence numbers
			        // I haven't thought of a good way to handle wrapping around yet
			        
			        senderSequenceNumbers.put(senderId, seqNumber);
			        inMessages.add(message);
			        System.out.println("[Receive Thread] Received message [" + senderId + "]| " + "size = " + packet.getLength() + ", seq# = " + seqNumber);
				}
				
			}
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
		} catch (IOException e) {
			System.out.println("[Receive Thread] " + e.toString());
		}
	}
}
