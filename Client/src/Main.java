/***********************************************
 * File: Main.java
 * Author: Jeremy Boulet, Duc-Thien Nguyen
 * Description: Main class for client
 *  
 ************************************************/
public class Main {
	
	/*
	 * Main
	 */
	public static void main(String[] args) {
		
		ClientDisplay clientDisplay = new ClientDisplay();
		
		boolean isNotConnected = true;
		
		while (isNotConnected) {
			
			clientDisplay.getInformations();
			try {
				clientDisplay.initializeSocket();
			} catch(Exception e) {
				System.out.println(e.getMessage());
				isNotConnected = true;
				continue;
			}
			isNotConnected = false;
		}
		clientDisplay.startConsole();
		
	}

}
