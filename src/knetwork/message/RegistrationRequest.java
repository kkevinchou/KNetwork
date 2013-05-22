package knetwork.message;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import knetwork.message.MessageTypes.MessageType;

public class RegistrationRequest extends Message {
	private DatagramPacket packet;
	
	public RegistrationRequest() {
		
	}
	
	private RegistrationRequest(DatagramPacket packet) {
		this.packet = packet;
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}

	@Override
	protected byte[] generateDerivedMessageBytes() {
		int totalBytes = 1 * 4;
		
		ByteBuffer buffer = ByteBuffer.allocate(totalBytes);
		buffer.putInt(MessageType.REG_REQUEST.getValue());
		
		return buffer.array();
	}
	
	public static Message constructFromPacket(DatagramPacket packet) {
		RegistrationRequest message = new RegistrationRequest(packet);
		
		return message;
	}
}
