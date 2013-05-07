package knetwork.common;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.message.Message;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	protected Set<Integer> reliableSendIds;
	
	protected BaseNetworkingManager(int inQueueSize) {
		reliableSendIds = new HashSet<Integer>();
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
	}

	public Message recv() {
		return inMessages.poll();
	}
	
	public Message recv_blocking() throws InterruptedException {
		return inMessages.take();
	}
}
