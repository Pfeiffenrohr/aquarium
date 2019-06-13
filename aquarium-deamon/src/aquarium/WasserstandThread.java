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
	public class WasserstandThread implements Runnable
	{
		Logging log;
		DB db;
		Hashtable config;
		int run;
		
		public WasserstandThread(Logging log, DB db,Hashtable config)
		{
			this.log=log;
			this.db=db;
			this.config=config;
		}
		
		public WasserstandThread()
		{
			
		}
	  public void run()
 {
		FileHandling fh = new FileHandling();  
		SystemExecute se = new SystemExecute();
		int count=0;
		int wasserstandsumme=0;
		int [] wasserstandArray = new int [10]; 
		log.writeLog(2, "WasserstandThread gestarted!");
		 //Zustand der Heizung holen.
		while ( db.con == null)
		{
			log.writeLog(1, "Error!!! Keine Verbindung zur Datenbank.. Versuche es in 5 minuten nochmal ...!");
			try {
				Thread.sleep(300000);
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
		}
	    
		//for (int i = 0; i < 5; i++) {
		int wasserstandAlt=0;
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
			String workdir=(String)config.get("workdir");
			
			try {
				int wasserstand=0;
				for (int k=0;k<5; k++)
				{
					if (Status.os.equals("win"))
					{
						 se.syscall_win(workdir+"\\licht_ein.bat");
					}
					else
					{
						se.syscall_unix(workdir+"/bin/readWasserstand.sh "+workdir);
					}
					//log.writeLog(2,"Wasserstand = >>"+fh.readFile(workdir+"/sensoren/wasserstand.txt")+"<<");
					//log.writeLog(2,"Sensor1 = >>"+fh.readFile(workdir+"/sensoren/sensor1")+"<<");
					String tmpstr=fh.readFile(workdir+"/sensoren/wasserstand.txt");
					
					if (tmpstr.length()>3)
					{
					//Bei den blöden linux wird immer ein Enter angehängt. Das schneiden wir hiermit weg
						 wasserstand= new Integer (tmpstr.substring(0,tmpstr.length()-1)).intValue();
					}
					else
						wasserstand= new Integer(tmpstr).intValue();
					
					if (wasserstand  -wasserstandAlt > -10 && wasserstand  -wasserstandAlt < 10  )
					{
						k=4;
						log.writeLog(4,"Wasserstandmessung richtig: ");
						int delta= wasserstand  -wasserstandAlt;
						log.writeLog(4,"Delta =" +delta);
					}
					else
					{
					log.writeLog(4,"Wasserstandmessung nicht richtig: ");
					int delta= wasserstand  -wasserstandAlt;
					log.writeLog(4,"Delta =" +delta);
					}
				}
				wasserstandAlt=wasserstand;
					if (! se.returnstring.equals(""))
			    		log.writeLog(3,se.returnstring);
			    	if (! se.errorstring.equals(""))
			    		log.writeLog(0,se.errorstring);
			    	log.writeLog(4,"Wasserstand = "+ wasserstand);
			    	wasserstandArray[count]=wasserstand;
			    	wasserstandsumme+=wasserstand;	
			    if ( count==9 )
			    {
			    	//den höchsten Eintrag wegschmeissen
			    	int max=0;
			    	int wasserstandMax=0;
			    	for (int i=0; i<= 9; i++)
			    	{
			    		if (wasserstandArray[i] > wasserstandMax)
			    		{
			    			wasserstandMax=wasserstandArray[i];
			    			max=i;
			    		}
			    	}
			    	
			    	//Alle zusammenzählen, ausser den höchsten
			    	wasserstandsumme=0;
			    	for (int i=0; i<= 9; i++)
			    	{
			    		if (i != max)
			    		{
			    			wasserstandsumme+=wasserstandArray[i];
			    		}
			    	}
			    	//insert in Datenbank
			    	wasserstand=wasserstandsumme/9;
			    	db.insertWasserstand(wasserstand);
			    	Status.Wasserstand=wasserstand;
			    	Status.WasserstandIsNew=1;
			    	count=0;
			    	wasserstandsumme=0;
			    }
			    else
			    {
			    
			    	count++;
			    }
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0,
						"!!!!Error Ein interner Fehler ist aufgetreten!!!");
			}
			}
			try {
				//Thread.sleep(120000);
				Thread.sleep(300000);
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
			db.closeConnection();
		}
		log.writeLog(2, "WasserstandThread beendet");
	}
}
