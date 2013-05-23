KNetwork
========

A UDP Networking library intended for games using a Client/Server model

Features
--------

* Client/Server message-based communication
* Broadcasting server messages to all clients
* Extendable messaging system
* Discard badly formatted messages
* The option to send reliable messages.  If a message is dropped during transmission (i.e. timesout) it is resent until an acknowledgement is received
* Discard old and stale messages that are not marked as reliable
	
Usage
--------

Include the packages to your java project
	
### 1.0 Define Your Message Type ###
	
Message types are at it's lowest level an integer.
User defined message types should be defined starting from 10 to INTEGER.MAX
	
### 2.0 Define Your Message ###

1. Subclass the Message class and provide the super constructor with your message's message type (Integer).  This will be passed back to you when reconstructing your message
2. Be sure to override generateMessageBodyBytes() so that the network manager knows how to serialize your message
3. Finally, you'll want to implement some sort of static method for reconstructing your message.  This method should be called from your subclass of MessageFactory to reconstruct the message when it's received
	
### 3.0 Define Your Message Factory ###

1. Subclass the MessageFactory class to specify how you wish to reconstruct messages received over the network
2. Override the buildMessageBody() method to control the message reconstruction process
3. The arguments provided are:
	* The received packet (DatagramPacket)
	* The message type (Integer)
	* The message body (MessageBody)
4. The message body contains a ByteBuffer which can be used to extract different dataTypes
5. The data you receive will again depend on how you serialized your message in generateMessageBodyBytes()
6. If your MessageFactory fails to construct a message, it uses the default MessageFactory to try and construct a message.  This will occur for example, when receving the registration, registration response, or acknowlegement message

Look at the knetwork.test package for an example message type, message, and message factory implementation

### 4.0 Message Format ###

	4 bytes - Protocol Id
	4 bytes - Message Type
	4 bytes - Message Id
	4 bytes - Sequence Number
	4 bytes - Sender Id
	4 bytes - Receiver Id
	4 bytes - Reliable
	(Message specific data)
	
TODO
--------

* Ensure message ordering for reliable messages
* Handling registration requests/responses being dropped
* Piggyback acks onto sent packets
* Sequence number wrapping
