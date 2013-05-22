package knetwork.message;

public class MessageTypes {

	public enum MessageType {
		REG_REQUEST(0),
		REG_RESPONSE(1),
		ACK(2),
		TEST(3);
		
		private final int value;
		
		private MessageType(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return this.value;
		}
	}

}
