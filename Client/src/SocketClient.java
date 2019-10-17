import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketClient {
	
	private static Socket clientSocket;

	public SocketClient() {}
	
	public void initializeSocket(String IpAddress, int portNumber) {
		try {
			
			clientSocket = new Socket(IpAddress, portNumber);
	
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public <T> void sendToServer(T object) {
		try {
		
			DataOutputStream objectOutput = new DataOutputStream(clientSocket.getOutputStream());
			objectOutput.writeUTF((String) object);
			objectOutput.flush();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
}
