//https://stackoverflow.com/questions/22928114/program-for-file-transfer-in-java-using-sockets

import java.net.*; 
import java.io.*;
 
public class fileTransfer {
	
	private File file;
	private Socket sock;
	private byte data[];
	
	public fileTransfer(String path, Socket sock){
		file = new File(path);
		this.sock = sock;
	}
	public boolean sendFile(){
		boolean sent = true;//double check after implementation.
		try{
			//Set max length of file to 2GB
			if (file.length() > Integer.MAX_VALUE){
				System.out.println("File exceeds 2GB limit. Upload failed");
				return false;
			}
			else{ //File is under 2GB. Good to send.
				data = new byte[2048];
				PrintWriter write = new PrintWriter(sock.getOutputStream());
				write.println(file.getName());
				write.flush();
				
				FileInputStream fileStream = new FileInputStream(file);
            	BufferedInputStream fileBuffer = new BufferedInputStream(fileStream);
            	OutputStream out = sock.getOutputStream();
            	int count;
            	while ((count = fileBuffer.read(data)) > 0) {
                	System.out.println("Data Sent : " + count);
                	out.write(data, 0, count);
                	out.flush();
            	}
            	out.close();
            	fileBuffer.close();
            	fileStream.close();
			}
		}
		catch (Exception e){
			System.out.println("Error encountered" + e.toString());
			sent = false;
		}
		return sent;
	}
	public boolean receiveFile(){
    	boolean received = true;
    	try {
        	InputStreamReader in = new InputStreamReader(sock.getInputStream());
			BufferedReader bf = new BufferedReader(in);
        	String fileName = bf.readLine();
       		in.close();
       		bf.close();
       		
       		
       		System.out.println("File Name : " + fileName);
        	byte data[] = new byte[2048]; // Here you can increase the size also which will receive it faster
        	FileOutputStream fileOut = new FileOutputStream("/Users/huntersacky/Desktop/3033_Project/3033_GroupProject/" + fileName);
        	InputStream fileIn = sock.getInputStream();
        	BufferedOutputStream fileBuffer = new BufferedOutputStream(fileOut);
        	int count;
        	int sum = 0;
        	while ((count = fileIn.read(data)) > 0) {
            	sum += count;
            	fileBuffer.write(data, 0, count);
            	System.out.println("Data received : " + sum);
            	fileBuffer.flush();
        	}
        	System.out.println("File Received...");
        	fileBuffer.close();
        	fileIn.close();
    	} 
    	catch (Exception e) {
        	System.out.println("Error : " + e.toString());
    	}
    	return received;
	}
}