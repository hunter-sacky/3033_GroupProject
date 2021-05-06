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
	//private static connection c;
	private static Socket sock;
	private static File f;
	private static String username;
	
	public static void main(String[] args) throws IOException{
		System.out.println("Server has started"); 
		ServerSocket serv = new ServerSocket(1000);
		sock = serv.accept();
		System.out.println(sock.getInetAddress());
		System.out.println("client connected");
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		pr = new PrintWriter(sock.getOutputStream());
		//connection c = new connection(sock);
		f = new File("/Users/huntersacky/Desktop/3033_project/3033_GroupProject/serverfiles");//Enter default pathway here. Change between machines.
		sendText("LiNeCoUnt,1");
		startSession();
		
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		boolean auth = authenticate();
		//List commands, should be a do while loop
		int command = 0;
		String[] commands = {"UPLOAD","DOWNLOAD","RENAME","DELETE","EXIT SESSION"};
		//sendText(""+commands.length);
		sendText("LiNeCoUnt,"+commands.length);
		do{
			sendText("Which command would you like to execute?");
			for (int i = 0; i < commands.length; i++){
				sendText("["+(i+1)+"] "+commands[i]);
			}
			command = Integer.parseInt(readText());
			//Verify if number is valid
			if (command<1 || command>5){
				//sendText(""+(commands.length+1));
				sendText("LiNeCoUnt,"+(commands.length+1));
				sendText("INVALID CHOICE");
			}
			else if (command < 5){
				switch(command){
					case 1:
						upload();
						break;
					case 2:
						download();
						break;
					case 3:
						rename();
						break;
					case 4:
						delete();
						break;
				}
				//Used to set up for next iteration of loop.
				//sendText(""+(commands.length));
				sendText("LiNeCoUnt,"+commands.length);
			}
		}while(command!=5);
		System.exit(0);
	}
	public static boolean authenticate() throws IOException{
		sendText("Username: ");
		username = readText();
		sendText("LiNeCoUnt,0");
		sendText("Password: ");
		String password = readText();
		System.out.println(username+" Logged into the server.");
		return true;
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
		String[] pathNames=f.list();
		String fileNames=dirList(pathNames);
		sendText("LiNeCoUnt,0");
		sendText("ReNaMe "+fileNames);
		String[] userInput = readText().split(",");
		Path p = Paths.get(f.getPath()+File.separator+pathNames[Integer.parseInt(userInput[0])-1]);
		Files.move(p, p.resolveSibling(userInput[1]),StandardCopyOption.REPLACE_EXISTING);
		System.out.println(username+" renamed "+pathNames[Integer.parseInt(userInput[0])-1]+" to "+userInput[1]);
		return false;
	}
	public static boolean delete() throws IOException{
		String[] pathnames = f.list();
		String fileNames = dirList(pathnames);
		sendText("DeLeTe "+fileNames);
		String[] userInput=readText().split(",");
		char choice = userInput[0].charAt(0);
		int num=Integer.parseInt(userInput[1]);
		//continue with the logic
		if (choice=='Y'){
			Path p = Paths.get(f.getPath()+File.separator+pathnames[num]);
			Files.delete(p);
			System.out.println(username+" deleted "+pathnames[num]+" from the server.");
			return true;
		}
		return false;
	}
	public static boolean download() throws IOException{
		//File test = new File(f.getPath()+File.separator+"test3.txt");
		//long filesize = test.length();
		String[] pathnames = f.list();
		String fileNames = "";
		for (int i=0; i<pathnames.length; i++){
			File temp = new File(f.getPath()+File.separator+pathnames[i]);
			if (i<pathnames.length-1){
				fileNames+=pathnames[i]+","+temp.length()+",";
			}
			else{
				fileNames+=pathnames[i]+","+temp.length();
			}
		}
		sendText("DoWnLoAd "+fileNames);
		int option = Integer.parseInt(readText());
		File sendFile = new File(f.getPath()+File.separator+pathnames[option-1]);
		sendText(sendFile.length()+"");
		//https://www.rgagnon.com/javadetails/java-0542.html
		//Console information
		System.out.println("Transferring "+sendFile.getName()+" to user: "+username);
		
        byte [] mybytearray  = new byte [(int)sendFile.length()];
        FileInputStream fis = new FileInputStream(sendFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        OutputStream os = sock.getOutputStream();
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println(username+" downloaded "+pathnames[option-1]+" from the server.");
		return true;
	}
	public static boolean upload() throws IOException{
		sendText("UpLoAd");
		//now download
		String str = readText();
		String[] arr = str.split(",");
		long filesize = Long.parseLong(arr[0]);
		String filename = arr[1];
		byte [] mybytearray  = new byte [(int)filesize];
      	InputStream is = sock.getInputStream();
      	FileOutputStream fos = new FileOutputStream(f.getPath()+File.separator+filename);
      	BufferedOutputStream bos = new BufferedOutputStream(fos);
      	int bytesRead = is.read(mybytearray,0,mybytearray.length);
      	int current = bytesRead;
      	do {
        	bytesRead =
         		is.read(mybytearray, current, (mybytearray.length-current));
        	if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1 && current<bytesRead);

      	bos.write(mybytearray, 0 , current);
      	bos.flush();
      	System.out.println(username+" uploaded "+filename+" to the server.");
		return true;
	}
	public static String dirList(String[] pathNames) throws IOException{
		String fileNames="";
		for (int i=0; i<pathNames.length; i++){
			if (i+1<pathNames.length)
				fileNames+=pathNames[i]+",";
			else
				fileNames+=pathNames[i];
		}
		return fileNames;
	}
	
}