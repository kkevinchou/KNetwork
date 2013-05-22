package knetwork.threads;

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
import knetwork.common.Helper;
import knetwork.message.*;

public class ReceiveThread extends Thread {
	private BlockingQueue<Message> inMessages;
	private BlockingQueue<Message> inAcknowledgements;
	
	private DatagramSocket localSocket;
	private Map<Integer, Integer> senderSequenceNumbers;
	public boolean executionFinished;
	
	private Set<String> reliablyReceivedMessages;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages, BlockingQueue<Message> inAcknowledgements) {
		this.localSocket = localSocket;
		this.inMessages = inMessages;
		this.inAcknowledgements = inAcknowledgements;
		
		reliablyReceivedMessages = new HashSet<String>();
		senderSequenceNumbers = new HashMap<Integer, Integer>();
	}
	
	private void main() throws IOException {
		while (true) {
			byte[] data = new byte[Constants.MAX_UDP_BYTE_READ_SIZE];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			localSocket.receive(packet);
			
			Message message = MessageFactory.buildMessage(packet.getData(), packet.getLength());
			
			if (message == null) {
				continue;
			}
			
			if (message instanceof AckMessage) {
				AckMessage ack = (AckMessage)message;
				inAcknowledgements.add(message);
				Helper.log("Received ACK| for message " + ack.getAckMsgId());
			} else {
		        boolean messageOkay = false;
				int senderId = message.getSenderId();
		        int seqNumber = message.getSeqNumber();
		        Integer prevSeqNumber = senderSequenceNumbers.get(senderId);
		        
				if (message.isReliable()) {
					if (!reliablyReceivedMessages.contains(message.hashKey())) {
						messageOkay = true;
						reliablyReceivedMessages.add(message.hashKey());
					}
				} else {
			        // TODO: Handling overflowing sequence numbers
					if (prevSeqNumber == null || prevSeqNumber.intValue() < seqNumber) {
						messageOkay = true;
				        senderSequenceNumbers.put(senderId, seqNumber);
					}
				}
				
				if (messageOkay) {
					inMessages.add(message);
					Helper.log("--- RECEIVE [" + message.getSenderId() + " -> " + message.getReceiverId() + "]| " + message.getMessageId() + " [SIZE: " + packet.getLength() + "]");
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
//			Helper.log("[Receive Thread] " + e.toString());
		} catch (IOException e) {
			Helper.log("[Receive Thread] " + e.toString());
		} catch (Exception e) {
			Helper.log("[Receive Thread] " + e.toString());
		}
	}
}
