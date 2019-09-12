import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {
	
	private static Socket clientSocket;

	public SocketClient() {}
	
	public void initializeSocket(String IpAddress, int portNumber) {
		try {
			
			clientSocket = new Socket(IpAddress, portNumber);
			sendToServer("PENIS");
	
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public <T> void sendToServer(T object) {
		try {
		
			ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
			objectOutput.writeObject(object);
			objectOutput.flush();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
}
