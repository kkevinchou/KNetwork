package knetwork.message.messages;

import java.nio.ByteBuffer;

import knetwork.message.MessageBody;

public class AckMessage extends Message  {
	public static final int MESSAGE_TYPE = 2;
	
	private int ackMsgId;
	
	public AckMessage(Message message) {
		super(MESSAGE_TYPE);
		setReceiverId(message.getSenderId());
		ackMsgId = message.getMessageId();
	}
	
	private AckMessage(int ackMsgId) {
		super(MESSAGE_TYPE);
		this.ackMsgId = ackMsgId;
	}
	
	public int getAckMsgId() {
		return ackMsgId;
	}

	@Override
	protected byte[] generateMessageBodyBytes() {
		int totalBytes = 1 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(ackMsgId);
		
		return buffer.array();
	}
	
	public static Message constructFromMessageBody(MessageBody body) {
		ByteBuffer buffer = body.getByteBuffer();
		
		int ackMsgId = buffer.getInt();
		AckMessage message = new AckMessage(ackMsgId);
		
		return message;
	}
}
