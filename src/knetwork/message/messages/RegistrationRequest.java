package knetwork.message.messages;

import java.net.DatagramPacket;

public class RegistrationRequest extends Message {
	private DatagramPacket packet;
	private static final byte[] ZERO_BYTE_ARRAY = new byte[0];
	public static final int MESSAGE_TYPE = 0;
	
	public RegistrationRequest() {
		super(MESSAGE_TYPE);
	}
	
	private RegistrationRequest(DatagramPacket packet) {
		super(MESSAGE_TYPE);
		this.packet = packet;
	}
	
	public DatagramPacket getPacket() {
		return packet;
	}

	@Override
	protected byte[] generateMessageBodyBytes() {
		return ZERO_BYTE_ARRAY;
	}
	
	public static Message constructFromPacket(DatagramPacket packet) {
		RegistrationRequest message = new RegistrationRequest(packet);
		
		return message;
	}
}
