KNetwork
========

    A UDP Networking library intended for games using a Client/Server model

Features
========

    Client registration with a server
    Clients sending/receiving messages to/from the server
    
    Server receiving messages from clients
    Server broadcasting messages to clients
    Server sending a message to a single client
    
    The ability to send reliable messages
    If the message is dropped during transmission (i.e. timesout) it is resent until an acknowledgement is received

MESSAGE HEADER
========

	4 bytes - Protocol Id
	4 bytes - Message Type
	4 bytes - Message Id
	4 bytes - Sequence Number
	4 bytes - Sender Id
	4 bytes - Receiver Id
	4 bytes - Reliable
	
USAGE
========
	Include the packages to your java project
	
### 1.0 Define Your Message Type ###
	
	Message types are at it's lowest level an integer.
	User defined message types should be defined starting from 10 to INTEGER.MAX
	
### 2.0 Define Your Message ###	

	Subclass the Message class and provide the super constructor with your message's message type (integer)
	Be sure to override generateMessageBodyBytes() so that the network manager knows how to serialize your message
	Finally, you'll want to implement some sort of static method for reconstructing your message
		This method should be called from your very own subclass of MessageFactory to reconstruct the message when it is received
	
### 3.0 Define Your Message Factory ###

	Subclass the MessageFactory class to specify how you wish to reconstruct messages received over the network
	Override the buildMessageBody() method to control the message reconstruction process
	The arguments provided are:
		* The received packet (DatagramPacket)
		* The message type (integer)
		* The message body
	The message body contains a ByteBuffer which can be used to extract different dataTypes
	The data you receive will again depend on how you serialized your message in generateMessageBodyBytes()
	If your MessageFactory fails to construct a message, it uses the default MessageFactory to try and construct a message
		This will occur for example, when the registration, registration response, and acknowledgement messages are sent
	
TODO
========

    Ensure message ordering for reliable messages
    Handling registration requests/responses being dropped
