package aquarium;

import java.text.SimpleDateFormat; 
import java.util.Calendar;
import java.util.Hashtable;

import common.FileHandling;
import common.SystemExecute;;

/**
 * Diese Klasse steuert die Ist und Solltemperatur
 * @author richard
 *
 */
	public class TempThread implements Runnable
	{
		Logging log;
		DB db;
		Hashtable config;
		int run;
		
		public TempThread(Logging log, DB db,Hashtable config)
		{
			this.log=log;
			this.db=db;
			this.config=config;
		}
		public TempThread()
		{
		}
	  public void run()
 {
		FileHandling fh = new FileHandling();  
		SystemExecute se = new SystemExecute();
		log.writeLog(2, "TempThread gestarted!");
		 //Zustand der Heizung holen.
		
	    Hashtable status_hash = (Hashtable)db.getStatus();
		Status.Heizung = ((Integer) status_hash.get("Heizung")).intValue();
		//for (int i = 0; i < 5; i++) {
		while (run==1)
		{
			
			// Das muß eine Endlosschleife werden
			if (db.dataBaseConnect((String) config.get("db_user"),
					(String) config.get("db_password"),
					(String) config.get("db_server"))) {
				log.writeLog(4, "Verbindung zur Datenbank erstellt ..");
			} else {
				log.writeLog(0,
						"Error!!!! Kann mich nicht zur Datenbank verbinden !!!!");
				try {
					Thread.sleep(300000);
				} catch (Exception e) {
					System.out.println(e);
					log.writeLog(0, e.toString());
				}
				continue;
			}
			if ( db.con != null)
			{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
		
			String zeit = formatter.format(cal.getTime());
			//Solltemperaturn auslesen
		    double sollTemp=db.getTempSoll(zeit+"00");
		    log.solltemp=sollTemp;
		  
		    //Isttemperatur auslesen
		    String workdir=(String)config.get("workdir");
		    try {
		    	if (Status.os.equals("win"))
				{
		    		se.syscall_win(workdir+"\\heizung_ein.bat");
				}
				else
				{
					se.syscall_unix(workdir+"/bin/auslesen.sh "+workdir);
				}
		    	
	    		
	    		if (! se.returnstring.equals(""))
	    		log.writeLog(3,se.returnstring);
	    		if (! se.errorstring.equals(""))
	    		log.writeLog(0,se.errorstring);
		    double istTemp= new Double (fh.readFile(workdir+"/sensoren/sensor1")).doubleValue();
		    log.isttemp= istTemp;
		    log.writeLog(4,"Solltemp = "+sollTemp);
		    log.writeLog(4,"Isttemp = "+istTemp);
			//System.out.println("Solltemp = "+sollTemp);
			//System.out.println("Isttemp = "+istTemp);
		    
		    
		 
		    
		    
		    if (sollTemp >= istTemp )
		    {
		    	if (Status.Heizung == 0)
		    	{
		    		//heizung einschalten
		    		if (Status.os.equals("win"))
					{
		    			se.syscall_win(workdir+"\\heizung_ein.bat");
					}
					else
					{
						se.syscall_unix(workdir+"/bin/heizung_ein "+workdir);
					}
		    		
		    		
		    		if (! se.returnstring.equals(""))
			    		log.writeLog(3,se.returnstring);
			    		if (! se.errorstring.equals(""))
			    		log.writeLog(0,se.errorstring);
		    		if (se.rc==0)
		    		{
		    			db.setStatus("Heizung", 1);
		    			Status.Heizung=1;
			    		log.writeLog(2,"Heizung eingeschaltet");
		    		}
		    		else
		    		{
		    			log.writeLog(0,"!!!!Error Heizung konnte nicht eingeschaltet werden");
		    		}
		    		
		    		//Status auf 1 setzen
		    	}
		    }
		    else
		    {
		    	if (Status.Heizung == 1)
		    	{
		    		//heizung ausschalten
		    		if (Status.os.equals("win"))
					{
		    			se.syscall_win(workdir+"\\heizung_aus.bat");
					}
					else
					{
						se.syscall_unix(workdir+"/bin/heizung_aus "+workdir);
					}
		    		
		    		
		    		
		    		if (! se.returnstring.equals(""))
			    		log.writeLog(3,se.returnstring);
			    		if (! se.errorstring.equals(""))
			    		log.writeLog(3,se.errorstring);
		    		if (se.rc==0)
		    		{
		    			db.setStatus("Heizung", 0);
		    			Status.Heizung=0;
			    		log.writeLog(2,"Heizung ausgeschaltet");
		    		}
		    		else
		    		{
		    			log.writeLog(0,"!!!!Error Heizung konnte nicht ausgeschaltet werden");
		    		}
		    		
		    	}
		    }
		    
		    	
		    } catch (Exception e) {
				System.out.println(e);
				log.writeLog(0,"!!!Error Daten in Sensor1 ist kein Doublewert!!!!!");
			}
		    db.closeConnection();
			}
			try {
				Thread.sleep(300000);
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
		    
		}
		 log.writeLog(2, "TempThread beendet! "+ run);
	}
}
