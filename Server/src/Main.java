import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
	
	private static ServerSocket listener;

	public static void main(String[] args) throws Exception{
		
		System.out.println("server started");
		
		int clientNumber = 0;
		
		listener = new ServerSocket();
		String serverAddress = "10.48.60.220";
		int serverPort = 5000;
		
		
		listener.setReuseAddress(true);
		InetAddress serverIP = InetAddress.getByName(serverAddress);
		
		listener.bind(new InetSocketAddress(serverIP, serverPort));
		
		System.out.format("The server is running on %s:%d", serverAddress, serverPort);
		
		try {
			while (true) {
	//			ServerSocket serverSocket = null;
	//			Socket socket = null;
	//			ObjectInputStream in = null;
	//			ObjectOutputStream out = null;
	//			
				
	//				serverSocket = new ServerSocket(5000);
					
	//				
	//				socket = serverSocket.accept();
	//				
	//				in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
	//				
	//				String strings = (String) in.readObject();
	//				
	//				System.out.println(strings);
	//				
	//				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
	//				out.writeObject("Yo");
	//				out.flush();
					
					
					new ClientHandler(listener.accept(), clientNumber++).start();
					
			}
		} catch (Exception e) {
			System.out.println("DAWDWAD"+e.getMessage());
		} finally {
				listener.close();
		}
	}

}
