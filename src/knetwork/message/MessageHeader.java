package knetwork.message;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import knetwork.Constants;

public class MessageHeader {
	private int protocolId;
	private int messageType;

	private int messageId;
	private int senderId;
	private int receiverId;
	private int seqNumber;
	private int reliable;
	
	private void init() {
		protocolId = messageType = messageId = senderId = receiverId = seqNumber = reliable = -1;
	}
	
	public MessageHeader(DatagramPacket packet) {
		byte[] bytes = packet.getData();
		int length = packet.getLength();
		
		if (length < Constants.MESSAGE_HEADER_SIZE) {
			init();
			return;
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(length).put(bytes, 0, length);
		buffer.position(0);
		
		protocolId = buffer.getInt();
		messageType = buffer.getInt();
		messageId = buffer.getInt();
		senderId = buffer.getInt();
		receiverId = buffer.getInt();
		seqNumber = buffer.getInt();
		reliable = buffer.getInt();
	}
	
	public MessageHeader() {
		protocolId = messageType = messageId = senderId = receiverId = seqNumber = reliable = -1;
	}
	
	public int getProtocolId() {
		return protocolId;
	}

	public int getMessageType() {
		return messageType;
	}

	public int getMessageId() {
		return messageId;
	}

	public int getSenderId() {
		return senderId;
	}

	public int getReceiverId() {
		return receiverId;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public int isReliable() {
		return reliable;
	}
	
	public String hashKey() {
		return messageId + "_" + senderId;
	}
}
