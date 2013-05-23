package knetwork.message;

import java.net.DatagramPacket;

import knetwork.Constants;
import knetwork.common.Logger;
import knetwork.message.MessageTypes.MessageType;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;
import knetwork.message.messages.RegistrationRequest;
import knetwork.message.messages.RegistrationResponse;

public abstract class MessageFactory {
	protected abstract Message buildMessageBody(DatagramPacket packet, int intMessageType, MessageBody body);
	
	private Message defaultBuildMessageBody(DatagramPacket packet, int intMessageType, MessageBody body) {
		// Compare message type with the last KNetwork message type.
		// If larger, then it's a user defined message type
		if (intMessageType >
				MessageType.values()[MessageType.values().length - 1].getValue()) {
			return null;
		}
		
		MessageType messageType = MessageType.values()[intMessageType];
		Logger.log("Received Type : " + messageType);
		
		Message message = null;
		
		if (messageType == MessageType.REG_REQUEST) {
			message = RegistrationRequest.constructFromPacket(packet);
		} else if (messageType == MessageType.REG_RESPONSE) {
			message = RegistrationResponse.constructFromMessageBody(body);
		} else if (messageType == MessageType.ACK) {
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
		
		Message message = buildMessageBody(packet, header.getMessageType(), body);
		
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
