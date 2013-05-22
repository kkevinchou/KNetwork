
import knetwork.message.Message;
import knetwork.message.MessageTypes.MessageType;
import knetwork.message.TestMessage;

public class Test {

	public static void main(String[] args) {
		MessageType messageType = MessageType.values()[0];
		
		if (messageType == MessageType.REG_REQUEST) {
			System.out.println("REQ");
		} else if (messageType == MessageType.REG_RESPONSE) {
			System.out.println("RESPONSE");
		}
	}

}
