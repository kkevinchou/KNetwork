package knetwork.test;

import java.net.DatagramPacket;

import knetwork.common.Logger;
import knetwork.message.MessageBody;
import knetwork.message.MessageFactory;
import knetwork.message.messages.Message;

public class TestMessageFactory extends MessageFactory {
	@Override
	protected Message buildMessageBody(DatagramPacket packet, int intMessageType, MessageBody body) {
		Message message = null;
		
		if (intMessageType == TestMessage.MESSAGE_TYPE) {
			message = TestMessage.constructFromMessageBody(body);
		}
		
		return message;
	}
}
