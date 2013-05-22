package knetwork.message;

import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public abstract class MessageFactory {
	public static Message buildMessageFromByteArray(byte[] bytes, int length) {
		ByteBuffer buffer = ByteBuffer.allocate(length).put(bytes, 0, length);
		buffer.position(0);
		
		int intMessageType = buffer.getInt();
//		System.out.println("MESSAGE FACTORY GOT TYPE: " + intMessageType);
		
		MessageType messageType = MessageType.values()[intMessageType];
		Message message = null; 

		if (messageType == MessageType.REG_REQUEST) {
			message = RegistrationRequest.constructFromByteBuffer(buffer);
		} else if (messageType == MessageType.REG_RESPONSE) {
			message = RegistrationResponse.constructFromByteBuffer(buffer);
		} else if (messageType == MessageType.ACK) {
			message = AckMessage.constructFromByteBuffer(buffer);
		} else if (messageType == MessageType.TEST) {
			message = TestMessage.constructFromByteBuffer(buffer);
		} else {
			System.out.println("ERROR: Unhandled message type in MessageFactory");
		}
		
		if (message != null) {
			Message.setMessageFooterFromByteBuffer(message, buffer);
			// Undo the incremented message ids.  I should find a better way of doing this...
			Message.nextMessageId--;
			Message.nextSeqNumber--;
		}
		
		return message;
	}
}
