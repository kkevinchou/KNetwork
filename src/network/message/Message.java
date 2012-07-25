package network.message;

import java.io.Serializable;

public abstract class Message implements Serializable {
	public enum MessageType { RegistrationResponse, RegistrationRequest };
	
	public abstract MessageType getMessageType();
}
