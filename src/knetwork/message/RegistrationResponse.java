package knetwork.message;

import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public class RegistrationResponse extends Message {
	private int registeredClientId;
	
	public RegistrationResponse(int registeredClientId) {
		this.registeredClientId = registeredClientId;
	}
	
	public int getRegisteredClientId() {
		return this.registeredClientId;
	}
	
	@Override
	protected byte[] generateDerivedMessageToBytes() {
		int totalBytes = 4 * 2;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.REG_RESPONSE.getValue());
		buffer.putInt(registeredClientId);
		
		return buffer.array();
	}
	
	public static Message constructFromByteBuffer(ByteBuffer buffer) {
		int clientId = buffer.getInt();
		
		RegistrationResponse message = new RegistrationResponse(clientId);
		
		return message;
	}
}
