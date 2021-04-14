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

	public static void main(String[] args) throws IOException{
		Socket sock = new Socket("127.0.0.1", 1000);
		pr = new PrintWriter(sock.getOutputStream());
		input = new Scanner(System.in);
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		
		startSession();
	}
	public static void startSession() throws IOException{
		readText();
		readText();
		sendText(input.nextLine());
		normalSession();
	}
	public static void normalSession() throws IOException{
		while (true){
			readText();
			sendText(input.nextLine());
		}
	}
	public static void readText() throws IOException{
		
		String str = bf.readLine();
		System.out.println("server : "+str);
	}
	public static void sendText(String str) throws IOException{
		pr.println(str);
		pr.flush();
	}
}