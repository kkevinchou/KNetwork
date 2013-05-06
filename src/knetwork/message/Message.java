package knetwork.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -4346009386669442875L;

	public enum MessageType { RegistrationResponse, RegistrationRequest, Big, Test, Acknowledge };
	
	public static int nextMessageId = 0;
	public static int nextSeqNumber = 0;
	private int messageId;
	private int sendertId;
	protected int seqNumber;
	
	public Message() {
		messageId = nextMessageId++;
		seqNumber = nextSeqNumber++;
		nextSeqNumber = nextSeqNumber % Integer.MAX_VALUE;
	}
	
	public int getSenderId() {
		return sendertId;
	}

	public void setSenderId(int clientId) {
		this.sendertId = clientId;
	}
	
	public int getSeqNumber() {
		return seqNumber;
	}
	
	public int getMessageId() {
		return messageId;
	}
	
	public abstract MessageType getMessageType();
}
