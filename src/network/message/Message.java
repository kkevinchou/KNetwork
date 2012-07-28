package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	public enum MessageType { RegistrationResponse, RegistrationRequest, Other };
	
	public static long sequenceNumber = 0;
	public static final int MAX_SIZE = 10000;
	private int clientId;
	
	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	protected Message() {
		sequenceNumber++;
	}
	
	public long getSequenceNumber() {
		return sequenceNumber;
	}
	
	public abstract  MessageType getMessageType();
}
