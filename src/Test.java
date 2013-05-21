import knetwork.message.Message2;


public class Test {

	public static void main(String[] args) {
		Byte[] byteArray = new Byte[4];
		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = new Byte("127");
		}
		
		Message2 message = new Message2(byteArray);
		Byte[] byteArray2 = message.bytes;
		for (int i = 0; i < byteArray2.length; i++) {
			System.out.println(byteArray2[i].toString());
		}
	}

}
