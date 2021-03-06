
import knetwork.Settings;
import knetwork.Util;
import knetwork.managers.ClientNetworkManager;
import knetwork.test.TestMessageFactory;
import knetwork.test.TestMessage;

public class ClientMain {
	public static void main(String[] args) {
		// String serverIp = "129.97.167.52";
		 String serverIp = "192.168.226.128";
//		String serverIp = "127.0.0.1";

		int serverPort = 8087;

		Settings.DEBUG_LOG = true;

		ClientNetworkManager clientNetworkManager = new ClientNetworkManager();
		clientNetworkManager.setMessageFactory(new TestMessageFactory());
		Util.log("Registering with server at " + serverIp + ":" + serverPort);

		if (!clientNetworkManager.register(serverIp, serverPort)) {
			return;
		}

		int sendCounter = 0;

		while (true) {
			if (sendCounter++ < 4) {
				clientNetworkManager.send_reliable(new TestMessage(sendCounter));
				System.out.println("send!");
			} else {
				break;
			}
		}

		clientNetworkManager.disconnect();
	}
}
