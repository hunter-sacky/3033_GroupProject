/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class server{	
	
	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	private static connection c;
	private static Socket sock;
	private static File f;
	
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
		f = new File("/Users/huntersacky/Desktop/3033_project/3033_GroupProject/files");//Enter default pathway here. Change between machines.
		sendText("1");
		startSession();
		
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		boolean auth = authenticate();
		//List commands, should be a do while loop
		int command = 0;
		String[] commands = {"UPLOAD","DOWNLOAD","RENAME","DELETE","EXIT SESSION"};
		sendText(""+commands.length);
		do{
			command = 0;
			sendText("Which command would you like to execute?");
			for (int i = 0; i < commands.length; i++){
				sendText("["+(i+1)+"] "+commands[i]);
			}
			command = Integer.parseInt(readText());
			//Verify if number is valid
			if (command<1 || command>5){
				sendText(""+(commands.length+1));
				sendText("INVALID CHOICE");
			}
			else{
				//Valid choice, make sure it's not the exit command
				if (command < 5){
					switch(command){
						case 1:
							upload();
							break;
						case 2:
							//download
							System.out.println("download");
							break;
						case 3:
							rename();
							break;
						case 4:
							delete();
							break;
					}
					//Used to set up for next iteration of loop.
					sendText(""+(commands.length));
				}
			}
		}while(command!=5);
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
		String[] pathnames = dirList("rename");
		int choice = Integer.parseInt(readText());
		//enforce proper choice
		while(choice<1||choice>pathnames.length){
			sendText("0");
			sendText("INVALID CHOICE. Please enter an integer between 1-"+pathnames.length);
			choice = Integer.parseInt(readText());
		}
		sendText("0");
		sendText("Please enter the new name for "+pathnames[choice-1]+":");
		String newName = readText();
		Path p = Paths.get(f.getPath()+File.separator+pathnames[choice-1]);
		Files.move(p, p.resolveSibling(newName),StandardCopyOption.REPLACE_EXISTING);
		//will return true if authorized... need to implement this method.
		return false;
	}
	public static boolean delete() throws IOException{
		String[] pathnames = dirList("delete");
		int choice = Integer.parseInt(readText());
		//enforce proper choice
		while(choice<1||choice>pathnames.length){
			sendText("0");
			sendText("INVALID CHOICE. Please enter an integer between 1-"+pathnames.length);
			choice = Integer.parseInt(readText());
		}
		Path p = Paths.get(f.getPath()+File.separator+pathnames[choice-1]);
		sendText("0");
		sendText("Are you sure you wish to delete "+ p.getFileName()+"? Y/N");
		boolean executeCommand=readText().toUpperCase().equals("Y")?true:false;
		if (executeCommand){
			//User wishes to proceed. Delete the file. Do need to add a check for directories
			Files.delete(p);
		}
		return true;
	}
	public static boolean upload() throws IOException{
		File test = new File(f.getPath()+File.separator+"test3.txt");
		System.out.println(test.getPath());
		System.out.println(test.length()+"yo");
		/*
			get size of file from client and filename
			clear the buffer
			make byte array size of file in bytes
			save the array as file?
			return true if operation successful
		*/
		return true;
	}
	public static String[] dirList(String option) throws IOException{
		String[] pathnames = f.list();
		sendText(""+pathnames.length);
		sendText("Which file would you like to "+option+"?");
		for (int i=1; i<=pathnames.length; i++){
			sendText("["+i+"] "+pathnames[i-1]);
		}
		return pathnames;
	}
	
}