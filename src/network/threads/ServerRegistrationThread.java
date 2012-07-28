package network.threads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import network.message.Message.MessageType;
import network.message.*;

public class ServerRegistrationThread extends Thread {
	private DatagramSocket localSocket;
	private int numRegistrations;
	private int numCurrentRegistrations;
	private int nextClientId;
	private ConcurrentMap<Integer, SendThread> clientSendThreads;
	
	public ConcurrentMap<Integer, SendThread> getClientSendThreads() {
		return clientSendThreads;
	}

	public ServerRegistrationThread(DatagramSocket localSocket, int numRegistrations) {
		numCurrentRegistrations = 0;
		nextClientId = 0;
		clientSendThreads = new ConcurrentHashMap<Integer, SendThread>();
		
		this.localSocket = localSocket;
		this.numRegistrations = numRegistrations;
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
			// Receive declarations
			recvData = new byte[Message.MAX_SIZE];
			recvPacket = new DatagramPacket(recvData, recvData.length);
			localSocket.receive(recvPacket);
			iStream = new ObjectInputStream(new ByteArrayInputStream(recvPacket.getData()));
			message = (Message) iStream.readObject();
        	
        	if (message.getMessageType() != MessageType.RegistrationRequest) continue;
        	
    		// Receive Request
    		clientIp = recvPacket.getAddress().getHostAddress();
    		clientPort = recvPacket.getPort();
    		clientSendThreads.put(nextClientId, new SendThread(clientIp, clientPort, localSocket));
    		
        	// Send Response
        	bStream = new ByteArrayOutputStream();
            oStream = new ObjectOutputStream(bStream);
            clientAddress = InetAddress.getByName(clientIp);
            
            oStream.writeObject(new RegistrationResponse(nextClientId));
            sendData = bStream.toByteArray();
	        sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
	        localSocket.send(sendPacket);
	        
	        bStream.close();
	        oStream.close();

    		numCurrentRegistrations++;
    		nextClientId++;
    		
    		if (numCurrentRegistrations == 1) {
            	System.out.println(numCurrentRegistrations + " client has registered!");
    		} else {
            	System.out.println(numCurrentRegistrations + " clients have registered!");
    		}
	        System.out.println(clientAddress + " " + clientPort);
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
