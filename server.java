/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class server{	
	
	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	private static connection c;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Server has started"); 
		ServerSocket serv = new ServerSocket(1000);
		Socket sock = serv.accept();
		System.out.println(sock.getInetAddress());
		System.out.println("client connected");
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		pr = new PrintWriter(sock.getOutputStream());
		connection c = new connection(sock);
		//System.out.println(c.receiveMessage());
		startSession();
		
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		boolean auth = authenticate();
		//List commands, should be a do while loop
		int command = 0;
		String[] commands = {"UPLOAD","DOWNLOAD","RENAME","DELETE","MOVE","EXIT SESSION"};
		do{
			sendText("Which command would you like to execute?");
			for (int i = 0; i < commands.length; i++){
				sendText("["+(i+1)+"] "+commands[i]);
			}
			command = Integer.parseInt(readText());
			System.out.println(command);
		}while(command>0);
	}
	public static boolean authenticate() throws IOException{
		sendText("Username: ");
		String username = readText();
		sendText("Password: ");
		String password = readText();
		c = new connection(username, password, true);

		return c.getAuthentication();//Change this just for testing purposes.
	}
	
	public static String readText() throws IOException{
		String str = bf.readLine();
		return str;
	}
	public static void sendText(String text) throws IOException{
		pr.println(text);
		pr.flush();
	}
	
}