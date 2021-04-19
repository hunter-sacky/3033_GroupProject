import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.net.*;
import java.io.*;

public class connection{
	
	private String username, passwordHash;
	private boolean authentication = false;
	private Socket sock;//This can be deleted possibly. Just testing options.
	private PrintWriter pw;
	private InputStreamReader input;
	private BufferedReader buffRead;
	
	public connection(String username, String password, boolean authentication){
		setUsername(username);
		setHash(password);
		setAuthentication(authentication);
		//this.sock = sock;
	}
	public connection (Socket sock) throws IOException{
		this.sock=sock; //This is a test for functionality.
		pw = new PrintWriter(this.sock.getOutputStream());
		input = new InputStreamReader(this.sock.getInputStream());
		buffRead = new BufferedReader(input);
	}
	public void sendMessage(String message){
		pw.println(message);
		pw.flush();		
	}
	public String receiveMessage() throws IOException{
		String str = buffRead.readLine	();
		return str;
	}
	
	
	public boolean sendFile(String filename){
		return false;
	}
	public boolean getFile(int length){
		return false;
	}
	
	
	
	
	
	
	
	/*
	SET METHODS / TOSTRING
	*/
	public void setHash (String password){
		/*
			Using this tutorial for MD5 in Java: https://www.geeksforgeeks.org/md5-hash-in-java/
		*/
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(password.getBytes());
			BigInteger no = new BigInteger(1, digest);
			passwordHash = no.toString(16);
			while (passwordHash.length() < 32){
				passwordHash = "0" + passwordHash;
			}
		}
		catch (NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		
	}
	public void setUsername (String username){
		this.username = username;
	}
	public void setAuthentication (boolean authentication){
		this.authentication = authentication;
	}
	public boolean getAuthentication(){
		return authentication;
	}
	public String toString(){
		return username+"\n"+passwordHash+"\n"+authentication;
	}
}