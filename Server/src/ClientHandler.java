/***********************************************
 * File: ClientHandler.java
 * Author: Jeremy Boulet, Duc-Thien Nguyen
 * Description: Class to handle request from client
 *  
 ************************************************/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ClientHandler extends Thread{

	private final String CONSOLE_FORMAT = "[%s:%d - %s]: %s";
	
	private final int CHUNK_SIZE = 8 * 1024;
	
	private Socket socket;
	
	private DataInputStream in;
	
	private DataOutputStream out;

	private int clientNumber;
	
	private boolean isRunning;
	
	private String currentDirectory = "";
	
	/*
	 * Constructor
	 * Param: socket -> Socket
	 * 		  clientNumber -> int
	 *
	 */
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		this.isRunning = true;
		
		currentDirectory = System.getProperty("user.dir");
	}
	
	/*
	 * Method which is run whenever ClientHandler is initialized
	 */
	public void run() {
		
		System.out.println("working with client: " + clientNumber);
		
		while(isRunning) {
			try {
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());

					runCommandLine(in.readUTF().toString());
		
			} catch (Exception e) {
				try {
					out.writeUTF(e.getMessage());
					out.flush();
				} catch (IOException e1) {

					//prints when client is disconnected
					System.out.print(String.format(CONSOLE_FORMAT, socket.getLocalAddress().toString().substring(1), socket.getLocalPort(), currentDate(), "Lost connection with client: " + clientNumber + "\n"));
					break;
				}
			}
		}
		
	}
	
	/*
	 * Method to handle command requested by the client
	 * param: command -> String
	 */
	private void runCommandLine(String command) throws Exception {
		
		String[] commands = command.split(" ");
		System.out.print(String.format(CONSOLE_FORMAT, socket.getLocalAddress().toString().substring(1), socket.getLocalPort(), currentDate(), command + "\n"));
		
		switch(commands[0]) {
			
			case "cd":
				executeCD(commands);
				break;
			
			case "ls":
				executeLS(commands);
				break;
			
			case "mkdir":
				executeMkdir(commands);
				break;
				
			case "upload":
				executeUpload(commands);
				break;
				
			case "download":
				executeDownload(commands);
				break;
				
			case "exit":
				executeExit();
				break;
			
			default:
				out.writeUTF("Unknown command: " + commands[0]);
				out.flush();
				break;
		
		}
		
	}
	
	/*
	 * Method to execute exit command
	 */
	private void executeExit() throws IOException {
		socket.close();
		in.close();
		out.close();
		isRunning = false;
	}
	
	/*
	 * Method to change directory of user
	 * param: commands -> String[]
	 */
	private void executeCD(String[] commands) throws Exception{
		if (commands.length == 1) {
			out.writeUTF("");
			out.flush();
			return;
		}

		File file = new File(currentDirectory);
		
		//Search for all directories in current directory
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name.replaceAll("\\s+","")).isDirectory();
		  }
		});
		List<String> directoryList = Arrays.asList(directories);
		directoryList.replaceAll(String::toLowerCase);

		if (commands[1].equals("..")) {
			File directory = new File(currentDirectory);
			String dirName = directory.getParentFile().getName();
		
			currentDirectory = Paths.get(currentDirectory).getParent().toString();

			out.writeUTF("You are currently in directory: " + dirName);
			out.flush();
			
		} else if (directoryList.contains(commands[1].toLowerCase())) {
			currentDirectory += "\\" + commands[1];
			out.writeUTF("You are currently in directory: " + commands[1]);
			out.flush();
			
		} else {
			throw new Exception("The system cannot find the path specified.");
		}
		
	}
	
	/*
	 * Method to return all files and directories in current directory
	 * param: commands -> String[]
	 */
	private void executeLS(String[] commands) throws Exception {

		if (commands.length != 1)
			throw new Exception("Unknown command");
		
		File folder = new File(currentDirectory);
		List<String> files = new ArrayList<>();
		
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				files.add("[Folder] " + fileEntry.getName());
			} else {
				files.add("[File] " + fileEntry.getName());
			}
		}
		
		String filenames = "";
		for (String name : files) {
			filenames += (name + "\n");
		}

		out.writeUTF(filenames);
		out.flush();
	}
	
	/*
	 * Method to create a new directory in current directory
	 * param: commands -> String[]
	 */
	private void executeMkdir(String[] commands) throws Exception{
		
		if (commands.length != 2) 
			throw new Exception("Must contain argument for directory name.");
		
		File directory = new File(currentDirectory + "\\" + commands[1]);
		
		if (directory.exists()) {
			throw new Exception("Directory already exist");
		} else {
			directory.mkdirs();
			out.writeUTF("Folder " + commands[1] + " has been created");
			out.flush();
		}
		
	}
	
	/*
	 * Method to receive uploaded file from client
	 * param: commands -> String[]
	 */
	private void executeUpload(String[] commands) throws Exception {
		FileOutputStream fos = null;
		
		if (commands.length != 2)
			throw new Exception("Must contain argument for file name.");
		
		try {			
			fos = new FileOutputStream(currentDirectory + "\\" + commands[1]);
			
			byte[] buffer = new byte[CHUNK_SIZE];
			
			int fileSize = in.readInt();
			int read = 0;
			int remaining = fileSize;
			
			while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
				remaining -= read;
				fos.write(buffer, 0, read);
			}
		} catch (NumberFormatException e) {
			System.out.println("Arg error: " + e.getMessage());
		} finally {
			if (fos != null) fos.close();
		}	
	}

	/*
	 * Method to send to client file
	 * param: commands -> String[]
	 */
	private void executeDownload(String[] commands) throws Exception {
		
		if (commands.length != 2)
			throw new Exception("Must contain argument for file name");
		
		File fileWanted = new File(currentDirectory, commands[1]);
		
		if (!fileWanted.exists())
			throw new Exception("The file specified does not exist in the current repository");
		
		out.writeInt((int)fileWanted.length());
		out.flush();
		
		FileInputStream fis = new FileInputStream(commands[1]);
		byte[] buffer = new byte[CHUNK_SIZE];
		
		int read;
		while((read = fis.read(buffer)) > 0)
			out.write(buffer, 0, read);
		
		fis.close();
	}
	
	/*
	 * Method to format current date
	 */
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ hh:mm").format(new Date());
	}	
}
