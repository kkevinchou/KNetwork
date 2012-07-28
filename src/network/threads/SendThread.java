package network.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import network.message.Message;

public class SendThread extends Thread {
	private BlockingQueue<Message> outMessages;

	private String destinationIp;
	private int destinationPort;
	private boolean finished;
	private DatagramSocket localSocket;
	
	public SendThread(String destinationIp, int destinationPort, DatagramSocket localSocket) {
		this.finished = false;
		outMessages = new ArrayBlockingQueue<Message>(100);
		
		this.destinationIp = destinationIp;
		this.destinationPort = destinationPort;
		this.localSocket = localSocket;
	}
	
	private void main() throws IOException {
		InetAddress destinationAddress = InetAddress.getByName(destinationIp);
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		byte[] data;
        ObjectOutputStream oStream = new ObjectOutputStream(bStream);
        DatagramPacket packet;
        
		while (!finished) {
			Message message = outMessages.poll();
			if (message != null) {
				oStream.writeObject(message);
		        data = bStream.toByteArray();
		        packet = new DatagramPacket(data, data.length, destinationAddress, destinationPort);
		        localSocket.send(packet);
			}
		}
		oStream.close();
	}
	
	public void terminate() {
		this.finished = true;
		this.interrupt();
	}
	
	public void queueMessage(Message m) {
		outMessages.add(m);
	}
	
	public void run() {
		try {
			main();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
