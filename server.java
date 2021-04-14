/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/

import java.net.*;
import java.io.*;

public class server{	
	
	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Server has started"); 
		ServerSocket serv = new ServerSocket(1000);
		Socket sock = serv.accept();
		
		System.out.println("client connected");
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		pr = new PrintWriter(sock.getOutputStream());
		
		startSession();
		/*int i=0;
		while (i++<10){
			readText();
			sendText("Welcome to the server");
		}*/
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		boolean auth = authenticate();
	}
	public static boolean authenticate() throws IOException{
		sendText("Username: ");
		String username = readText();
		sendText("Password: ");
		String password = readText();
		System.out.println("Username = "+username+"\nPassword = "+password);
		return false;//Change this just for testing purposes.
	}
	
	public static String readText() throws IOException{
		//Gets the input stream from the socket.
		//InputStreamReader in = new InputStreamReader(sock.getInputStream());
		//Creates a buffer reader object to read the input stream.
		//BufferedReader bf = new BufferedReader(in);
		
		String str = bf.readLine();
		return str;
	}
	public static void sendText(String text) throws IOException{
		//PrintWriter pr = new PrintWriter(sock.getOutputStream());
		pr.println(text);
		pr.flush();
	}
	
}