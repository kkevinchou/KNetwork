package knetwork.test;

import java.net.DatagramPacket;

import knetwork.common.Logger;
import knetwork.message.MessageBody;
import knetwork.message.MessageFactory;
import knetwork.message.messages.Message;
import knetwork.test.TestMessageTypes.TestMessageType;


public class TestMessageFactory extends MessageFactory {
	@Override
	protected Message buildMessageBody(DatagramPacket packet, int intMessageType, MessageBody body) {
		TestMessageType messageType = TestMessageType.convertToEnum(intMessageType);
		Logger.log("Received Type : " + messageType);
		
		Message message = null;
		
		if (messageType == TestMessageType.TEST_TYPE) {
			message = TestMessage.constructFromMessageBody(body);
		}
		
		return message;
	}
}
