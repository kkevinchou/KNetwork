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

MESSAGE FORMAT
========
	4 bytes - Message Type
	4 bytes - Sender Id
	4 bytes - Receiver Id
TODO
========

    Ensure message ordering for reliable messages
    Allow the sending/receiving raw byte arrays.  Sending a serialized object can take up a lot of bandwidth
