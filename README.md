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

MESSAGE FOOTER
========

	4 bytes - Message Id
	4 bytes - Sequence Number
	4 bytes - Sender Id
	4 bytes - Receiver Id
	4 bytes - Reliable
	
TODO
========

    Ensure message ordering for reliable messages
    Handling registration requests/responses being dropped