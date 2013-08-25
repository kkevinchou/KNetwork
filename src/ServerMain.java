import knetwork.managers.ServerNetworkManager;
import knetwork.message.messages.Message;
import knetwork.test.TestMessageFactory;
import knetwork.test.TestMessage;

public class ServerMain {
	public static void main(String[] args) {
		System.out.println("Starting Server...");
		ServerNetworkManager serverNetworkManager = new ServerNetworkManager();
		serverNetworkManager.setMessageFactory(new TestMessageFactory());

		if (!serverNetworkManager.waitForRegistrations(8087, 1)) {
			return;
		}

		while (true) {
			Message message = serverNetworkManager.recv_blocking();

			if (message instanceof TestMessage) {
				System.out.println("ServerMain VAL = " + ((TestMessage)message).val);
			} else {
				System.out.println("NULL MESSAGE");
			}
		}
	}
}