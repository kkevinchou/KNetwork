package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	private static final long serialVersionUID = -4346009386669442875L;

	public enum MessageType { RegistrationResponse, RegistrationRequest, Test };
	
	public static int nextSeqNumber = 0;
	public static final int MAX_SIZE = 10000;
	private int clientId;
	protected int seqNumber;
	
	public Message() {
		seqNumber = nextSeqNumber++;
	}
	
	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	public int getSeqNumber() {
		return seqNumber;
	}
	
	public abstract  MessageType getMessageType();
}
