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

	public static void main(String[] args) throws IOException{
		sock = new Socket("127.0.0.1", 1000);
		pr = new PrintWriter(sock.getOutputStream());
		input = new Scanner(System.in);
		
		in = new InputStreamReader(sock.getInputStream());
		bf = new BufferedReader(in);
		startSession();
	}
	public static void startSession() throws IOException{
		readText(true);
		readText(false);		
		readText(false);
		sendText(input.nextLine());
		normalSession();
	}
	public static void normalSession() throws IOException{
		while (true){
			int i=readText(true);
			while (i>-1){
				readText(false);
				i--;
			}
			sendText(input.nextLine());
		}
	}
	public static int readText(boolean expect) throws IOException{
		String str = bf.readLine();
		//Used for multi-line communication so that the client knows how many lines to expect.
		if (expect){
			return Integer.parseInt(str);
		}
		System.out.println("server : "+str);
		return -1;
	}
	public static void sendText(String str) throws IOException{
		pr.println(str);
		pr.flush();
	}
}