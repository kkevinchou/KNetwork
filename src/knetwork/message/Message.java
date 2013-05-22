package knetwork.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import knetwork.Constants;
import knetwork.message.MessageTypes.MessageType;

public abstract class Message {
	public static int nextMessageId = 0;
	public static int nextSeqNumber = 0;
	
	private int messageId;
	private int senderId;
	private int receiverId;
	private int seqNumber;
	private int reliable;

	private byte[] generateFooterBytes() {
		int footerSize = 5 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(footerSize);
		buffer = buffer.putInt(messageId).putInt(senderId).putInt(receiverId);
		buffer = buffer.putInt(seqNumber).putInt(reliable);
		
		return buffer.array();
	}
	
	protected abstract byte[] generateDerivedMessageToBytes();
	
	private void init() {
		receiverId = Constants.SERVER_ID;
		messageId = nextMessageId++;
		seqNumber = nextSeqNumber++;
		reliable = 0;
		nextSeqNumber = nextSeqNumber % Integer.MAX_VALUE;
	}
	
	public Message() {
		init();
	}
	
	public Message(int receiverId) {
		init();
		this.receiverId = receiverId; 
	}
	
	public final byte[] convertMessageToBytes() {
		byte[] derivedMessageBytes = generateDerivedMessageToBytes();
		byte[] footerBytes = generateFooterBytes();
		
		int a = derivedMessageBytes.length;
		int b = footerBytes.length;
		
		byte[] messageBytes = new byte[derivedMessageBytes.length + footerBytes.length];
		
		for (int i = 0; i < derivedMessageBytes.length; i++) {
			messageBytes[i] = derivedMessageBytes[i];
		}
		
		for (int i = 0; i < footerBytes.length; i++) {
			messageBytes[i + derivedMessageBytes.length] = footerBytes[i];
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
	
	protected static void setMessageFooterFromByteBuffer(Message message, ByteBuffer buffer) {
		int messageId = buffer.getInt();
		int senderId = buffer.getInt();
		int receiverId = buffer.getInt();
		int seqNumber = buffer.getInt();
		int reliable = buffer.getInt();
		
		message.messageId = messageId;
		message.senderId = senderId;
		message.receiverId = receiverId;
		message.seqNumber = seqNumber;
		message.reliable = reliable;
	}
}
