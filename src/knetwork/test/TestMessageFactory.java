package knetwork.test;

import java.net.DatagramPacket;

import knetwork.common.Logger;
import knetwork.message.Message;
import knetwork.message.MessageBody;
import knetwork.message.MessageFactory;
import knetwork.test.TestMessageTypes.TestMessageType;


public class TestMessageFactory extends MessageFactory {
	@Override
	protected Message buildMessageBody(DatagramPacket packet, int intMessageType, MessageBody body) {
		Message message = super.buildMessageBody(packet, intMessageType, body);
		
		// The base message factory was able to construct a message, return it
		if (message != null) {
			return message;
		}
		
		TestMessageType messageType = TestMessageType.convertToEnum(intMessageType);
		Logger.log("Received Type : " + messageType);
		
		if (messageType == TestMessageType.TEST_TYPE) {
			message = TestMessage.constructFromMessageBody(body);
		}
		
		return message;
	}
}
