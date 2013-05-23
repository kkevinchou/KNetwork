package knetwork.test;

import knetwork.message.Message;

public class Test {
	public static void main(String[] args) {
		Message message = null;
		
		if (message instanceof Message) {
			System.out.println("MEOW");
		} else {
			System.out.println("JAH");
		}
	}

}
