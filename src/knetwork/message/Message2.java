package knetwork.message;

public class Message2 {
	public Byte[] bytes;
	
	public Message2(Byte[] bytes) {
		this.bytes = new Byte[bytes.length];
		
		for (int i = 0; i < bytes.length; i++) {
			this.bytes[i] = new Byte(bytes[i].byteValue());
		}
	}
}
