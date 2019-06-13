package aquarium;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.ServerSocket;
import java.net.Socket;

public class CommunicationThread implements Runnable{
	private ServerSocket server;
	public Hashtable env;
	Logging log;
	
	
	public CommunicationThread()
	{
		
	}
	public void run() {
	
		try {
			//int port = Integer.parseInt(strport);
		    int port= 8976;
			server = new ServerSocket(port);
			 log.writeLog(2, "Communicationconsole gestarted!");
			handleConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void handleConnection() {
		
		
		// System.out.println("Waiting for client message...");
		 log.writeLog(5, "Handle Connection");
		//
		// The server do a loop here to accept all connection initiated by the
		// client application.
		//
		while (true) {			
				 log.writeLog(5, "In while");
					try {
				
				Socket socket = server.accept();
				socket.setSoTimeout(3600000);			
				 log.writeLog(5, "Connectionhandler");
				new ConnectionHandler(socket,env,log);
				 log.writeLog(5, "Connectionhandler started");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

class ConnectionHandler implements Runnable {
	private Socket socket;
	boolean debug = true;
	Hashtable env;
	Logging log;

	public ConnectionHandler(Socket socket,Hashtable env,Logging log) {
		this.socket = socket;
		this.env=env;
		this.log = log;
		Thread t = new Thread(this);
		
		 log.writeLog(5, "Starting Thread");
		t.start();
	}

	public void run() {
		try {
			//
			// Read a message sent by client application
			//
			
			System.out.println("Thread is running");
			ObjectInputStream ois = new ObjectInputStream(
					socket.getInputStream());
			 ObjectOutputStream oos = new ObjectOutputStream(
                     socket.getOutputStream());
			String message;
			String input  [];

			
			while (true)
			{
			log.writeLog(7, "Warte auf Nachricht ...");
			message = (String) ois.readObject();		
			log.writeLog(7, "Message =  >" + message+"<");
			input=message.split(" ");
			
			// Vector dat=new Vector();
			if ( input[0].equals("exit"))
			{
				oos.writeObject("Good by");
				ois.close();
				oos.close();
				break;
			}
			String str="";
			if ( input[0].equals("help"))
			 {
				
				str="commands: ";
				str = str + "i: Zeigt Infos \n";
				str = str + "getTemp: Zeigt Temperatur \n";
				str = str + "status: Zeigt Status von Luft, Temperatur und Heizung \n";
				str = str + "set debug <1-10>: Setzt den Debuglevel \n";
			oos.writeObject(str);
			continue;

			}
			if ( input[0].equals("i"))
			{
				  Date akt = new Date();
				 long diff = akt.getTime() - ((Date) env.get("startzeit")).getTime();
				 
				 double tag = Math.floor(diff / (1000*60*60*24));
		         diff = diff % (1000*60*60*24);
		         double std = Math.floor(diff / (1000*60*60));
		         diff = diff % (1000*60*60);
		         double min = Math.floor(diff / (1000*60));
		         diff = diff % (1000*60);
		         double sec = Math.floor(diff / 1000);
		         double mSec = diff % 1000;
				
				
				str="Laufzeit: "+tag+" Tage, " + std + " Stdunden, " + min + " Minuten, " + sec + " Sekunden \n";
				Iterator<Map.Entry>  it;
				Map.Entry            entry;
				
				it = env.entrySet().iterator();
				while (it.hasNext()) {
				    entry = it.next();
				   
				     str=str+entry.getKey().toString() + " " +
				        entry.getValue().toString()+"\n";
				}
				if (log.isActive)
				{
					str=str+"--Serverstate activ--\n";
				}
				else
				{
					str=str+"--Serverstate inactiv--\n";
				}
				oos.writeObject(str);
				continue;
			
			}
		
			if ( input[0].equals("getTemp"))
 {
					str = "Temperatur : \n";
					if (log.isActive) {
						str = str + "Soll: " + log.solltemp+"\n";
						str = str + "Ist: " + log.isttemp+"\n";
					} else {
						str = "Server is not activ";
					}

					oos.writeObject(str);
					continue;

				}
			
			if ( input[0].equals("status"))
			 {
								
								if (log.isActive) {
									str = str + "Heizung: " + Status.Heizung+"\n";
									str = str + "Licht: " + Status.Licht+"\n";
									str = str + "Luft: " + Status.Luft+"\n";
									
								} else {
									str = "Server is not activ";
								}

								oos.writeObject(str);
								continue;

							}
			
			
			if ( input[0].equals("set"))
			{
				if  ( input[1].equals("debug"))
				{
					
					//env.put("mT",input[2]);
					log.debug = new Integer(input[2]).intValue();
					//log.debug = 8;
					log.writeLog(2, "Setze Debug auf " +log.debug);
					oos.writeObject("Setze Debug auf " + log.debug);
					continue;
				}
			}
			oos.writeObject("Command unknown");
			}
		} catch (IOException e) {

			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
