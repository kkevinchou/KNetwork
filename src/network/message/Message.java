package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	public enum MessageType { RegistrationResponse };
	
	public abstract MessageType getMessageType();
}
