package knetwork.test;

import java.nio.ByteBuffer;

import knetwork.message.MessageBody;
import knetwork.message.messages.Message;

public class TestMessage extends Message {
	public static final int MESSAGE_TYPE = 10;

	public int val;
	
	public TestMessage() {
		super(MESSAGE_TYPE);
		this.val = 12;
	}
	
	public TestMessage(int val) {
		super(MESSAGE_TYPE);
		this.val = val;
	}

	@Override
	protected byte[] generateMessageBodyBytes() {
		int totalBytes = 1 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(val);
		
		return buffer.array();
	}
	
	public static Message constructFromMessageBody(MessageBody body) {
		ByteBuffer buffer = body.getByteBuffer();
		
		int val = buffer.getInt();
		TestMessage message = new TestMessage(val);
		
		return message;
	}
}
