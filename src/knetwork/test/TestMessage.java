package knetwork.test;

import java.io.Serializable;
import java.nio.ByteBuffer;

import knetwork.message.MessageBody;
import knetwork.message.messages.Message;
import knetwork.test.TestMessageTypes.TestMessageType;

public class TestMessage extends Message implements Serializable {
	private static final long serialVersionUID = -1992275173175723676L;

	public int val;
	
	public TestMessage() {
		super(TestMessageType.TEST_TYPE.getValue());
		this.val = 12;
	}
	
	public TestMessage(int val) {
		super(TestMessageType.TEST_TYPE.getValue());
		this.val = val;
	}

	@Override
	protected byte[] generateDerivedMessageBytes() {
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
