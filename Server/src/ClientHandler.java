import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


//AJOUTER LE COMMAND LINE CONSOLE FORMAT

public class ClientHandler extends Thread{

	private String CONSOLE_FORMAT = "[%s:%d - %s]: ";
	
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
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	private void runCommandLine(String command) throws Exception {
		
		String[] commands = command.split(" ");
		
		System.out.println(commands[0]);
		
		switch(commands[0]) {
			
			case "cd":
				executeCD(commands);
				break;
			
			case "ls":
				executeLS(commands);
				break;
			
			case "mkdir":
				break;
				
			case "upload":
				break;
				
			case "download":
				break;
				
			case "exit":
				break;
		
		}
		
	}
	
	@SuppressWarnings("resource")
	private void executeCD(String[] commands) throws Exception{
		if (commands.length == 1) {
			return;
		}

		File file = new File(currentDirectory);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
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

		System.out.println(currentDirectory);
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
	}
	
}