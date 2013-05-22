package knetwork.client;

import java.net.DatagramSocket;

import knetwork.Constants;
import knetwork.common.BaseNetworkingManager;
import knetwork.common.Helper;
import knetwork.message.*;
import knetwork.threads.ReceiveThread;
import knetwork.threads.SendThread;


public class ClientNetworkManager extends BaseNetworkingManager {
	private DatagramSocket socket;
	private SendThread sendThread;
	private ReceiveThread receiveThread;
	private int clientId;
	
	public ClientNetworkManager() {
		super(Constants.CLIENT_IN_QUEUE_SIZE);
		clientId = -1;
	}
	
	public boolean register(String serverIp, int serverPort) {
		try {
			socket = new DatagramSocket();
			
			sendThread = new SendThread(serverIp, serverPort, socket);
			send(new RegistrationRequest());
			sendThread.start();

			receiveThread = new ReceiveThread(socket, inMessages, inAcknowledgements);
			receiveThread.start();
			
			Message message = null;
			
			try {
				do {
					message = recv_blocking();
				} while (!(message instanceof RegistrationResponse));
			} catch (InterruptedException e) {
				Helper.log("[ClientNetworkManager] " + e.toString());
				Helper.log("[ClientNetworkManager] Registration interrupted");
				throw e;
			}
			
			RegistrationResponse regResponse = (RegistrationResponse)message;
			clientId = regResponse.getRegisteredClientId();
			
			Helper.log("[ClientNetworkManager] Registered with clientId = " + clientId);
			return true;
		} catch (Exception e) {
		}
		
		return false;
	}
	
	protected void sendMessageAcknowledgement(Message m) {
		send(new AckMessage(m));
	}

	public void send(Message message) {
		message.setSenderId(clientId);
		message.setReceiverId(Constants.SERVER_ID);
        sendThread.queueMessage(message);
	}
	
	public void send_reliable(Message message) {
		message.setReliable(true);
		send(message);
		outAcknowledgements.put(message.getMessageId(), message);
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		receiveThread.interrupt();
		sendThread.interrupt();
		
		try {
			receiveThread.join();
			sendThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		socket.close();
	}

	protected void reSendReliableMessage(Message message) {
		send(message);
	}
}