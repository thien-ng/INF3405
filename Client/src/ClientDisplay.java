import java.util.Scanner;

public class ClientDisplay {
	
	private String REGEX_IP_ADDRESS = "\\b((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)(\\.|$)){4}\\b";
	
//	private String CONSOLE_FORMAT = "[%s:%d - ]"
	
	private SocketClient socketClient;
	
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
			System.out.print("Enter a valid Port Number between 5000 and 5050: ");
			String portInput = scan.nextLine();
			portNumber = Integer.parseInt(portInput);
			
		} while (portNumber < 5000 || portNumber > 5050);
		
		this.portNumber = portNumber;
		this.IpAddress = ipInput;

		socketClient.initializeSocket(ipInput, portNumber);
	}
	
	public void startConsole() {
		Scanner scan = new Scanner(System.in);
		
		while (true) {
			System.out.println(IpAddress);
			
			
		}
		
		
	}
	
	

}
