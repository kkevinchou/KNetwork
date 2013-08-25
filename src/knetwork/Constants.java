package knetwork;

// Constants for KNetwork
public class Constants {
	public static final int SEND_THREAD_QUEUE_SIZE = 1000;
	public static final int SERVER_IN_QUEUE_SIZE = 10000;
	public static final int CLIENT_IN_QUEUE_SIZE = 1000;
	public static final int MAX_UDP_BYTE_READ_SIZE = 1400;
	public static final int SERVER_ID = 0;
	public static final int UNASSIGNED_CLIENT_ID = -1;
	public static final long ACKNOWLEDGEMENT_TIMEOUT = 5 * 1000;

	public static final int MESSAGE_HEADER_SIZE = 7 * 4;
	public static final int PROTOCOL_ID = 19900302;

	public static final int MAX_RESERVED_MESSAGE_TYPE = 9;
	public static final int CLIENT_REGISTRATION_TIMEOUT = 2 * 1000;

	public static final int MAX_RETRY_CONNECTION_ATTEMPTS = 2;
}
