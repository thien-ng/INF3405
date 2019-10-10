import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
		Socket clientSocket = null;
		String IP_ADDRESS = "10.48.60.220";
		System.out.println("test");
		try {
			try {
				clientSocket = new Socket(IP_ADDRESS, 5000);
				
				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
				
				objectOutput.writeObject(new String(commandLine));
				objectOutput.flush();
				
				ObjectInputStream obj = new ObjectInputStream(clientSocket.getInputStream());
				
				System.out.println("receiving " + obj.readObject());
				
			} finally {
				clientSocket.close();
			}
			
		} catch (Exception e) {
			System.out.println("test " + e.getMessage());
		}
	}
	

}
