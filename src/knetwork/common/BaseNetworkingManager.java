package knetwork.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.Constants;
import knetwork.message.AckMessage;
import knetwork.message.Message;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	protected BlockingQueue<Message> inAcknowledgements;
	protected Map<Integer, Message> outAcknowledgements;
	private Timer ackTimer;
	
	protected BaseNetworkingManager(int inQueueSize) {
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
		inAcknowledgements = new ArrayBlockingQueue<Message>(inQueueSize);
		outAcknowledgements = new HashMap<Integer, Message>();
		
		ackTimer = new Timer();
		ackTimer.scheduleAtFixedRate(new TimerTask() {
			  public void run() {
				  Iterator<Message> iter = inAcknowledgements.iterator();
				  while (iter.hasNext()) {
					  AckMessage message = (AckMessage)iter.next();
					  outAcknowledgements.remove(message.getAckMsgId());
					  iter.remove();
				  }
				  
				  for (Message m : outAcknowledgements.values()) {
					  reSendReliableMessage(m);
				  }
			  }
		}, 1, Constants.ACKNOWLEDGEMENT_TIMEOUT);
	}
	
	protected abstract void reSendReliableMessage(Message m);
	public abstract void sendMessageAcknowledgement(Message m);

	public Message recv() {
		Message message = inMessages.poll();
		return message;
	}
	
	public Message recv_blocking() {
		Message message = null;
		
		try {
			message = inMessages.take();
		} catch (InterruptedException e) {
		}
		
		return message;
	}
	
	public void disconnect() {
		ackTimer.cancel();
	}
}
