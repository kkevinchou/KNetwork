package network.message;

public class RegistrationRequest extends Message {
	private static final long serialVersionUID = 6222482642687719179L;
	
	public RegistrationRequest() {
	}

	public MessageType getMessageType() {
		return MessageType.RegistrationRequest;
	}
}
