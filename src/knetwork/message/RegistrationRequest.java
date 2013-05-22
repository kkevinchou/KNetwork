package knetwork.message;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import knetwork.message.MessageTypes.MessageType;

public class RegistrationRequest extends Message {
	@Override
	protected byte[] generateDerivedMessageToBytes() {
		int totalBytes = 4;
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.REG_REQUEST.getValue());
		
		return buffer.array();
	}
	
	public static Message constructFromByteBuffer(ByteBuffer buffer) {
		RegistrationRequest message = new RegistrationRequest();
		
		return message;
	}
}
