/***********************************************
 * File: ClientDisplay.java
 * Author: Jeremy Boulet, Duc-Thien Nguyen
 * Description: Class containing methods displaying
 * 				console to client and the connection
 * 				between client and server
 *  
 ************************************************/

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientDisplay {
	
	private final String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
	private final String CONSOLE_FORMAT = "[%s:%d - %s]: ";
	
	private final int CHUNK_SIZE = 8 * 1024;

	private int portNumber;
	
	private String IpAddress;

	private Socket socketClient;
	
	private DataOutputStream objectOutput;
	
	private DataInputStream objectInput;
	
	private boolean isRunning;
	
	
	/*
	 * Constructeur
	 */
	public ClientDisplay() {
		this.isRunning = true;
	}
	
	/*
	 * Méthode qui demande au client l'ip et le port du serveur
	 */
	public void getInformations() {
		Scanner scan = new Scanner(System.in);
		String ipInput = "";
		int portNumber = 0;
		
		// Demande pour l'ip
		do {
			System.out.print("Enter a valid Ip Address: ");
			ipInput = scan.nextLine();
			
		} while (!ipInput.matches(REGEX_IP_ADDRESS));
		
		// Demande pour le port
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
	
	/*
	 *  Méthode utilisée pour initialiser le socket pour communiquer avec le serveur
	 *  Elle initialise aussi data output et input stream
	 */
	@SuppressWarnings("resource")
	public void initializeSocket() throws Exception{
		
		socketClient = new Socket(IpAddress, portNumber);
		objectOutput = new DataOutputStream(socketClient.getOutputStream());
		objectInput = new DataInputStream(socketClient.getInputStream());

	}
	
	/*
	 * Méthode qui affiche la console au client
	 */
	public void startConsole() {
		Scanner scan = new Scanner(System.in);
		
		while (isRunning) {
			System.out.print(String.format(CONSOLE_FORMAT, IpAddress, portNumber, currentDate()));
			String command = scan.nextLine();	
			String[] commands = command.split(" ");
			
			try {
				switch (commands[0].toLowerCase()) {
					case "exit":
						System.out.println("Disconnecting...");
						
						objectOutput.writeUTF("exit");
						socketClient.close();
						scan.close();
						isRunning = false;
						
						System.out.println("Disconnected");
						break;
						
					case "upload":
						executeUpload(command, commands);
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
			
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
		}
	}
	
	/*
	 * Méthode qui upload un fichier au serveur
	 * Params: command -> 	String : commande fait par le client
	 * 		   commands -> 	String[] : Tableau des commandes fait par le client
	 * Throws: Exception
	 */
	private void executeUpload(String command, String[] commands) throws Exception {
		if (commands.length != 2)
			throw new Exception("Must contain argument for file name.");
		
		File uploadedFile = new File(System.getProperty("user.dir"), commands[1]);
		
		if (!uploadedFile.exists()) 	
			throw new Exception("File name does not exist.");
		
		objectOutput.writeUTF(command);	
			
		objectOutput.writeLong( uploadedFile.length());
		objectOutput.flush();
		
		byte[] buffer = new byte[CHUNK_SIZE];
		FileInputStream fis = new FileInputStream(commands[1]);
	
		int read;
		while((read = fis.read(buffer)) > 0)
			objectOutput.write(buffer, 0, read);
		
		fis.close();
		
		String message = objectInput.readBoolean() ? "File uploaded" : "Error while uploading";
		System.out.println(message);
	}
	
	/*
	 * Méthode qui download un fichier du serveur
	 * Params: command -> 	String : commande fait par le client
	 * 		   commands -> 	String[] : Tableau des commandes fait par le client
	 * Throws: IOException
	 */
	private void executeDownload(String command, String[] commands) throws IOException {
		
		FileOutputStream fos = null;
		
		objectOutput.writeUTF(command);
		objectOutput.flush();
		
		// return si le fichier existe pas
		if (!objectInput.readBoolean())
			return;
		
		try {			
			String currentDirectory = System.getProperty("user.dir");
			fos = new FileOutputStream(currentDirectory + "\\" +  commands[1]);			
			
			byte[] buffer = new byte[CHUNK_SIZE];
			
			long fileSize = objectInput.readLong();
			int read = 0;
			long remaining = fileSize;
			
			while((read = objectInput.read(buffer, 0, Math.min(buffer.length, (int)remaining))) > 0 ) {
				remaining -= read;
				fos.write(buffer, 0 , read);
			}
			
			System.out.println("File downloaded");
			
		} catch (NumberFormatException e) {
			System.out.println("Arg error: " + e.getMessage());
		} finally {
			if (fos != null) fos.close();
		}	
	}
	
	
	/*
	 * Méthode pour formater la date
	 */
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ hh:mm").format(new Date());
	}	

}
