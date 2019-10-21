import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientDisplay {
	
	private String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
	private String CONSOLE_FORMAT = "[%s:%d - %s]: ";
	
	private int CHUNK_SIZE = 8 * 1024;

	private int portNumber;
	
	private String IpAddress;

	private Socket socketClient;
	
	private DataOutputStream objectOutput;
	
	private DataInputStream objectInput;
	
	public ClientDisplay() {
	}
	
	public void getInformations() {
		Scanner scan = new Scanner(System.in);
		String ipInput = "";
		int portNumber = 0;
		
		do {
			System.out.print("Enter a valid Ip Address: ");
			ipInput = scan.nextLine();
			
		} while (!ipInput.matches(REGEX_IP_ADDRESS));
		
		do {
			try {
				System.out.print("Enter a valid Port Number between 5000 and 5050: ");
				String portInput = scan.nextLine();
				portNumber = Integer.parseInt(portInput);
			} catch (Exception e) {
				portNumber = -1;
			}
			
		} while (portNumber < 5000 || portNumber > 5050);
		
		this.portNumber = portNumber;
		this.IpAddress = ipInput;

	}
	
	public void initializeSocket() {
		try {
			
			socketClient = new Socket(IpAddress, portNumber);
			objectOutput = new DataOutputStream(socketClient.getOutputStream());
			objectInput = new DataInputStream(socketClient.getInputStream());
	
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void startConsole() throws Exception {
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			System.out.print(String.format(CONSOLE_FORMAT, IpAddress, portNumber, currentDate()));
			String command = scan.nextLine();	
			String[] commands = command.split(" ");
			
			try {
				
				switch (commands[0]) {
					case "exit":
					case "Exit":
						System.out.println("Disconnecting...");
						
						objectOutput.writeUTF("exit");
						socketClient.close();
						scan.close();
						
						System.out.println("Disconnected");
						break;
						
					case "upload":
					case "Upload":
						executeUpload(command, commands);
						break;
					
					case "download":
					case "Download":
						executeDownload(command, commands);
						break;
					
					default:
						objectOutput.writeUTF(command);
						objectOutput.flush();
						
						String data = objectInput.readUTF();
						System.out.println(data);
				}				
			
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	private void executeUpload(String command, String[] commands) throws Exception {
		if (commands.length != 2)
			throw new Exception("Must contain argument for file name.");
		
		File uploadedFile = new File(System.getProperty("user.dir"), commands[1]);
		
		if (!uploadedFile.exists()) 	
			throw new Exception("File name does not exist.");
		
		objectOutput.writeUTF(command);	
			
		objectOutput.writeInt((int) uploadedFile.length());
		objectOutput.flush();
		
		byte[] buffer = new byte[CHUNK_SIZE];
		FileInputStream fis = new FileInputStream(commands[1]);
	
		int read;
		while((read = fis.read(buffer)) > 0)
			objectOutput.write(buffer, 0, read);
		
		fis.close();
	}
	
	private void executeDownload(String command, String[] commands) throws IOException {
		
		FileOutputStream fos = null;
		
		objectOutput.writeUTF(command);
		objectOutput.flush();
		
		try {			
			String currentDirectory = System.getProperty("user.dir");
			fos = new FileOutputStream(currentDirectory + "\\" +  commands[1]);			
			
			byte[] buffer = new byte[CHUNK_SIZE];
			
			int fileSize = objectInput.readInt();
			int read = 0;
			int remaining = fileSize;
			
			while((read = objectInput.read(buffer, 0, Math.min(buffer.length, remaining))) > 0 ) {
				remaining -= read;
				fos.write(buffer, 0 , read);
			}
			
		} catch (NumberFormatException e) {
			System.out.println("Arg error: " + e.getMessage());
		} finally {
			if (fos != null) fos.close();
		}	
	}
	
	
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ mm:ss").format(new Date());
	}	

}
