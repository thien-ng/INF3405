import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandRunner {
	
	private String CONSOLE_FORMAT = "[%s:%d - %s]: %s is not recognized as an internal or external command,\r\n" + 
			"operable program or batch file.";
	
	private SocketClient socketClient;
	
	private int portNumber;
	
	private String IpAddress;
	
	public CommandRunner(SocketClient socketClient, String IpAddress, int portNumber) {
		this.socketClient = socketClient;
		this.portNumber = portNumber;
		this.IpAddress = IpAddress;
	}
	
	public void runCommandLine(String commandLine) {
		String command = commandLine.toLowerCase();	
		String[] commandParts = command.split(" ");
		
		if (command.startsWith("cd")) {
			
		}
		
		else {
			
			switch(commandParts[0]) {
			
				case "ls":
					handeLs(commandParts);
					break;
				case "mkdir":
					handleMkdir(commandParts);
					break;
				case "upload":
					handleUpload(commandParts);
					break;
				case "download":
					handleDownload(commandParts);
					break;
				case "exit":
					handleExit(commandParts);
					break;
				default:
					System.out.println(String.format(CONSOLE_FORMAT, IpAddress, portNumber, currentDate(), command));
			
			}
			
		}
	}
	
	private void handleCd(String command) {
		String commandWithoutSpace = command.replace(" ","");
		if (commandWithoutSpace == "cd..") {
			// change to parent repo
		}
		
	}
	
	private void handeLs(String[] commandParts) {
		System.out.println("LS");
	}
	
	private void handleMkdir(String[] commandParts) {
		
	}
	
	private void handleUpload(String[] commandParts) {
		
	}
	
	private void handleDownload(String[] commandParts) {
		
	}
	
	private void handleExit(String[] commandParts) {
		
	}
	
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ mm:ss").format(new Date());
	}

}
