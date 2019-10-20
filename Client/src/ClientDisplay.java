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
	
	public void startConsole() throws IOException {
		Scanner scan = new Scanner(System.in);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		
		while (true) {
			System.out.print(String.format(CONSOLE_FORMAT, IpAddress, portNumber, currentDate()));
			String command = scan.nextLine();	
			String[] commands = command.split(" ");
			
			try {
				
				switch (commands[0]) {
					case "upload":
						executeUpload(command, commands, fis, bis);
						break;
					
					case "download":
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
			} finally {
				if (bis != null) bis.close();
			}
			
			
		}
	}
	
	private void executeUpload(String command, String[] commands, FileInputStream fis, BufferedInputStream bis) throws IOException {
		File uploadedFile = new File(System.getProperty("user.dir"), commands[1]);
		if (uploadedFile.exists()) {						
			byte[] byteFile = new byte[(int)uploadedFile.length()];
			fis = new FileInputStream(uploadedFile);
			bis = new BufferedInputStream(fis);
			bis.read(byteFile, 0, byteFile.length);
			
			objectOutput.write(byteFile, 0, byteFile.length);
			objectOutput.flush();
			objectOutput.writeUTF(command + " " + uploadedFile.length());
			objectOutput.flush();
		}
	}
	
	private void executeDownload(String command, String[] commands) throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		objectOutput.writeUTF(command);
		objectOutput.flush();
		
		try {			
			String currentDirectory = System.getProperty("user.dir");
			String[] response = objectInput.readUTF().split(" ");
	
			byte[] byteArray = new byte [Integer.parseInt(response[1])];
			fos = new FileOutputStream(currentDirectory + "\\" +  commands[1]);
			bos = new BufferedOutputStream(fos);
			int read = objectInput.read(byteArray, 0, byteArray.length);
			int current = read;
			
			while (read > 0) {
				read = objectInput.read(byteArray, current, (byteArray.length - current));
				if (read >= 1) current += read;
			}
			
			bos.write(byteArray, 0, current);
			bos.flush();
		} catch (NumberFormatException e) {
			System.out.println("Arg error: " + e.getMessage());
		} finally {
			if (fos != null) fos.close();
			if (bos != null) bos.close();
		}	
	}
	
	
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ mm:ss").format(new Date());
	}	

}
