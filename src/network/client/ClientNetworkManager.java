package network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import network.message.*;
import network.message.Message.MessageType;

public class ClientNetworkManager {
	private Socket clientSocket = null;
	private int clientId;
	
	public ClientNetworkManager() throws UnknownHostException, IOException, ClassNotFoundException {
		clientSocket = new Socket("192.168.203.130", 9001);
		
		ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
		Message message = (Message)inputStream.readObject();
		
		if (message.getMessageType() == MessageType.RegistrationResponse) {
			RegistrationResponse response = (RegistrationResponse)message;
			clientId = response.getRegisteredClientId();
			
			System.out.println("Registration response received!");
			System.out.println("Client id = " + clientId);
		}
		
		inputStream.close();
		clientSocket.close();
	}
}