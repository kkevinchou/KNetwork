package knetwork.message;

public class RegistrationResponse extends Message {
	private static final long serialVersionUID = 4917069840905038506L;
	private int registeredClientId;
	
	public RegistrationResponse(int registeredClientId) {
		this.registeredClientId = registeredClientId;
	}
	
	public int getRegisteredClientId() {
		return this.registeredClientId;
	}
	
	public MessageType getMessageType() {
		return MessageType.RegistrationResponse;
	}
}
