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
		clientDisplay.getInformations();
		clientDisplay.initializeSocket();
		clientDisplay.startConsole();
		
	}

}
