package knetwork.message.messages;

import java.nio.ByteBuffer;

import knetwork.message.MessageBody;

public class RegistrationResponse extends Message {
	private int registeredClientId;
	public static final int MESSAGE_TYPE = 1;
	
	public RegistrationResponse(int registeredClientId) {
		super(MESSAGE_TYPE);
		setReceiverId(registeredClientId);
		this.registeredClientId = registeredClientId;
	}
	
	public int getRegisteredClientId() {
		return this.registeredClientId;
	}
	
	@Override
	protected byte[] generateMessageBodyBytes() {
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
