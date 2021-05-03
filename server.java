/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class server{	
	
	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	private static connection c;
	private static Socket sock;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Server has started"); 
		ServerSocket serv = new ServerSocket(1000);
		sock = serv.accept();
		System.out.println(sock.getInetAddress());
		System.out.println("client connected");
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		pr = new PrintWriter(sock.getOutputStream());
		connection c = new connection(sock);
		//System.out.println(c.receiveMessage());
		sendText("1");
		startSession();
		
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		boolean auth = authenticate();
		//List commands, should be a do while loop
		int command = 0;
		String[] commands = {"UPLOAD","DOWNLOAD","RENAME","DELETE","MOVE","EXIT SESSION"};
		sendText(""+commands.length);
		do{
			command = 0;
			sendText("Which command would you like to execute?");
			for (int i = 0; i < commands.length; i++){
				sendText("["+(i+1)+"] "+commands[i]);
			}
			command = Integer.parseInt(readText());
			//Verify if number is valid
			if (command<1 || command>6){
				sendText(""+(commands.length+1));
				sendText("INVALID CHOICE");
			}
			else{
				//Valid choice, make sure it's not the exit command
				if (command < 6){
					switch(command){
						case 1:
							//upload
							System.out.println("upload");
							break;
						case 2:
							//download
							System.out.println("download");
							break;
						case 3:
							//rename
							System.out.println("rename");
							rename();
							break;
						case 4:
							//delete
							System.out.println("delete");
							break;
						case 5:
							//move
							System.out.println("move");
							break;
					}
					//Used to set up for next iteration of loop.
					sendText(""+(commands.length));
				}
			}
		}while(command!=6);
		System.exit(0);
	}
	public static boolean authenticate() throws IOException{
		sendText("Username: ");
		String username = readText();
		sendText("0");
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
	
	//The methods below are used to manipulate the file system
	public static boolean rename() throws IOException{
		File f = new File("/Users/huntersacky/Desktop/3033_project/3033_GroupProject/files");//Enter default pathway here. Change between machines.
		String[] pathnames = f.list();
		sendText(""+pathnames.length);
		sendText("Which file would you like to rename?");
		for (int i=1; i<=pathnames.length; i++){
			sendText("["+i+"] "+pathnames[i-1]);
		}
		int choice = Integer.parseInt(readText());
		//enforce proper choice
		
		//will return true if sucessful operation
		return false;
	}
	
}