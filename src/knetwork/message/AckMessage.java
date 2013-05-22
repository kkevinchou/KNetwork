package knetwork.message;

import java.io.Serializable;
import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public class AckMessage extends Message implements Serializable {
	private static final long serialVersionUID = 4423374756069684189L;
	
	private int ackMsgId;
	
	public AckMessage(Message message) {
		setReceiverId(message.getSenderId());
		ackMsgId = message.getMessageId();
	}
	
	private AckMessage(int ackMsgId) {
		this.ackMsgId = ackMsgId;
	}
	
	public int getAckMsgId() {
		return ackMsgId;
	}

	@Override
	protected byte[] generateDerivedMessageBytes() {
		int totalBytes = 2 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.ACK.getValue());
		buffer.putInt(ackMsgId);
		
		return buffer.array();
	}
	
	public static Message constructFromByteBuffer(ByteBuffer buffer) {
		int ackMsgId = buffer.getInt();
		
		AckMessage message = new AckMessage(ackMsgId);
		
		return message;
	}
}
