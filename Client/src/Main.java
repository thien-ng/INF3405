import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class Main {

	public static void main(String[] args) {
		
		ClientDisplay clientDisplay = new ClientDisplay();
		clientDisplay.getInformations();
		clientDisplay.startConsole();
		
	}

}
