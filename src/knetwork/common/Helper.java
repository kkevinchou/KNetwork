package knetwork.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import knetwork.message.Message;

public abstract class Helper {
	public static Message getMessageFromPacket(DatagramPacket packet) {
		Message message = null;
		try {
			ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
			message = (Message)iStream.readObject();
	        iStream.close();
		} catch (ClassCastException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	return message;
	}
	
	public static byte[] convertMessageToByteArray(Message m) {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
    	ObjectOutputStream oStream;
		try {
			oStream = new ObjectOutputStream(bStream);
	        oStream.writeObject(m);

	        bStream.close();
	        oStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        return bStream.toByteArray();
	}
}
