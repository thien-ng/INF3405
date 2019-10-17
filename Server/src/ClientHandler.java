import java.io.BufferedInputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientHandler extends Thread{

	private String CONSOLE_FORMAT = "[%s:%d - %s]: ";
	
	private String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
	private Socket socket;

	private int clientNumber;
	
	
	private String currentDirectory = "";
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
		
		currentDirectory = System.getProperty("user.dir");
	}
	
	public void run() {
		
		System.out.println("working with client: " + clientNumber);
		
		try {
			ObjectInputStream data = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			String command = (String) data.readObject();
			System.out.println(command);
			runCommandLine(command);
			
	
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	private void runCommandLine(String command) {
		
		String[] commands = command.split(" ");
		
		System.out.println(commands[0]);
		
		switch(commands[0]) {
			
			case "cd":
				runCd(commands);
				break;
			
			case "ls":
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
	
	private void runCd(String[] commands) {
		
		System.out.println("RUN CD");

		List<String> directoryList = new ArrayList<>();

		try {
			@SuppressWarnings("resource")
			Stream<Path> walk = Files.walk(Paths.get(currentDirectory));
			directoryList = walk.filter(Files::isDirectory)
									  .map(Path::getFileName)
									  .map(x -> x.toString())
									  .collect(Collectors.toList());


		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (directoryList.contains(commands[1].toLowerCase())) {
			currentDirectory += "\\" + commands[1];
			System.out.println(currentDirectory);
		}
		System.out.println(currentDirectory);
	}
	
}
