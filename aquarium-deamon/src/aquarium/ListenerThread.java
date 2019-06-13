package aquarium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import common.FileHandling;
import common.SystemExecute;;

/**
 * Diese Klasse steuert das Licht
 * @author richard
 *
 */
public class ListenerThread  implements Runnable{	
	
	int port;
	Logging log;
	public ListenerThread()
	{
		
	}

public void run()
 {
	  try
	  {
	  final  ServerSocket server;
	  server = new ServerSocket( port );
	  log.writeLog(2, "ListenerThread gestarted!");
	  while ( true )
	    {
	      Socket client = null;
	      try
	      {
	        client = server.accept();
	     
	        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
			
	        String handshake= (String)ois.readObject();
	      
	        log.writeLog(6,"Handshake = "+handshake);
	        //System.out.println("Handshake = "+handshake);
	        if (handshake.equals("GiveStatus"))
	        {
	        	Hashtable status = new Hashtable();
	        	status.put("isServer", Status.Server);
	        	status.put("Heizung", Status.Heizung);
	        	status.put("Licht", Status.Licht);
	        	status.put("Luft", Status.Luft);
	        	status.put("WasserstandIsNew", Status.WasserstandIsNew);
	        	status.put("Wasserstand", Status.Wasserstand);
	        	oos.writeObject(status);
	        	Status.WasserstandIsNew=0;
	        	log.writeLog(6,"Hashtable geschickt");
	        	//System.out.println("Hashtable geschickt");
	        }
	        
	      
	      }
	      catch ( IOException e ) {
	        e.printStackTrace();
	      }
	      finally {
	        if ( client != null )
	          try { client.close(); } catch ( IOException e ) { e.printStackTrace(); }
	      }
	    }
	  
	  }
	  catch ( Exception e ) {
	        e.printStackTrace();
	      }
 }

}
		
