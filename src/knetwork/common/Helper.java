package knetwork.common;

import knetwork.Constants;

public abstract class Helper {
	public static void log(String s) {
		if (Constants.DEBUG_MODE) {
			System.out.println(s);
		}
	}
}
