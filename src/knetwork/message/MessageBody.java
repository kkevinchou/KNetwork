package knetwork.message;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;

import knetwork.Constants;

public class MessageBody {
	byte[] bodyBytes;
	
	public MessageBody(DatagramPacket packet) {
		byte[] data = packet.getData();
		int length = packet.getLength();
		
		int headerSize = Constants.MESSAGE_HEADER_SIZE;
		ByteBuffer buffer = ByteBuffer.allocate(length - headerSize).put(data, headerSize, length - headerSize);
		buffer.position(0);
		
		bodyBytes = buffer.array();
	}
	
	public ByteBuffer getByteBuffer() {
		ByteBuffer buffer = ByteBuffer.allocate(bodyBytes.length).put(bodyBytes);
		buffer.position(0);
		return buffer;
	}
}
