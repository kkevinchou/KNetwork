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
	}
	
	public boolean register(String serverIp, int serverPort) {
		try {
			socket = new DatagramSocket();
			
			sendThread = new SendThread(serverIp, serverPort, socket);
			sendThread.queueMessage(new RegistrationRequest());
			sendThread.start();

			receiveThread = new ReceiveThread(socket, inMessages, inAcknowledgements);
			receiveThread.start();
			
			Message m = null;
			
			try {
				do {
					m = recv_blocking();
				} while (!(m instanceof RegistrationResponse));
			} catch (InterruptedException e) {
				Helper.log("[ClientNetworkManager] " + e.toString());
				Helper.log("[ClientNetworkManager] Registration interrupted");
				throw e;
			}
			
			RegistrationResponse regResponse = (RegistrationResponse)m;
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
        sendThread.queueMessage(message);
	}
	
	public void send_reliable(Message message) {
		message.reliable = true;
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