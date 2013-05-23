package knetwork.common;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.Constants;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;


public class SendThread extends Thread {
	private BlockingQueue<Message> outMessages;

	private String destinationIp;
	private int destinationPort;
	private DatagramSocket localSocket;
	
	public SendThread(String destinationIp, int destinationPort, DatagramSocket localSocket) {
		outMessages = new ArrayBlockingQueue<Message>(Constants.SEND_THREAD_QUEUE_SIZE);
		
		this.destinationIp = destinationIp;
		this.destinationPort = destinationPort;
		this.localSocket = localSocket;
	}
	
	private void main() throws IOException, InterruptedException {
		InetAddress destinationAddress = InetAddress.getByName(destinationIp);
        
		while (true) {
			Message message = outMessages.take();
			byte[] data = message.convertMessageToBytes();
			
			if (message instanceof AckMessage) {
				Logger.log("    ---    SEND ACK for message " + ((AckMessage)message).getAckMsgId());
			} else {
				Logger.log("    ---    SEND [" + message.getSenderId() + " -> " + message.getReceiverId() + "]| " + message.getMessageId() + " [TYPE: " + message.getMessageType() + "] [SIZE: " + data.length + "]");
			}
	        
	        DatagramPacket packet = new DatagramPacket(data, data.length, destinationAddress, destinationPort);
	        localSocket.send(packet);
		}
	}
	
	public void interrupt() {
		super.interrupt();
	    localSocket.close();
	}
	
	public void queueMessage(Message m) {
		outMessages.add(m);
	}
	
	public void run() {
		try {
			main();
		} catch (SocketException e) {
//			Helper.log("[Send Thread] " + e.toString());
		} catch (InterruptedException e) {
//			Helper.log("[Send Thread] " + e.toString());
		} catch (IOException e) {
			Logger.log("[Send Thread] " + e.toString());
		}
	}
}
