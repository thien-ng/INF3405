import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Main {

	public static void main(String[] args) {
		
		ClientDisplay test = new ClientDisplay();
		

		test.getInformations();

		
	}
	
//	private static void instanciateSocket() {
//		Socket clientSocket = null;
//		String IP_ADDRESS = "10.200.27.199";
//		System.out.println("test");
//		try {
//			try {
//				clientSocket = new Socket(IP_ADDRESS, 5000);
//				
//				ObjectOutputStream objectOutput = new ObjectOutputStream(clientSocket.getOutputStream());
//				
//				objectOutput.writeObject(new String("PENIS BANDE"));
//				objectOutput.flush();
//				
//				ObjectInputStream obj = new ObjectInputStream(clientSocket.getInputStream());
//				
//				System.out.println("receiving " + obj.readObject());
//				
//			} finally {
//				clientSocket.close();
//			}
//			
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//	}

}
