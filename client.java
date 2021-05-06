/*

	I used the code from: https://www.youtube.com/watch?v=-xKgxqG411c

*/
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class client{

	private static InputStreamReader in;
	private static BufferedReader bf;
	private static PrintWriter pr;
	private static Scanner input;
	private static Socket sock;
	private static boolean exec=true;
	private static File f;

	public static void main(String[] args) throws IOException{
		sock = new Socket("127.0.0.1", 1000);
		pr = new PrintWriter(sock.getOutputStream());
		input = new Scanner(System.in);
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		f = new File("/Users/huntersacky/Desktop/3033_project/3033_GroupProject/clientfiles");
		normalSession();
	}
	public static void normalSession() throws IOException{
		while (true){
			int i=readText();
			while(i-->-1){
				readText();
			}
			if (exec)
				sendText(input.nextLine());
		}
	}
	public static int readText() throws IOException{
		String str = bf.readLine();
		if (str.contains("LiNeCoUnt")){
			exec=true;
			return Integer.parseInt(str.split(",")[1]);
		}
		else if (str.contains("DoWnLoAd")){
			download(str);
		}
		else if (str.contains("UpLoAd")){
			upload();
		}
		else if (str.contains("ReNaMe")){
			rename(str);
		}
		else if (str.contains("DeLeTe")){
			exec=true;
			delete(str);
		}
		else{
			System.out.println(str);
		}
		return -1;
	}
	public static void sendText(String str) throws IOException{
		pr.println(str);
		pr.flush();
	}
	public static void download(String str) throws IOException{
		String[] arr = str.split(" ");
		System.out.println("Which file would you like to download?");
		String[] names = arr[1].split(",");
		for (int i=0; i<names.length; i++){
			if (i%2==0&&i!=0){
				System.out.println("["+((i/2)+1)+"] "+names[i]);
			}
			else if (i==0){
				System.out.println("[1] "+names[i]);
			}
		}
		int choice = checkChoice(input.nextInt(),names.length/2);
		sendText(choice+"");
		int filesize = Integer.parseInt(names[(choice*2)-1]);
		String filename = names[(choice*2)-2];
		//original code
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
      	System.out.println("File transfer completed.");
      	exec=false;
	}
	public static void rename(String str) throws IOException{
		System.out.println("Which file would you like to rename?");
		String[] fileNames=str.split(" ")[1].split(",");
		printFileNames(fileNames);
		int choice = checkChoice(input.nextInt(),fileNames.length);
		System.out.println("What would you like to rename "+fileNames[choice-1]+ " to: ");
		String newName=input.nextLine();
		sendText(choice+","+newName);
		exec=false;
	}
	public static void delete(String str) throws IOException{
		System.out.println("Which file would you like to delete?");
		String[] fileNames=str.split(" ")[1].split(",");
		printFileNames(fileNames);
		int choice = checkChoice(input.nextInt(),fileNames.length);
		//Ensure they want to delete this file
		System.out.println("Are you sure you want to delete "+fileNames[choice-1]+"? Y/N");
		char deleteVar = input.nextLine().toUpperCase().charAt(0);
		while(deleteVar!='Y'&&deleteVar!='N'){
			System.out.println("INVALID CHOICE. Please enter Y or N.");
			deleteVar=input.nextLine().toUpperCase().charAt(0);
		}
		if (deleteVar=='Y'){
			//delete it
			sendText("Y,"+(choice-1));
		}
		else{
			//don't delete it
			sendText("N,"+(choice-1));
		}
		exec=false;
	}
	public static void upload() throws IOException{
		System.out.println("Which file would you like to upload?");
		String[] pathNames = f.list();
		printFileNames(pathNames);
		int choice = checkChoice(input.nextInt(),pathNames.length);
		//now upload
		File uploadFile = new File(f.getPath()+File.separator+pathNames[choice-1]);
		long filesize = uploadFile.length();
		sendText(filesize+","+pathNames[choice-1]);
		System.out.println("Transferring "+pathNames[choice-1]+" to server.");
		
		byte [] mybytearray  = new byte [(int)filesize];
        FileInputStream fis = new FileInputStream(uploadFile);
        BufferedInputStream bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        OutputStream os = sock.getOutputStream();
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println("File transfer completed");
        exec=false;
		
	}
	public static int checkChoice(int choice, int items){
		while(choice<1||choice>items){
			System.out.println("INVALID CHOICE. Please enter an integer between 1-"+items);
			choice = input.nextInt();
		}
		input.nextLine(); //Clears the buffer
		return choice;
	}
	public static void printFileNames(String[] arr){
		for (int i=0; i<arr.length; i++){
			System.out.println("["+(i+1)+"] "+arr[i]);
		}
	}
}