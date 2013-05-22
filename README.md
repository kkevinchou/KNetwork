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
    If the message is dropped during transmission it is resent until an acknowledgement is received
   
DOCUMENTATION
========
	Message types below 100 are reserved for internal use

MESSAGE HEADER
========
	4 bytes - Message Id
	4 bytes - Sequence Number
	4 bytes - Sender Id
	4 bytes - Receiver Id
	4 bytes - Reliable
	
	4 bytes - Message Type
	
REGISTRATION REQUEST
========

REGISTRATION RESPONSE
========
	
ACKNOWLEDGEMENT
========
	
TODO
========

    Ensure message ordering for reliable messages
    Allow the sending/receiving raw byte arrays.  Serialized objects are too large to contain in a single packet.
