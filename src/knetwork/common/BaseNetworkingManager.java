package knetwork.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

import knetwork.Constants;
import knetwork.message.AckMessage;
import knetwork.message.Message;
import knetwork.message.Message.MessageType;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	protected BlockingQueue<Message> inAcknowledgements;
	protected Map<Integer, Message> outAcknowledgements;
	
	protected BaseNetworkingManager(int inQueueSize) {
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
		inAcknowledgements = new ArrayBlockingQueue<Message>(inQueueSize);
		outAcknowledgements = new HashMap<Integer, Message>();
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			  public void run() {
				  System.out.println("NUM ACKS " + inAcknowledgements.size());
				  
				  Iterator<Message> iter = inAcknowledgements.iterator();
				  while (iter.hasNext()) {
					  AckMessage message = (AckMessage)iter.next(); // must be called before you can call i.remove()
					  outAcknowledgements.remove(message.getAckMsgId());
					  iter.remove();
				  }
				  
				  for (Message m : outAcknowledgements.values()) {
					  reSendReliableMessage(m);
				  }
			  }
		}, 1, Constants.RELIABILITY_ACKNOWLEDGEMENT_TIMEOUT);
	}
	
	protected abstract void reSendReliableMessage(Message m);
	protected abstract void sendMessageAcknowledgement(Message m);
	
	private void acknowledgeReliableMessage(Message message) {
		if (message != null && message.reliable) {
			sendMessageAcknowledgement(message);
		}
	}

	public Message recv() {
		Message message = inMessages.poll();
		acknowledgeReliableMessage(message);
		
		return message;
	}
	
	public Message recv_blocking() throws InterruptedException {
		Message message = inMessages.take();
		acknowledgeReliableMessage(message);
		
		return message;
	}
}
