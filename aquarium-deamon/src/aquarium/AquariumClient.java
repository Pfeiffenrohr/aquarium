package aquarium;

import java.io.IOException;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

//import com.pi4j.util.Console;

public class AquariumClient {

	public static void main(String args[]) throws InterruptedException, IOException {

		String ip = args[0];
		//String ip = "192.168.2.22";
		Socket socket = null;
		socket = new Socket(ip, 8976);
		//System.out.println("1");
		Console input_reader = System.console();
		//System.out.println("2");
		// I'm just going to exit if the console is not provided
		// if (input_reader == null) {
		// System.err.println("No console.");
		// System.exit(1);
		//ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		System.err.println("2,5");
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois =null;

		System.err.println("3");
		String input="init";
		try {
			while (! input.equals("exit")) {

				//System.err.println("4");
				input = input_reader.readLine("Aquarium > ");
				// String message = (String) ois.readObject();
				oos.writeObject(input);
				if (ois==null)
				{
					System.err.println("Create ois");
				 ois = new ObjectInputStream(socket.getInputStream());
				}
				input_reader.printf((String) ois.readObject());
				input_reader.printf("");
				Thread.sleep(1000);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			socket.close();
		}

	}
}
