import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


//AJOUTER LE COMMAND LINE CONSOLE FORMAT

public class ClientHandler extends Thread{

	private String CONSOLE_FORMAT = "[%s:%d - %s]: %s";
	
	private String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
	private Socket socket;
	
	private DataInputStream in;
	
	private DataOutputStream out;

	private int clientNumber;
	
	
	private String currentDirectory = "";
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		
		currentDirectory = System.getProperty("user.dir");
	}
	
	public void run() {
		
		System.out.println("working with client: " + clientNumber);
		
		while(true) {
			try {
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());

					runCommandLine(in.readUTF().toString());
		
			} catch (Exception e) {
				try {
					out.writeUTF(e.getMessage());
					out.flush();
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
		
	}
	
	private void runCommandLine(String command) throws Exception {
		
		String[] commands = command.split(" ");
		// TODO remove dash before ip address
		System.out.print(String.format(CONSOLE_FORMAT, socket.getLocalAddress(), socket.getLocalPort(), currentDate(), command + "\n"));
		
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
				break;
				
			case "exit":
				break;
		
		}
		
	}
	
	// PROBLEME AVEC CD pour ~directoryList.contains(commands[1].toLowerCase()
	@SuppressWarnings("resource")
	private void executeCD(String[] commands) throws Exception{
		if (commands.length == 1) {
			out.writeUTF("");
			out.flush();
			return;
		}

		File file = new File(currentDirectory);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name.replaceAll("\\s+","")).isDirectory();
		  }
		});
		List<String> directoryList = Arrays.asList(directories);
		System.out.println(Arrays.toString(directories));

		if (commands[1].equals("..")) {
			currentDirectory = Paths.get(currentDirectory).getParent().toString();
			
		} else if (directoryList.contains(commands[1].toLowerCase())) {
			currentDirectory += "\\" + commands[1];
			
		} else {
			throw new Exception("The system cannot find the path specified.");
		}
		
		out.writeUTF("");
		out.flush();
		
	}
	
	private void executeLS(String[] commands) throws Exception {

		if (commands.length != 1) {
			throw new Exception("Unknown command");
		}
		
		File file = new File(currentDirectory);
		String[] directories = file.list();
		String filenames = "";
		for (String name : directories) {
			filenames += (name + "\n");
		}

		out.writeUTF(filenames);
		out.flush();
	}
	
	private void executeMkdir(String[] commands) throws Exception{
		
		if (commands.length != 2) {
			throw new Exception("Must contain argument for directory name.");
		}
		
		File directory = new File(currentDirectory + "\\" + commands[1]);
		
		if (directory.exists()) {
			throw new Exception("Directory already exist");
		} else {
			directory.mkdirs();
			out.writeUTF("");
			out.flush();
		}
		
	}
	
	private void executeUpload(String[] commands) throws Exception {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		if (commands.length != 3) {
			throw new Exception("Must contain argument for file name.");
		}
		
		try {			
			int fileSize = Integer.parseInt(commands[2]);
			byte[] byteArray = new byte [fileSize];
			fos = new FileOutputStream(currentDirectory + "\\" +  commands[1]);
			bos = new BufferedOutputStream(fos);
			int bytesRead = in.read(byteArray, 0, byteArray.length);
			int current = bytesRead;
			
			do {
				bytesRead = in.read(byteArray, current, (byteArray.length - current));
				if (bytesRead >= 0) current += bytesRead;
			} while (bytesRead < fileSize);
			
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
