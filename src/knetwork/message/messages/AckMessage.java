package knetwork.message.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import knetwork.message.MessageBody;
import knetwork.message.MessageTypes.MessageType;

public class AckMessage extends Message implements Serializable {
	private static final long serialVersionUID = 4423374756069684189L;
	
	private int ackMsgId;
	
	public AckMessage(Message message) {
		super(MessageType.ACK.getValue());
		setReceiverId(message.getSenderId());
		ackMsgId = message.getMessageId();
	}
	
	private AckMessage(int ackMsgId) {
		super(MessageType.ACK.getValue());
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
