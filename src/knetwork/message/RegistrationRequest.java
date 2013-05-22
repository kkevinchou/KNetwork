package knetwork.message;

import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public class RegistrationRequest extends Message {
	@Override
	protected byte[] generateDerivedMessageBytes() {
		int totalBytes = 1 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.REG_REQUEST.getValue());
		
		return buffer.array();
	}
	
	// The buffer argument is there just to preserve the same interface between messages
	public static Message constructFromByteBuffer(ByteBuffer buffer) {
		RegistrationRequest message = new RegistrationRequest();
		
		return message;
	}
}
