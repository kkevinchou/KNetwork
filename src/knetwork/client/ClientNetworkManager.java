package knetwork.client;

import java.net.DatagramSocket;

import knetwork.Constants;
import knetwork.common.BaseNetworkingManager;
import knetwork.message.*;
import knetwork.message.Message.MessageType;
import knetwork.server.ReceiveThread;
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
				m = recv_blocking();
				while (m.getMessageType() != MessageType.RegistrationResponse) {
					m = recv_blocking();
				}
			} catch (InterruptedException e) {
				System.out.println("[ClientNetworkManager] " + e.toString());
				System.out.println("[ClientNetworkManager] Registration interrupted");
				throw e;
			}
			
			RegistrationResponse regResponse = (RegistrationResponse)m;
			clientId = regResponse.getRegisteredClientId();
			
			System.out.println("[ClientNetworkManager] Registered with clientId = " + clientId);
			return true;
		} catch (Exception e) {
		}
		
		return false;
	}
	
	protected void sendMessageAcknowledgement(Message m) {
		send(new AckMessage(m));
	}

	public void send(Message m) {
		m.setSenderId(clientId);
        sendThread.queueMessage(m);
	}
	
	public void send_reliable(Message m) {
		m.reliable = true;
		send(m);
		outAcknowledgements.put(m.getMessageId(), m);
	}
	
	public void disconnect() {
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

	protected void reSendReliableMessage(Message m) {
		System.out.println("CLIENT RESEND");
		send(m);
	}
}