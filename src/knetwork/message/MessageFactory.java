package knetwork.message;

import java.net.DatagramPacket;

import knetwork.Constants;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;
import knetwork.message.messages.RegistrationRequest;
import knetwork.message.messages.RegistrationResponse;

public abstract class MessageFactory {
	protected abstract Message buildMessage(DatagramPacket packet, int intMessageType, MessageBody body);
	
	private Message defaultBuildMessageBody(DatagramPacket packet, int messageType, MessageBody body) {
		// If larger, then it's a user defined message type
		if (messageType > Constants.MAX_RESERVED_MESSAGE_TYPE) {
			return null;
		}
		
		Message message = null;
		
		if (messageType == RegistrationRequest.MESSAGE_TYPE) {
			message = RegistrationRequest.constructFromPacket(packet);
		} else if (messageType == RegistrationResponse.MESSAGE_TYPE) {
			message = RegistrationResponse.constructFromMessageBody(body);
		} else if (messageType == AckMessage.MESSAGE_TYPE) {
			message = AckMessage.constructFromMessageBody(body);
		}
		
		return message;
	}
	
	public final Message buildMessageFromPacket(DatagramPacket packet) {
		MessageHeader header = new MessageHeader(packet);
		
		if (header.getProtocolId() != Constants.PROTOCOL_ID) {
			return null;
		}
		
		MessageBody body = new MessageBody(packet);
		
		Message message = buildMessage(packet, header.getMessageType(), body);
		
		// If the user defined message factory fails to build a message, use the default
		if (message == null) {
			message = defaultBuildMessageBody(packet, header.getMessageType(), body);
		}
		
		if (message != null) {
			Message.setMessageHeader(message, header);
			
			// Undo the incremented message ids.  I should find a better way of doing this...
			Message.nextMessageId--;
			Message.nextSeqNumber--;
		}
		
		return message;
	}
}
