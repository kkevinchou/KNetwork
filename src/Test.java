import knetwork.message2.Message2;


public class Test {

	public static void main(String[] args) {
		Message2 message = new Message2();
		byte[] bytes = message.getRawBytes();
		for (int i = 0; i < bytes.length; i++) {
			System.out.println(bytes[i]);
		}
	}

}
