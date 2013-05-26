package knetwork.message;

import java.net.DatagramPacket;

import knetwork.message.messages.Message;

public class DefaultMessageFactory extends MessageFactory {

	@Override
	protected Message buildMessage(DatagramPacket packet,
			int intMessageType, MessageBody body) {
		return null;
	}

}
