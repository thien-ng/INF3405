import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientHandler extends Thread{
	
	private Socket socket;
	private int clientNumber;
	
	public ClientHandler(Socket socket, int clientNumber) {
		this.socket = socket;
		this.clientNumber = clientNumber;
	}
	
	public void run() {
		
		try {
			
			
			ObjectInputStream data = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
			String strings = (String) data.readObject();
			
			System.out.println(strings);
			
	
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
}
