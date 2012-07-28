package network.threads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import network.message.Message;

public class ReceiveThread extends Thread {
	BlockingQueue<Message> inMessages;
	private boolean finished;
	protected DatagramSocket localSocket;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages) {
		this.inMessages = inMessages;
		this.finished = false;
		this.localSocket = localSocket;
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] data = null;
		DatagramPacket packet = null;
		ObjectInputStream iStream = null;
		
		while (!finished) {
			data = new byte[Message.MAX_SIZE];
			packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);
			
			iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));
	        Message message = (Message) iStream.readObject();
        	inMessages.add(message);
        	System.out.println("Message received!");
		}
        
		if (iStream != null) {
	        iStream.close();
		}
	}
	
	public void terminate() {
		this.finished = true;
		this.interrupt();
	}
	
	public void run() {
		try {
			main();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
