import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;
public class Main {
	
	private static ServerSocket listener;
	
	private static String SERVER_IP = "192.168.2.28";

	public static void main(String[] args) throws Exception{
		
		int clientNumber = 0;
		int serverPort = getInformations();

		
		InetAddress serverIP = InetAddress.getByName(SERVER_IP);
		listener = new ServerSocket();
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		listener.setReuseAddress(true);
		
		System.out.format("The server is running on %s:%d\n", SERVER_IP, serverPort);


		try {
			while (true) {

					new ClientHandler(listener.accept(), clientNumber++).start();
					
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
//			listener.close();
		}
	}
	
	public static int getInformations() {
		Scanner scan = new Scanner(System.in);
		int portNumber = 0;
		
		do {
			try {
				System.out.print("Enter a valid Port Number between 5000 and 5050: ");
				String portInput = scan.nextLine();
				portNumber = Integer.parseInt(portInput);
			} catch (Exception e) {
				portNumber = -1;
			}
			
		} while (portNumber < 5000 || portNumber > 5050);
		
		return portNumber;
	}

}
