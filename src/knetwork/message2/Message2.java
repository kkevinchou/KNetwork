package knetwork.message2;


public class Message2 extends BaseMessage {
	public enum MessageType { REG_REQUEST, REG_RESPONSE, ACK };
	
	public Message2() {
		bytes = new Byte[4];
		
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = new Byte("1");
		}
	}
}
