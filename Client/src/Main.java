
public class Main {

	public static void main(String[] args) {
		
		ClientDisplay clientDisplay = new ClientDisplay();
		clientDisplay.getInformations();
		clientDisplay.initializeSocket();
		clientDisplay.startConsole();
		
	}

}
