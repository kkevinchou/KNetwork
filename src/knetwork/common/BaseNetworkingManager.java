package knetwork.common;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.message.Message;
import knetwork.message.Message.MessageType;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	protected Set<Integer> reliableSendIds;
	
	protected BaseNetworkingManager(int inQueueSize) {
		reliableSendIds = new HashSet<Integer>();
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
	}
	
	protected abstract void sendReliabilityAcknowledgement(Message m);
	
	protected void handleReliableReceive() {
		Message m = inMessages.peek();
		
		if (m == null || m.getMessageType() != MessageType.Acknowledge) {
			return;
		}
		
		inMessages.poll();
		reliableSendIds.remove(m.getMessageId());
	}
}
