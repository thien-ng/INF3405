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
			
	}
	

}
