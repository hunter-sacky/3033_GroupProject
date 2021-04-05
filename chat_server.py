import socket
import select
from threading import *
import sys


server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
"""
the first argument AF_INET is the address domain of the socket. This is used when we have an Internet Domain
with any two hosts
The second argument is the type of socket. SOCK_STREAM means that data or characters are read in a continuous flow
"""
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

IP_address = "10.0.0.64"
Port = 1020
server.bind((IP_address, Port)) 
#binds the server to an entered IP address and at the specified port number. The client must be aware of these parameters
server.listen(100)
#listens for 100 active connections. This number can be increased as per convenience
list_of_clients=[]

def clientthread(conn, addr):
    conn.send("Welcome to this chatroom!")
    #sends a message to the client whose user object is conn
    while True:
            try:     
                message = conn.recv(2048)    
                if message:
                	if "exit" in message:
                		print("closing the program")
                		sys.exit()
			else:
				print("<" + addr[0] + "> " + message)
                    		message_to_send = "<" + addr[0] + "> " + message
                    		broadcast(message_to_send,conn)
                    		#prints the message and address of the user who just sent the message on the server terminal
                else:
                    remove(conn)
            except:
                continue

def broadcast(message,connection):
    for clients in list_of_clients:
        if clients!=connection:
            try:
                clients.send(message)
            except:
                clients.close()
                remove(clients)

def remove(connection):
    if connection in list_of_clients:
        list_of_clients.remove(connection)

while True:
    conn, addr = server.accept()
    """
    Accepts a connection request and stores two parameters, conn which is a socket object for that user, and addr which contains
    the IP address of the client that just connected
    """
    list_of_clients.append(conn)
    print(addr[0] + " connected")
    #maintains a list of clients for ease of broadcasting a message to all available people in the chatroom
    #Prints the address of the person who just connected
    #start_new_thread(clientthread,(conn,addr))
    clientthread(conn,addr) #changed this part. It originally called a function that didn't exist.
    #creates and individual thread for every user that connects

conn.close()
server.close()


'''
This code is from my server.py program. Used for the headersize stuff
def send_message(message,sock):
        #This sends the message to the server with a 10 character header before so that the client knows$
        message=f'{len(message):<{HEADERSIZE}}' + message
        sock.send(bytes(message, "utf-8"))
        
        
def receive_message(sock):
        full_message = ''
        new_message = True
        exit_condition = True
        message_length = 0
        while exit_condition:
                message = sock.recv(16)
                if new_message:
                        message_length = int(message[:HEADERSIZE])
                        new_message = False
                full_message += message.decode("utf-8")
                if len(full_message) - HEADERSIZE == message_length:
                        print(full_message[HEADERSIZE:])
                        new_message = True
                        full_message = ''
                        exit_condition = False
'''
