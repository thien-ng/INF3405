import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class ClientDisplay {
	
	private String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
	private String CONSOLE_FORMAT = "[%s:%d - %s]: ";
	
	private SocketClient socketClient;
	
	private CommandRunner commandRunner;
	
	private int portNumber;
	
	private String IpAddress;
	
	public ClientDisplay() {
		socketClient = new SocketClient();
	}
	
	public void getInformations() {
		Scanner scan = new Scanner(System.in);
		String ipInput = "";
		int portNumber = 0;
		
		do {
			System.out.print("Enter a valid Ip Address: ");
			ipInput = scan.nextLine();
			
		} while (!ipInput.matches(REGEX_IP_ADDRESS));
		
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

		socketClient.initializeSocket(ipInput, portNumber);
		commandRunner = new CommandRunner(socketClient, ipInput, portNumber);
	}
	
	public void startConsole() {
		Scanner scan = new Scanner(System.in);
		

		while (true) {
			System.out.print(String.format(CONSOLE_FORMAT, IpAddress, portNumber, currentDate()));
			String command = scan.nextLine();
			commandRunner.runCommandLine(command);
		}
	}
	
	
	private String currentDate() {
		return new SimpleDateFormat("yyyy-MM-dd @ mm:ss").format(new Date());
	}
	
	

}
