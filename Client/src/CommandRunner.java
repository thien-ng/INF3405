import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandRunner {
	
	private SocketClient socketClient;

	private int portNumber;
	
	private String IpAddress;
	
	public CommandRunner(SocketClient socketClient, String IpAddress, int portNumber) {
		this.socketClient = socketClient;
		this.portNumber = portNumber;
		this.IpAddress = IpAddress;
	}
	
	public void runCommandLine(String commandLine) {
			
		socketClient.sendToServer(commandLine);
			
	}
	

}
