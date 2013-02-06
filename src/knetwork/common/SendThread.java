package knetwork.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.KNetwork;
import knetwork.message.Message;


public class SendThread extends Thread {
	private BlockingQueue<Message> outMessages;

	private String destinationIp;
	private int destinationPort;
	private boolean finished;
	private DatagramSocket localSocket;
	
	public SendThread(String destinationIp, int destinationPort, DatagramSocket localSocket) {
		finished = false;
		outMessages = new ArrayBlockingQueue<Message>(KNetwork.sendThreadQueueSize);
		
		this.destinationIp = destinationIp;
		this.destinationPort = destinationPort;
		this.localSocket = localSocket;
	}
	
	private void main() throws IOException {
		byte[] data = null;
		InetAddress destinationAddress = InetAddress.getByName(destinationIp);
        DatagramPacket packet = null;
        ByteArrayOutputStream bStream = null;
        ObjectOutputStream oStream = null;
        Message message = null;
        
		while (!finished) {
			message = outMessages.poll();
			if (message != null) {
				bStream = new ByteArrayOutputStream();
		        oStream = new ObjectOutputStream(bStream);
				oStream.writeObject(message);
				
		        data = bStream.toByteArray();
		        System.out.println("[Send Thread] Sending message| size = " + data.length + ", seq# = " + message.getSeqNumber());
		        packet = new DatagramPacket(data, data.length, destinationAddress, destinationPort);
		        localSocket.send(packet);
				
				oStream.close();
			}
		}
	}
	
	public void terminate() {
		this.finished = true;
	}
	
	public void queueMessage(Message m) {
		outMessages.add(m);
	}
	
	public void run() {
		try {
			main();
		}catch (SocketException e) {
			System.out.println("[Send Thread] " + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[Send Thread] Terminated");
	}
}