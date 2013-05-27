package knetwork.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import knetwork.Constants;
import knetwork.message.messages.AckMessage;
import knetwork.message.messages.Message;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	protected BlockingQueue<AckMessage> inAcknowledgements;
	protected Map<Integer, Message> outAcknowledgements;
	private Timer ackTimer;
	
	protected BaseNetworkingManager(int inQueueSize) {
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
		inAcknowledgements = new ArrayBlockingQueue<AckMessage>(inQueueSize);
		outAcknowledgements = new HashMap<Integer, Message>();
		
		ackTimer = new Timer();
		ackTimer.scheduleAtFixedRate(new TimerTask() {
			  public void run() {
				  Iterator<AckMessage> iter = inAcknowledgements.iterator();
				  while (iter.hasNext()) {
					  AckMessage message = iter.next();
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
	
	public Message recv_timeout(long timeoutInMilliSeconds) {
		Message message = null;
		
		try {
			message = inMessages.poll(timeoutInMilliSeconds, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
		
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
