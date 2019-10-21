/***********************************************
 * File: Main.java
 * Author: Jeremy Boulet, Duc-Thien Nguyen
 * Description: Main class for server
 *  
 ************************************************/

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {
	
	private static ServerSocket listener;
	
	/*
	 * Main
	 */
	public static void main(String[] args) throws IOException {
		
		int clientNumber = 0;
		int serverPort = getInformations();

		listener = new ServerSocket(serverPort);
		listener.setReuseAddress(true);
		
		System.out.format("The server is running on %d\n", serverPort);


		try {
			while (true) {
					// Create new thread for every new client connected
					new ClientHandler(listener.accept(), clientNumber++).start();
					
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			listener.close();
		}
	}
	
	
	/*
	 * Method to ask user to input port number
	 */
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
