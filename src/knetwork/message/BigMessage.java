package knetwork.message;

public class BigMessage extends Message {
	private static final long serialVersionUID = 394531243202487652L;
	private byte[] bytes;
	
	public BigMessage(int numBytes) {
		super();
		bytes = new byte[numBytes];
	}
	
	public byte[] getBytes() {
		return bytes;
	}

}
