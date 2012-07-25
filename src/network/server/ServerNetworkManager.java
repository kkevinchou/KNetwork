package network.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;

import network.message.Message;
import network.message.RegistrationRequest;
import network.message.Message.MessageType;
import network.message.RegistrationResponse;

public class ServerNetworkManager {
	private DatagramSocket socket = null;
	
	public ServerNetworkManager(int port) throws SocketException {
		socket = new DatagramSocket(port);
	}
	
	public void listen() throws IOException, ClassNotFoundException {
		byte[] data = new byte[100];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		
		ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        Message message = (Message) iStream.readObject();
        iStream.close();
        
        ByteArrayOutputStream bStream = null;
        if (message == null) {
        	System.out.println("null!");
        }
        if (message.getMessageType() == MessageType.RegistrationRequest) {
        	System.out.println("Registration request received!");
        	
        	bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream); 
            oo.writeObject(new RegistrationResponse(1));
            oo.close();
            
            int port = packet.getPort();
            InetAddress address = packet.getAddress();
            System.out.println(address.toString() + ", " + port);
            
            data = bStream.toByteArray();
            packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
        }
	}
}
