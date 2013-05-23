package knetwork.message.messages;

import java.nio.ByteBuffer;

import knetwork.Constants;
import knetwork.message.MessageHeader;

public abstract class Message {
	public static int nextMessageId = 0;
	public static int nextSeqNumber = 0;
	
	private int protocolId;
	private int messageType;
	private int messageId;
	private int senderId;
	private int receiverId;
	private int seqNumber;
	private int reliable;
	
	protected Message(int messageType) {
		protocolId = Constants.PROTOCOL_ID;
		this.messageType = messageType;
		messageId = nextMessageId++;
		receiverId = Constants.SERVER_ID;
		seqNumber = nextSeqNumber++;
		reliable = 0;
	}

	private byte[] generateHeaderBytes() {
		int headerSize = Constants.MESSAGE_HEADER_SIZE;
		
		ByteBuffer buffer = ByteBuffer.allocate(headerSize);
		buffer = buffer.putInt(protocolId).putInt(messageType).putInt(messageId);
		buffer = buffer.putInt(senderId).putInt(receiverId);
		buffer = buffer.putInt(seqNumber).putInt(reliable);
		
		return buffer.array();
	}
	
	protected abstract byte[] generateDerivedMessageBytes();
	
	public final byte[] convertMessageToBytes() {
		byte[] headerBytes = generateHeaderBytes();
		byte[] derivedMessageBytes = generateDerivedMessageBytes();
		
		byte[] messageBytes = new byte[derivedMessageBytes.length + headerBytes.length];
		
		for (int i = 0; i < headerBytes.length; i++) {
			messageBytes[i] = headerBytes[i];
		}
		
		for (int i = 0; i < derivedMessageBytes.length; i++) {
			messageBytes[i + headerBytes.length] = derivedMessageBytes[i];
		}
		
		return messageBytes;
	}
	
	public final int getSenderId() {
		return senderId;
	}

	public final void setSenderId(int clientId) {
		this.senderId = clientId;
	}
	
	public final int getReceiverId() {
		return receiverId;
	}
	
	public final boolean isReliable() {
		return (reliable == 1);
	}

	public final void setReliable(boolean reliable) {
		if (reliable) {
			this.reliable = 1;
		} else {
			this.reliable = 0;
		}
	}

	public final void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	
	public final int getSeqNumber() {
		return seqNumber;
	}
	
	public final int getMessageId() {
		return messageId;
	}
	
	public static void setMessageHeader(Message message, MessageHeader header) {
		message.protocolId = header.getProtocolId();
		message.messageType = header.getMessageType();
		message.messageId = header.getMessageId();
		message.senderId = header.getSenderId();
		message.receiverId = header.getReceiverId();
		message.seqNumber = header.getSeqNumber();
		message.reliable = header.isReliable();
	}
	
	public String hashKey() {
		return messageId + " " + senderId;
	}
}
