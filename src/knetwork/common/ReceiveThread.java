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
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;

public class ReceiveThread extends Thread {
	private BlockingQueue<Message> inMessages;
	private BlockingQueue<AckMessage> inAcknowledgements;
	
	private DatagramSocket localSocket;
	private Map<Integer, Integer> senderSequenceNumbers;
	
	private Set<String> reliablyReceivedMessages;
	private BaseNetworkingManager netManager;
	private MessageFactory messageFactory;
	
	public ReceiveThread(BaseNetworkingManager netManager, MessageFactory messageFactory, BlockingQueue<Message> inMessages, BlockingQueue<AckMessage> inAcknowledgements) {
		this.netManager = netManager;
		this.inMessages = inMessages;
		this.inAcknowledgements = inAcknowledgements;
		this.messageFactory = messageFactory;
		
		reliablyReceivedMessages = new HashSet<String>();
		senderSequenceNumbers = new HashMap<Integer, Integer>();
	}
	
	public void setSocket(DatagramSocket localSocket) {
		this.localSocket = localSocket;
	}
	
	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	private void main() throws IOException {
		while (true) {
			byte[] data = new byte[Constants.MAX_UDP_BYTE_READ_SIZE];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);
			
			Message message = messageFactory.buildMessageFromPacket(packet);
			
			if (message == null) {
				continue;
			}
			
			if (message instanceof AckMessage) {
				AckMessage ackMessage = (AckMessage)message;
				inAcknowledgements.add(ackMessage);
				Logger.log("Received ACK| for message " + ackMessage.getAckMsgId());
				continue;
			}
			
	        boolean messageOkay = false;
			int senderId = message.getSenderId();
	        int seqNumber = message.getSeqNumber();
	        Integer prevSeqNumber = senderSequenceNumbers.get(senderId);
	        
	        if (senderId == Constants.UNASSIGNED_CLIENT_ID) {
	        	// Client is registering for the first time, so they have no senderId
	        	messageOkay = true;
	        } else if (message.isReliable() && !reliablyReceivedMessages.contains(message.hashKey())) {
				// Process the message only if we've never received it before
				messageOkay = true;
				reliablyReceivedMessages.add(message.hashKey());
				netManager.sendMessageAcknowledgement(message);
			} else {
		        // TODO: Handling overflowing sequence numbers
				// Non reliable messages are only processed if it's more recent than the last message
				if (prevSeqNumber == null || prevSeqNumber.intValue() < seqNumber) {
					messageOkay = true;
			        senderSequenceNumbers.put(senderId, seqNumber);
				}
			}
			
			if (messageOkay) {
				inMessages.add(message);
				Logger.log("--- RECEIVE [" + message.getSenderId() + " -> " + message.getReceiverId() + "]| " + message.getMessageId() + " [SIZE: " + packet.getLength() + "]");
			}
		}
	}
	
	public void interrupt() {
		super.interrupt();
		if (localSocket != null) {
		    localSocket.close();
		}
	}
	
	public void run() {
		try {
			main();
		} catch (SocketException e) {
			Logger.error("[Receive Thread] " + e.toString());
		} catch (IOException e) {
			Logger.error("[Receive Thread] " + e.toString());
		} catch (Exception e) {
			Logger.error("[Receive Thread] " + e.toString());
		}
	}
}
