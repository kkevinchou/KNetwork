package knetwork.managers;

import java.net.DatagramSocket;

import knetwork.Constants;
import knetwork.common.BaseNetworkingManager;
import knetwork.common.Logger;
import knetwork.common.ReceiveThread;
import knetwork.common.SendThread;
import knetwork.message.*;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;
import knetwork.message.messages.RegistrationRequest;
import knetwork.message.messages.RegistrationResponse;


public class ClientNetworkManager extends BaseNetworkingManager {
	private DatagramSocket socket;
	private SendThread sendThread;
	private ReceiveThread receiveThread;
	private int clientId;
	private MessageFactory messageFactory;
	
	public ClientNetworkManager() {
		super(Constants.CLIENT_IN_QUEUE_SIZE);
		clientId = -1;
		messageFactory = new DefaultMessageFactory();
		receiveThread = new ReceiveThread(this, messageFactory, inMessages, inAcknowledgements);
	}
	
	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
		receiveThread.setMessageFactory(messageFactory);
	}
	
	public boolean register(String serverIp, int serverPort) {
		boolean registerSuccess = false;
		
		try {
			socket = new DatagramSocket();
			
			sendThread = new SendThread(serverIp, serverPort, socket);
			send(new RegistrationRequest());
			sendThread.start();

			receiveThread.setSocket(socket);
			receiveThread.start();
			
			Message message = null;

			do {
				message = recv_blocking();
			} while (!(message instanceof RegistrationResponse));
			
			RegistrationResponse regResponse = (RegistrationResponse)message;
			clientId = regResponse.getRegisteredClientId();
			
			Logger.log("[ClientNetworkManager] Registered with clientId = " + clientId);
			registerSuccess = true;
		} catch (Exception e) {
			Logger.error("[ClientNetworkManager] " + e.toString());
		}
		
		if (!registerSuccess) {
			disconnect();
		}
		
		return registerSuccess;
	}
	
	public void sendMessageAcknowledgement(Message m) {
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
			Logger.error("[ClientNetworkManager] " + e.toString());
		}
		
		socket.close();
	}

	protected void reSendReliableMessage(Message message) {
		send(message);
	}
}