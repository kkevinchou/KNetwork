package knetwork.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import knetwork.KNetwork;
import knetwork.common.SendThread;
import knetwork.message.*;
import knetwork.message.Message.MessageType;


public class ServerRegistrationThread extends Thread {
	private DatagramSocket localSocket;
	private int numRegistrations;
	private int numCurrentRegistrations;
	private int nextClientId;
	private ConcurrentMap<Integer, SendThread> clientSendThreads;
	private List<Integer> clientIds;
	
	public ConcurrentMap<Integer, SendThread> getClientSendThreads() {
		return clientSendThreads;
	}
	
	public List<Integer> getClientIds() {
		return clientIds;
	}

	public ServerRegistrationThread(DatagramSocket localSocket, int numRegistrations) {
		numCurrentRegistrations = 0;
		nextClientId = 0;
		clientSendThreads = new ConcurrentHashMap<Integer, SendThread>();
		
		this.localSocket = localSocket;
		this.numRegistrations = numRegistrations;
		clientIds = new ArrayList<Integer>();
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] recvData = null;
		byte[] sendData = null;
		DatagramPacket recvPacket = null;
		DatagramPacket sendPacket = null;
		
		ObjectInputStream iStream = null;
		ObjectOutputStream oStream = null;
		ByteArrayOutputStream bStream = null;
		
		String clientIp = null;
		int clientPort = -1;
		InetAddress clientAddress = null;
		Message message = null;
		
		while (numCurrentRegistrations < numRegistrations) {
			recvData = new byte[KNetwork.maxUdpByteReadSize];
			recvPacket = new DatagramPacket(recvData, recvData.length);
			
			localSocket.receive(recvPacket);
			iStream = new ObjectInputStream(new ByteArrayInputStream(recvPacket.getData()));
			
			try {
				message = (Message) iStream.readObject();
			} catch (ClassCastException e) {
				e.printStackTrace();
				continue;
			}
        	if (message.getMessageType() != MessageType.RegistrationRequest) continue;
        	
    		// Receive Request
    		clientIp = recvPacket.getAddress().getHostAddress();
    		clientPort = recvPacket.getPort();
    		clientSendThreads.put(nextClientId, new SendThread(clientIp, clientPort, localSocket));
    		clientIds.add(nextClientId);
    		
        	// Send Response
        	bStream = new ByteArrayOutputStream();
            oStream = new ObjectOutputStream(bStream);
            clientAddress = InetAddress.getByName(clientIp);
            
            RegistrationResponse registrationResponse = new RegistrationResponse(nextClientId);
            registrationResponse.setSenderId(KNetwork.serverSenderId);
            oStream.writeObject(registrationResponse);
            sendData = bStream.toByteArray();
	        sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
	        localSocket.send(sendPacket);
	        
	        bStream.close();
	        oStream.close();

    		numCurrentRegistrations++;
    		nextClientId++;
    		
    		if (numCurrentRegistrations == 1) {
            	System.out.print(numCurrentRegistrations + " client has registered!");
    		} else {
            	System.out.print(numCurrentRegistrations + " clients have registered!");
    		}
	        System.out.println(" - " + clientAddress + " " + clientPort);
		}
        
		if (iStream != null) {
	        iStream.close();
		}
	}
	
	public void run() {
		try {
			main();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
