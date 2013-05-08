package knetwork.message;

import java.io.Serializable;

public class AckMessage extends Message implements Serializable {
	private static final long serialVersionUID = 4423374756069684189L;
	
	private int ackMsgId;
	
	public AckMessage(Message message) {
		super(message.getSenderId());
		ackMsgId = message.getMessageId();
	}
	
	public int getAckMsgId() {
		return ackMsgId;
	}
}
