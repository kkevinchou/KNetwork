package knetwork.message;

import java.io.Serializable;

public class TestMessage extends Message implements Serializable {
	private static final long serialVersionUID = -1992275173175723676L;

	public int val;
	
	public TestMessage(int val) {
		super();
		this.val = val;
	}

	public MessageType getMessageType() {
		return MessageType.Test;
	}
}
