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
import java.util.Arrays;
import java.util.Date;
import java.util.List;


//AJOUTER LE COMMAND LINE CONSOLE FORMAT

public class ClientHandler extends Thread{

	private String CONSOLE_FORMAT = "[%s:%d - %s]: %s";
	
	private int CHUNK_SIZE = 8 * 1024;
	
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
		
		loop:while(true) {
			try {
					in = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());

					runCommandLine(in.readUTF().toString());
		
			} catch (Exception e) {
				try {
					out.writeUTF(e.getMessage());
					out.flush();
				} catch (IOException e1) {
					System.out.print(String.format(CONSOLE_FORMAT, socket.getLocalAddress().toString().substring(1), socket.getLocalPort(), currentDate(), "Lost connection with client: " + clientNumber + "\n"));
					break;
				}
			}
		}
		
	}

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
	
	private void executeExit() throws IOException {
		socket.close();
		in.close();
		out.close();
	}

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
		directoryList.replaceAll(String::toLowerCase);

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

		if (commands.length != 1)
			throw new Exception("Unknown command");
		
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
		
		if (commands.length != 2) 
			throw new Exception("Must contain argument for directory name.");
		
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
	
	private void executeDownload(String[] commands) throws Exception {
		
		if (commands.length != 2)
			throw new Exception("Must contain argument for file name");
		
		File fileWanted = new File(System.getProperty("user.dir"), commands[1]);
		
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
	
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ mm:ss").format(new Date());
	}	
}
