package knetwork.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -4346009386669442875L;

	public enum MessageType { RegistrationResponse, RegistrationRequest, Big, Test };
	
	public static int nextSeqNumber = 0;
	private int sendertId;
	protected int seqNumber;
	
	public Message() {
		seqNumber = nextSeqNumber++;
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
	
	public abstract  MessageType getMessageType();
}
