package knetwork;

import knetwork.Settings;

public abstract class Util {
    public static long getTick() {
        return System.currentTimeMillis();
    }

	public static void log(String s) {
		if (Settings.DEBUG_LOG) {
			System.out.println(s);
		}
	}

	public static void error(String s) {
		if (Settings.DEBUG_ERROR) {
			System.out.println(s);
		}
	}
}
