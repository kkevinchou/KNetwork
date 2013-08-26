package knetwork.managers;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import knetwork.Util;
import knetwork.Constants;
import knetwork.common.ReceiveThread;
import knetwork.common.SendThread;
import knetwork.message.*;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;
import knetwork.message.messages.RegistrationRequest;
import knetwork.message.messages.RegistrationResponse;

public class ServerNetworkManager extends BaseNetworkingManager {
	private DatagramSocket socket;
	private ConcurrentMap<Integer, SendThread> sendThreads;

	public ServerNetworkManager() {
		super(Constants.SERVER_IN_QUEUE_SIZE);
	}

	public void setMessageFactory(MessageFactory messageFactory) {
		receiveThread.setMessageFactory(messageFactory);
	}

	public Set<Integer> getClientIds() {
		return sendThreads.keySet();
	}

	public boolean waitForRegistrations(int port, int numRegistrations) {
		boolean acquireSocketSuccessful = false;

		try {
			socket = new DatagramSocket(port);
			acquireSocketSuccessful = true;
		} catch (BindException e) {
			Util.error("[ServerNetworkManager] " + e.toString());
		} catch (SocketException e) {
			Util.error("[ServerNetworkManager] " + e.toString());
		} finally {
			if (!acquireSocketSuccessful) {
				disconnect();
				return false;
			}
		}

		receiveThread.setSocket(socket);
		receiveThread.start();

		sendThreads = new ConcurrentHashMap<Integer, SendThread>();

		int nextClientId = 1;
		int numSuccessfulRegistrations = 0;

		while (numSuccessfulRegistrations < numRegistrations) {
			boolean success = false;

			try {
				success = registerUser(nextClientId);
			} catch (IOException e) {
				Util.log("[ServerNetworkManager] " + e.toString());
			} catch (InterruptedException e) {
				Util.log("[ServerNetworkManager] " + e.toString());
			}

			if (success) {
				nextClientId++;
				numSuccessfulRegistrations++;
			}
		}

		return true;
	}

	private boolean registerUser(int clientId) throws IOException, InterruptedException {
		Message message = recv_blocking();

		if (!(message instanceof RegistrationRequest)) {
			return false;
		}

		DatagramPacket packet = ((RegistrationRequest)message).getPacket();

		String clientIp = packet.getAddress().getHostAddress();
		int clientPort = packet.getPort();

		SendThread sendThread = new SendThread(clientIp, clientPort, socket);
		sendThread.start();
		sendThreads.put(clientId, sendThread);

		_send(new RegistrationResponse(clientId));

		InetAddress clientAddress = InetAddress.getByName(clientIp);
		Util.log("[ServerNetworkManager] Registered User - " + clientAddress + " " + clientPort);

        return true;
	}

	public void send_reliable(int clientId, Message message) {
		message.setReliable(true);
		send(clientId, message);
		outAcknowledgements.put(message.getMessageId(), message);
	}

	public void send(int clientId, Message message) {
		message.setReceiverId(clientId);
		_send(message);
	}

	@Override
	protected void _send(Message m) {
		if (m.getReceiverId() == Constants.SERVER_ID) {
			System.out.println("WARNING - SENDING MESSAGE FROM SERVER TO SERVER");
		}

		if (m.getReceiverId() == Constants.UNASSIGNED_ID) {
			System.out.println("WARNING - SENDING MESSAGE TO AN UNASSIGNED CLIENT ID");
		}

		m.setSenderId(Constants.SERVER_ID);
		SendThread sendThread = sendThreads.get(m.getReceiverId());
		sendThread.queueMessage(m);
	}

	public void broadcast(Message message) {
		for (Integer clientId : sendThreads.keySet()) {
			send(clientId, message);
		}
	}

	public void broadcast_reliable(Message message) {
		for (Integer clientId : sendThreads.keySet()) {
			send_reliable(clientId, message);
		}
	}

	@Override
	public void disconnect() {
		super.disconnect();
		receiveThread.interrupt();

		try {
			receiveThread.join();
		} catch (InterruptedException e) {
			Util.log("[ServerNetworkManager] " + e.toString());
		}

		if (sendThreads != null) {
			for (ConcurrentMap.Entry<Integer, SendThread> entry : sendThreads.entrySet()) {
				SendThread sendThread = entry.getValue();
				sendThread.interrupt();
			}

			for (ConcurrentMap.Entry<Integer, SendThread> entry : sendThreads.entrySet()) {
				SendThread sendThread = entry.getValue();
				try {
					sendThread.join();
				} catch (InterruptedException e) {
					Util.log("[ServerNetworkManager] " + e.toString());
				}
			}
		}
	}
}
