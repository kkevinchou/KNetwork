package network.threads;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import network.message.Message;
import network.message.TestMessage;
import network.message.Message.MessageType;

public class ReceiveThread extends Thread {
	BlockingQueue<Message> inMessages;
	private boolean finished;
	protected DatagramSocket localSocket;
	protected int lastSeqNumber;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages) {
		this.inMessages = inMessages;
		this.finished = false;
		this.localSocket = localSocket;
		lastSeqNumber = 0;
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] data = null;
		DatagramPacket packet = null;
		ObjectInputStream iStream = null;
		Message message = null;
		
		while (!finished) {
			data = new byte[Message.MAX_SIZE];
			packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);
			iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

	        try {
				message = (Message) iStream.readObject();
			} catch (ClassCastException e) {
				e.printStackTrace();
				continue;
			}
	        
	        int seqNumber = message.getSeqNumber();
	        if (seqNumber <= lastSeqNumber) {
	        	System.out.println("[Receive Thread] Received message out of order");
	        	continue;
	        }
	        lastSeqNumber = seqNumber;
	        
	        if (message.getMessageType() == MessageType.Test) {
	        	TestMessage t = (TestMessage)message;
		        System.out.println("[Receive Thread] Received message with sequence number = " + t.getSeqNumber());
	        }
	        
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
