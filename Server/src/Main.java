import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	public static void main(String[] args) {
		
		System.out.println("server started");
		
		while (true) {
			ServerSocket serverSocket = null;
			Socket socket = null;
			ObjectInputStream in = null;
			ObjectOutputStream out = null;
			
			try {
				serverSocket = new ServerSocket(5000);
				
				socket = serverSocket.accept();
				
				in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				
				String strings = (String) in.readObject();
				
				System.out.println(strings);
				
				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				out.writeObject("Yo");
				out.flush();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					serverSocket.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
