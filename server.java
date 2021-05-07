/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/


import java.net.*;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class server{	
	
	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	//private static Socket sock;
	private static SSLSocket sock;
	private static File f;
	private static String username, serverDirectory;
	private static ArrayList<String[]> permissionsMatrix;
	private static InputStream is; 
	

	public static void main(String[] args) throws IOException{
		/*System.out.println("Server has started"); 
		ServerSocket serv = new ServerSocket(1000);
		sock = serv.accept();
		System.out.println(sock.getInetAddress());
		System.out.println("client connected");
		serverDirectory="/home/rarealton/school/3033_GroupProject";
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		pr = new PrintWriter(sock.getOutputStream());
		permissionsMatrix = new ArrayList<String[]>();
		//populate the array list from permissions.txt
		Scanner file = new Scanner(new File("permissions.txt"));
		while(file.hasNextLine()){
			permissionsMatrix.add(file.nextLine().split(","));
		}
		
		f = new File(serverDirectory);//Enter default pathway here. Change between machines.
	//	newFile("test123.txt");
		sendText("LiNeCoUnt,1");
		startSession();*/
		try{
			SSLServerSocketFactory sslserversocketfactory =
        		(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
      		SSLServerSocket serv =
        		(SSLServerSocket)sslserversocketfactory.createServerSocket(1000);
      		SSLSocket sock = (SSLSocket)serv.accept();
      		is = sock.getInputStream();
      		//InputStream inputstream = sock.getInputStream();
		//in = new InputStreamReader(inputstream);
		in = new InputStreamReader(is);
      		bf = new BufferedReader(in);
      		pr = new PrintWriter(sock.getOutputStream());
      		System.out.println("Client Connected");
      		permissionsMatrix = new ArrayList<String[]>();
      		Scanner file = new Scanner(new File("permissions.txt"));
      		while (file.hasNextLine()){
      			permissionsMatrix.add(file.nextLine().split(","));
      		}
      		serverDirectory="/home/rarealton/school/3033_GroupProject/files";
      		f=new File(serverDirectory);
      		sendText("LiNeCoUnt,1");
      		startSession();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	public static void startSession() throws IOException{
		sendText("Welcome to the Secure File Management Application. Before proceeding please authenticate below.");
		//boolean auth = authenticate();
		
		do{
		System.out.print("");
		}while(!authenticate());//run again if failed to login
		
		//List commands, should be a do while loop
		int command = 0;
		String[] commands = {"UPLOAD","DOWNLOAD","RENAME","DELETE","EXIT SESSION"};
		/*//sendText(""+commands.length);
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
		System.exit(0);*/
		boolean[] commandsPerm = new boolean[commands.length];
		commandsPerm[0]=checkPermission("[DIRECTORY]",0);
		commandsPerm[1]=checkPermission("[DIRECTORY]",1);
		commandsPerm[2]=checkPermission("[DIRECTORY]",2);
		commandsPerm[3]=commandsPerm[2];
		commandsPerm[4]=true;
		int count=0;
		for (boolean b:commandsPerm)
			count+=b?1:0;
		
		do{
			sendText("LiNeCoUnt,"+count);
			sendText("Which command would you like to execute?");
			int temp=1;
			for (int i=0; i<commands.length; i++){
				if (commandsPerm[i]){
					sendText("["+temp+"] "+commands[i]);
					temp++;
				}
			}
			command = Integer.parseInt(readText());
			if (command<1 || command>count){
				sendText("LiNeCoUnt,"+(commands.length+1));
				sendText("INVALID CHOICE");
			}
			else{
				//Valid choice. Now to figure out what the choice is
				int c=0;
				for (int i=0; i<commandsPerm.length; i++){
					if (commandsPerm[i]){
						c++;
					}
					if (c==command){
						switch(i+1){
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
							case 5:
								writePermFile();
								System.exit(0);
								break;
						}						
					}
				}
			}
		} while(true);
	}
	public static boolean authenticate() throws IOException{
		sendText("Username: ");
		username = readText();
		sendText("LiNeCoUnt,0");
		sendText("Password: ");
		String password = readText();
		//start of bash script to check authentication
		String[] command = {"./test.sh", username, password};
		Process process = Runtime.getRuntime().exec(command);
		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = null;
			while ((s = reader.readLine()) != null){
				System.out.println(s);
				if(s.equals("1")) {
					System.out.println(username+ " Logged into the server.");
					return true;
				}
				else {
					System.out.println("Invalid username or password");
					return false;
				}
			}
		
		
		System.out.println(username+" Logged into the server.");
		return true;
	}
	
	public static String readText() throws IOException{
		String str = bf.readLine();
		return str;
	}
	public static void sendText(String text) throws IOException{
		//pr = new PrintWriter(sock.getOutputStream());
		pr.println(text);
		pr.flush();
		//pr.close();
	}
	
	//The methods below are used to manipulate the file system
	public static boolean rename() throws IOException{
	//!rename denotes no permission for the directory. Will return error if they don't have auth to file either
		
			//They have permissions to rename in the directory. Now check for file perms on each case./*String[] pathNames=f.list();
			String[] pathNames=f.list();
			String fileNames=dirList(pathNames,2);
			//change what is contained in pathnames
			pathNames=fileNames.split(",");
			if (pathNames[0].length()>1){
				sendText("LiNeCoUnt,0");
				sendText("ReNaMe "+fileNames);
				String[] userInput = readText().split(",");
				Path p = Paths.get(f.getPath()+File.separator+pathNames[Integer.parseInt(userInput[0])-1]);
				Files.move(p, p.resolveSibling(userInput[1]),StandardCopyOption.REPLACE_EXISTING);
				System.out.println(username+" renamed "+pathNames[Integer.parseInt(userInput[0])-1]+" to "+userInput[1]);
				updateFile(new String[]{p.getFileName().toString(),userInput[1]},1);//of name, new name
			}
			
		return false;
		
		
		/*String[] pathNames=f.list();
		String fileNames=dirList(pathNames);
		sendText("LiNeCoUnt,0");
		sendText("ReNaMe "+fileNames);
		String[] userInput = readText().split(",");
		Path p = Paths.get(f.getPath()+File.separator+pathNames[Integer.parseInt(userInput[0])-1]);
		Files.move(p, p.resolveSibling(userInput[1]),StandardCopyOption.REPLACE_EXISTING);
		System.out.println(username+" renamed "+pathNames[Integer.parseInt(userInput[0])-1]+" to "+userInput[1]);
		return false;*/
	}
	public static boolean delete() throws IOException{
		String[] pathnames = f.list();
		String fileNames = dirList(pathnames,2);
		pathnames=fileNames.split(",");
		if (pathnames[0].length()>1){
			sendText("DeLeTe "+fileNames);
			String[] userInput=readText().split(",");
			char choice = userInput[0].charAt(0);
			int num=Integer.parseInt(userInput[1]);
			//continue with the logic
			if (choice=='Y'){
				Path p = Paths.get(f.getPath()+File.separator+pathnames[num]);
				Files.delete(p);
				System.out.println(username+" deleted "+pathnames[num]+" from the server.");
				updateFile(new String[]{pathnames[num],pathnames[num]},2);
				return true;
			}
		}
		return false;
		//original code below
		/*String[] pathnames = f.list();
		String fileNames = dirList(pathnames,2);
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
		return false;*/
	}
	public static boolean download() throws IOException{
		//File test = new File(f.getPath()+File.separator+"test3.txt");
		//long filesize = test.length();
		String[] pathnames = f.list();
		String fileNames = dirList(pathnames,1);
		pathnames=fileNames.split(",");
		fileNames="";
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
        //pr.close();
	OutputStream os = new WriterOutputStream(pr);
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println(username+" downloaded "+pathnames[option-1]+" from the server.");
		//pr = new PrintWriter(sock.getOutputStream());
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
      	      	//InputStream is = sock.getInputStream();
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
      	updateFile(new String[]{filename,filename},3);
		return true;
	}
	public static String dirList(String[] pathNames, int operation) throws IOException{
		//returns new pathnames, string of the available ones
		ArrayList<String> list = new ArrayList<String>();
		String ret="";
		for (int i=0; i<pathNames.length; i++){
			//Verify if this file can have that operation done on it
			if (checkPermission(pathNames[i],operation)){
				list.add(pathNames[i]);
			}
		}
		//Creates the listing of files accessible to user.
		for (int i=0; i<list.size(); i++){
			if (i+1<list.size())
				ret+=list.get(i)+",";
			else
				ret+=list.get(i);
		}
		return ret;
		/*String fileNames="";
		for (int i=0; i<pathNames.length; i++){
			if (i+1<pathNames.length)
				fileNames+=pathNames[i]+",";
			else
				fileNames+=pathNames[i];
		}
		return fileNames;*/
	}
	public static boolean checkPermission(String fileName, int operation){
		/*
			First, get users place in the list
			First check directory permissions. ArrayList<String>.get(1)
			Then check file permissions
			Operations:
				upload: 0
				download: 1
				rename/delete: 2
		*/
		int userPlace = 0;
		for (int i=0; i<permissionsMatrix.get(0).length; i++){
			if (username.equals(permissionsMatrix.get(0)[i])){
				userPlace=i;
				break;
			}
		}
		//Get the row number for the fileName string
		
		int row=0;
		for (int i=0; i<permissionsMatrix.size(); i++){
			if (permissionsMatrix.get(i)[0].equals(fileName)){
				row=i;
				break;
			}
		}
		String perm = permissionsMatrix.get(row)[userPlace];
		//rwx permissions.
		if (operation==1){
			//Read permission: Download
			if (perm.contains("r"))
				return true;
		}
		else if (operation==0||operation==3){
			//Write permission: Upload
			if (perm.contains("w"))
				return true;
		}
		else{
			//Execute permission: Rename, Delete
			if (perm.contains("x"))
				return true;
		}
		return false;
	}
	public static void updateFile(String[] s, int operation) throws IOException{
		/*
		String[] s contains original file name and updated file name
		1=rename
		2=delete
		3=upload, makes new entry
		*/
		if (operation!=3){
			int row=0;
			for (int i=0; i<permissionsMatrix.size(); i++){
				if (permissionsMatrix.get(i)[0].equals(s[0])){
					row=i;
					break;
				}
			}
			if (operation==1){
				permissionsMatrix.get(row)[0]=s[1];	
			}
			else{
				permissionsMatrix.remove(row);
			}
		}
		else{
			String[] temp = new String[permissionsMatrix.get(1).length];
			temp[0]=s[1];
			for (int i=1; i<permissionsMatrix.get(1).length; i++){
				temp[i]=permissionsMatrix.get(1)[i];
			}
			permissionsMatrix.add(temp);
		}
		writePermFile();
	}
	public static void writePermFile() throws IOException{
		//Write state of permissionsMatrix to permissions.txt
		FileWriter temp = new FileWriter("permissions.txt");
		for (int i=0; i<permissionsMatrix.size(); i++){
			String line="";
			String[] arr=permissionsMatrix.get(i);
			for (int j=0; j<arr.length; j++){
				if (j<arr.length-1)
					line+=arr[j]+",";
				else
					line+=arr[j];
			}
			if (i<permissionsMatrix.size()-1)
				temp.write(line+"\n");
			else
				temp.write(line);
		}
		temp.close();
	}
	
}
