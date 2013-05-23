package knetwork.test;

import knetwork.common.Logger;

public class TestMessageTypes {
	public enum TestMessageType {
		NULL(-1), TEST_TYPE(10);

		private final int value;

		private TestMessageType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}

		public static int convertToInt(TestMessageType type) {
			return type.getValue();
		}

		public static TestMessageType convertToEnum(int num) {
			switch (num) {
			case 10:
				return TEST_TYPE;
			default:
				Logger.log("[TestMessageType] Unmatched conversion to enum for value " + num);
				return NULL;
			}
		}
	}
}
