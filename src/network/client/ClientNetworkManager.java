package network.client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import network.message.*;
import network.message.Message.MessageType;

public class ClientNetworkManager {
	
	public ClientNetworkManager() {
	}
	
	public void connect() throws IOException, ClassNotFoundException {
        DatagramSocket socket = new DatagramSocket();
        
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        ObjectOutput oo = new ObjectOutputStream(bStream); 
        oo.writeObject(new RegistrationRequest());
        oo.close();

        byte[] data = bStream.toByteArray();
        InetAddress address = InetAddress.getByName("192.168.203.130");
        DatagramPacket packet = new DatagramPacket(data, data.length, address, 9001);
        socket.send(packet);
    
        data = new byte[1000];
        packet = new DatagramPacket(data, data.length);
        socket.receive(packet);

        ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
        Message message = (Message) iStream.readObject();
        iStream.close();
        
        if (message.getMessageType() == MessageType.RegistrationResponse) {
        	System.out.println("Response received!");
        }
    
        socket.close();
	}
}