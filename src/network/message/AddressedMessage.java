package network.message;

public class AddressedMessage {
	int clientId;
	Message message;
	
	public AddressedMessage(int clientId, Message message) {
		this.clientId = clientId;
		this.message = message;
	}

	public int getClientId() {
		return clientId;
	}

	public Message getMessage() {
		return message;
	}
}
