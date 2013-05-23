package knetwork.common;

import knetwork.Constants;

public abstract class Logger {
	public static void log(String s) {
		if (Constants.DEBUG_LOG) {
			System.out.println(s);
		}
	}
	
	public static void error(String s) {
		if (Constants.DEBUG_ERROR) {
			System.out.println(s);
		}
	}
}
