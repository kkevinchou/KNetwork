package knetwork.message2;

public class BaseMessage {
	protected Byte[] bytes;
	
	public byte[] getRawBytes() {
		byte[] rawBytes = new byte[bytes.length];
		for (int i = 0; i < bytes.length; i++) {
			rawBytes[i] = bytes[i].byteValue();
		}
		return rawBytes;
	}
}
