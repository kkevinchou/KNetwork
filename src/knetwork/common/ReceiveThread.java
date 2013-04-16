package knetwork.common;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.*;

public class ReceiveThread extends Thread {
	private BlockingQueue<Message> inMessages;
	private boolean finished;
	private DatagramSocket localSocket;
	protected Map<Integer, Integer> senderSequenceNumbers;
	
	public ReceiveThread(DatagramSocket localSocket, BlockingQueue<Message> inMessages) {
		this.localSocket = localSocket;
		this.inMessages = inMessages;
		finished = false;
		senderSequenceNumbers = new HashMap<Integer, Integer>();
	}
	
	private void main() throws IOException, ClassNotFoundException {
		byte[] data = null;
		DatagramPacket packet = null;
		ObjectInputStream iStream = null;
		Message message = null;
		int seqNumber = 0;
		int senderId = 0;
		
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
	        
	        senderId = message.getSenderId();
	        seqNumber = message.getSeqNumber();
	        
	        // For now, we won't check for valid sequence numbers
	        // I haven't thought of a good way to handle wrapping around yet
	        
	        senderSequenceNumbers.put(senderId, seqNumber);
	        inMessages.add(message);
	        System.out.println("[Receive Thread] Received message [" + senderId + "]| " + "size = " + packet.getLength() + ", seq# = " + seqNumber);
	        
        	iStream.close();
		}
	}
	
	public void terminate() {
		this.finished = true;
	}
	
	public void run() {
		try {
			main();
		} catch (SocketException e) {
			System.out.println("[Receive Thread] " + e.toString());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[Receive Thread] Terminated");
	}
	
	// TODO: This way of handling wrapping seems very hacky...
	private boolean sequenceNumberOkay(int currentSeqNumber, int lastSeqNumber) {
		int bitMask = 0x60000000; // bit mask for first 2 bits
		
		if (((lastSeqNumber & bitMask) == 0) && ((currentSeqNumber & bitMask) == 0x60000000)) {
			// Too big of a jump, probably was out of order after a wrap around
			return false;
		} else if (currentSeqNumber > lastSeqNumber) {
			return true;
		} else if (((lastSeqNumber & bitMask) == 0x60000000) && ((currentSeqNumber & bitMask) == 0))  {
			// If we go from a 0b11-- number to a 0b00-- number, we must've wrapped around
			return true;
		}
				
		return false;
	}
}
