import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		
		ClientDisplay clientDisplay = new ClientDisplay();
		clientDisplay.getInformations();
		clientDisplay.initializeSocket();
		clientDisplay.startConsole();
		
	}

}
