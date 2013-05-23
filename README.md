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
	
### Message Types ###
	
	Message types are at it's lowest level an integer.
	User defined message types should be defined starting from 10 to INTEGER.MAX
	
TODO
========

    Ensure message ordering for reliable messages
    Handling registration requests/responses being dropped
