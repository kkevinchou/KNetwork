package knetwork.message;

import java.io.Serializable;

import knetwork.Constants;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -4346009386669442875L;

	public enum MessageType { RegistrationResponse, RegistrationRequest, Big, Test, Acknowledge };
	
	public static int nextMessageId = 0;
	public static int nextSeqNumber = 0;
	private int messageId;
	private int sendertId;
	private int receiverId;
	protected int seqNumber;
	public boolean reliable;
	
	public Message() {
		receiverId = Constants.SERVER_SENDER_ID;
		messageId = nextMessageId++;
		seqNumber = nextSeqNumber++;
		reliable = false;
		nextSeqNumber = nextSeqNumber % Integer.MAX_VALUE;
	}
	
	public int getSenderId() {
		return sendertId;
	}

	public void setSenderId(int clientId) {
		this.sendertId = clientId;
	}
	
	public int getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	
	public int getSeqNumber() {
		return seqNumber;
	}
	
	public int getMessageId() {
		return messageId;
	}
	
	public abstract MessageType getMessageType();
}
