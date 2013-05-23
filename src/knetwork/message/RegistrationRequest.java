package knetwork.message;

import java.net.DatagramPacket;
import knetwork.message.MessageTypes.MessageType;

public class RegistrationRequest extends Message {
	private DatagramPacket packet;
	private static final byte[] ZERO_BYTE_ARRAY = new byte[0];
	
	public RegistrationRequest() {
		super(MessageType.REG_REQUEST.getValue());
	}
	
	private RegistrationRequest(DatagramPacket packet) {
		super(MessageType.REG_REQUEST.getValue());
		this.packet = packet;
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}

	@Override
	protected byte[] generateDerivedMessageBytes() {
		return ZERO_BYTE_ARRAY;
	}
	
	public static Message constructFromPacket(DatagramPacket packet) {
		RegistrationRequest message = new RegistrationRequest(packet);
		
		return message;
	}
}
