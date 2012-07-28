package network.threads;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import network.message.Message.MessageType;
import network.message.*;

public class ClientRegistrationThread extends Thread {
	private DatagramSocket localSocket;
	private String serverIp;
	private int serverPort;
	private int clientId;
	
	public ClientRegistrationThread(String serverIp, int serverPort, DatagramSocket localSocket) {
		this.localSocket = localSocket;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] sendData = null;
		DatagramPacket sendPacket = null;
		ObjectInputStream iStream = null;
        InetAddress serverAddress = InetAddress.getByName(serverIp);
		
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutputStream oStream = new ObjectOutputStream(bStream);

        // Send request
		oStream.writeObject(new RegistrationRequest());
        sendData = bStream.toByteArray();
        sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        localSocket.send(sendPacket);
        
        bStream.close();
        oStream.close();

        // Receive response
        byte[] recvData = new byte[Message.MAX_SIZE];
        DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
        Message message = null;
        while (message == null || message.getMessageType() != MessageType.RegistrationResponse) {
    		localSocket.receive(recvPacket);
    		iStream = new ObjectInputStream(new ByteArrayInputStream(recvPacket.getData()));
            message = (Message) iStream.readObject();
        }
        RegistrationResponse response= (RegistrationResponse)message;
        clientId = response.getRegisteredClientId();
        
    	iStream.close();
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public void run() {
		try {
			main();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
