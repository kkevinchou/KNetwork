package knetwork.common;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import knetwork.message.Message;

public abstract class BaseNetworkingManager {
	protected BlockingQueue<Message> inMessages;
	
	protected BaseNetworkingManager(int inQueueSize) {
		inMessages = new ArrayBlockingQueue<Message>(inQueueSize);
	}

	public Message recv() {
		return inMessages.poll();
	}
	
	public Message recv_blocking() throws InterruptedException {
		return inMessages.take();
	}
}
