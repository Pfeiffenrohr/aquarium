package aquarium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import common.FileHandling;
import common.SystemExecute;;

/**
 * Diese Klasse steuert das Licht
 * @author richard
 *
 */
	public class LuefterThread implements Runnable
	{
		Logging log;
		DB db;
		Hashtable config;
		int run;
		
		public LuefterThread(Logging log, DB db,Hashtable config)
		{
			this.log=log;
			this.db=db;
			this.config=config;
		}
		
		public LuefterThread()
		{
			
		}
	  public void run()
 {
		FileHandling fh = new FileHandling();  
		SystemExecute se = new SystemExecute();
		log.writeLog(2, "LuefterThread gestarted!");
		 //Zustand der Heizung holen.
		
	   
		
		//for (int i = 0; i < 5; i++) {
		while (run==1)
		{
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
			 Hashtable status_hash = (Hashtable)db.getStatus();
				Status.Luft = ((Integer) status_hash.get("Luft")).intValue();
			//SollWert auslesen
		    int sollLicht=db.getLuftSoll(zeit+"00");
			// Isttemperatur auslesen
			String workdir = (String) config.get("workdir");
			try {
				if (Status.Luft == 0 && sollLicht == 1) {
					// licht einschalten
					if (Status.os.equals("win"))
					{
						 se.syscall_win(workdir+"\\licht_ein.bat");
					}
					else
					{
						se.syscall_unix(workdir + "/bin/luefter_ein "+workdir);
					}
					
					
					if (! se.returnstring.equals(""))
			    		log.writeLog(3,se.returnstring);
			    	if (! se.errorstring.equals(""))
			    		log.writeLog(0,se.errorstring);
					if (se.rc == 0) {
						db.setStatus("Luft", 1);
						Status.Luft=1;
						log.writeLog(2, "Luefter eingeschaltet");
					} else {
						log.writeLog(0,
								"!!!!Error Luefter konnte nicht eingeschaltet werden");
					}

					// Status auf 1 setzen
				}
				if (Status.Luft == 1 && sollLicht == 0) {
					// licht einschalten
					if (Status.os.equals("win"))
					{
						 se.syscall_win(workdir+"\\licht_aus.bat");
					}
					else
					{
						se.syscall_unix(workdir + "/bin/luefter_aus "+workdir);
					}
					
					
					if (! se.returnstring.equals(""))
			    		log.writeLog(3,se.returnstring);
			    	if (! se.errorstring.equals(""))
			    		log.writeLog(0,se.errorstring);
					if (se.rc == 0) {
						db.setStatus("Luft", 0);
						Status.Luft=0;
						log.writeLog(2, "Luefter ausgeschaltet");
					} else {
						log.writeLog(0,
								"!!!!Error Luefter konnte nicht ausgeschaltet werden");
					}

					// Status auf 1 setzen
				}
				

			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0,
						"!!!!Error Ein interner Fehler iste aufgetreten!!!");
			}
			db.closeConnection();
			}
			try {
				Thread.sleep(120000);
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
		}
		log.writeLog(2, "LuefterThread beendet");
	}
}
