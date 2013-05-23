package knetwork.message;

import java.nio.ByteBuffer;
import knetwork.message.MessageTypes.MessageType;

public class RegistrationResponse extends Message {
	private int registeredClientId;
	
	public RegistrationResponse(int registeredClientId) {
		super(MessageType.REG_RESPONSE.getValue());
		setReceiverId(registeredClientId);
		this.registeredClientId = registeredClientId;
	}
	
	public int getRegisteredClientId() {
		return this.registeredClientId;
	}
	
	@Override
	protected byte[] generateDerivedMessageBytes() {
		int totalBytes = 1 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(registeredClientId);
		
		return buffer.array();
	}
	
	public static Message constructFromMessageBody(MessageBody body) {
		ByteBuffer buffer = body.getByteBuffer();
		
		int clientId = buffer.getInt();
		RegistrationResponse message = new RegistrationResponse(clientId);
		
		return message;
	}
}
