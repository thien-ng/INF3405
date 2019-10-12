import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
public class Main {
	
	private static ServerSocket listener;

	public static void main(String[] args) throws Exception{
		
		System.out.println("server started");
		
		int clientNumber = 0;
		
		listener = new ServerSocket();
		String serverAddress = "192.168.2.34";
		int serverPort = 5000;
		
		
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		
		System.out.format("The server is running on %s:%d", serverAddress, serverPort);


		try {
			while (true) {
	//			ObjectOutputStream out = null;
	//				
	//				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	//				out.writeObject("Yo");
	//				out.flush();

					new ClientHandler(listener.accept(), clientNumber++).start();
					
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			listener.close();
		}
	}

}
