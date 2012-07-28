package knetwork.threads;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.Message;
import knetwork.message.TestMessage;
import knetwork.message.Message.MessageType;


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
			data = new byte[KNetwork.maxUdpByteReadSize];
			packet = new DatagramPacket(data, data.length);
			localSocket.receive(packet);
			iStream = new ObjectInputStream(new ByteArrayInputStream(packet.getData()));

	        try {
				message = (Message) iStream.readObject();
			} catch (ClassCastException e) {
				System.out.println("[Receive Thread] could not cast received object into a Message");
				continue;
			} catch (EOFException e) {
				System.out.println("[Receive Thread] Could not read enough data to form an object");
				continue;
			}
	        
	        int seqNumber = message.getSeqNumber();
	        if (seqNumber <= lastSeqNumber) {
	        	System.out.println("[Receive Thread] Received a message out of order");
	        	continue;
	        }
	        lastSeqNumber = seqNumber;
	        
	        System.out.println("[Receive Thread] Received message| " + "size = " + packet.getLength() + ", sequence number = " + message.getSeqNumber());
	        
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
