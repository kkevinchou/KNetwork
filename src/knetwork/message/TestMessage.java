package knetwork.message;

import java.io.Serializable;
import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public class TestMessage extends Message implements Serializable {
	private static final long serialVersionUID = -1992275173175723676L;

	public int val;
	
	public TestMessage() {
		this.val = 12;
	}
	
	public TestMessage(int val) {
		this.val = val;
	}

	@Override
	protected byte[] generateDerivedMessageBytes() {
		int totalBytes = 2 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.TEST.getValue());
		buffer.putInt(val);
		
		return buffer.array();
	}
	
	public static Message constructFromByteBuffer(ByteBuffer buffer) {
		int val = buffer.getInt();
		
		TestMessage message = new TestMessage(val);
		
		return message;
	}
}
