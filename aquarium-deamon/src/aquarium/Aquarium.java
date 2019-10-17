package aquarium;

import common.FileHandling;
import common.SystemExecute;


import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.net.*;
import java.io.*;

public class Aquarium {

	String debug="3";
	String logfile;
	int isMaster=0;
	/**
	 * @param args
	 */
	

	
	private void init (Hashtable config)
	{
		FileHandling fh = new FileHandling();
		SystemExecute se = new SystemExecute();
		TempThread tempT = new TempThread();
		LichtThread lichtT = new LichtThread();
		LuefterThread luftT = new LuefterThread();
		WasserstandThread standT = new WasserstandThread();
		ListenerThread listener = new ListenerThread();
		CommunicationThread comm = new CommunicationThread();
		Thread t1=null;
		Thread t2=null;
		Thread t3=null;
		Thread t4=null;
		Thread t5=null;
		Socket server = null; 
		boolean init=true;
		
		//System.out.println(config.get("logfile"));
		Logging log= new Logging();
		logfile=(String)config.get("logfile");
		log.logfile=logfile;
		log.writeLog(1,"Starte Server ...");
		Date start = new Date();

		config.put("startzeit", start);
		//Set Debuglevel
		debug = (String) config.get("debug");
		System.out.println("debug = " + config.get("debug"));
		log.debug = new Integer(debug).intValue();
		log.isActive = false;
		//log.debug = 8;
		log.writeLog(2, "Setze Debug auf " + debug);
		Status.Server = 0;
		String forceMaster = (String) config.get("forceMaster");
		// Verbindung zur Datenbank
		DB db = new DB();
		if (db.dataBaseConnect((String) config.get("db_user"),
				(String) config.get("db_password"),
				(String) config.get("db_server"))) {
			log.writeLog(2, "Verbindung zur Datenbank erstellt ..");
		} else {
			log.writeLog(0,
					"Error!!!! Kann mich nicht zur Datenbank verbinden !!!!");
			
		}

		// Initialisiere Alle Ports
		String workdir = (String) config.get("workdir");
		log.writeLog(3, "Setze workingdir auf " + workdir);
		if (!fh.directory_exists(workdir, false)) {
			System.err.println("Workingdir nicht gefunden");
			log.writeLog(0, "Error!!! Workingdir nicht gefunden!!!!");
			System.exit(2);
		}

		// log.writeLog(2,"TempThread gestarted!");
		// Starte Communicationthread
		Thread tComm = new Thread(comm);
		comm.env = config;
		comm.log = log;
		log.writeLog(2, "Starte Communicationthread ..");
		tComm.start();
		String host = (String) config.get("backup_IP");
		int port = new Integer((String) config.get("backup_port"));
		// Starte Listenerthrad
		Thread tListener = new Thread(listener);
		listener.port = port;
		listener.log = log;
		tListener.start();
		boolean first = true;
		DB testDB = new DB();
		while (true) {
			int becomeMaster = 0;
			if (forceMaster.contentEquals("true"))
			{
				becomeMaster = 1;
			}
			try {
				// System.out.println("www1");
				// Verbindungsversuch zur Datanbank
				if (db.con == null)	
				{
				if (testDB.dataBaseConnect((String) config.get("db_user"),
						(String) config.get("db_password"),
						(String) config.get("db_server"))) {
					//testDB.closeConnection();
				} else {
					if (db.con != null) {
						// System.out.println("Versuche Verbindung zu beenden");
						db.closeConnection();
						db.con = null;
					}
					log.writeLog(2, "Error Keine Verbindung zur Datenbank!!!");
				}
				}
				if (db.con == null)
					if (db.dataBaseConnect((String) config.get("db_user"),
							(String) config.get("db_password"),
							(String) config.get("db_server"))) {
						log.writeLog(2, "Verbindung zur Datenbank erstellt ..");
					} else {
						log.writeLog(0,
								"Error!!!! Kann mich nicht zur Datenbank verbinden !!!!");
						try {
							Thread.sleep(300000);
							continue;
						} catch (Exception e) {
							System.out.println(e);
							log.writeLog(0, e.toString());
						}
					}
				 //System.out.println("nach con");
				// log.writeLog(2,"Nach con");
				server = new Socket(host, port);
				ObjectOutputStream oos = new ObjectOutputStream(
						server.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(
						server.getInputStream());
				oos.writeObject("GiveStatus");
				 //System.out.println("Nach GiveStatus");
				// 2 Bedeutet, Gibt es einen Server irgendwo?
				Hashtable result = (Hashtable) ois.readObject();
				// 0 bedeutet, es gibt einen Server
				// 1 bedeutet, es gibt einen Backup Server
				oos.close();
				ois.close();
				int isServer = (Integer) result.get("isServer");
				if (isServer == 0) {
					becomeMaster = 1;

				} else {
					Status.Heizung = ((Integer) result.get("Heizung"))
							.intValue();
					db.setStatus("Heizung", Status.Heizung);
					Status.Licht = ((Integer) result.get("Licht")).intValue();
					db.setStatus("Licht", Status.Licht);
					Status.Luft = ((Integer) result.get("Luft")).intValue();
					db.setStatus("Luft", Status.Luft);
					log.writeLog(5, "WasserstandIsNew:" + ((Integer) result.get("WasserstandIsNew")).intValue());
					if (((Integer) result.get("WasserstandIsNew")).intValue()==1)
					{
						log.writeLog(4, "Wasserstand durch Listenerthread eingetragen");
						db.insertWasserstand(((Integer) result.get("Wasserstand")).intValue());
					}    
					if (first) {
						log.writeLog(1, "Gehe in Standby");
						first = false;
					}

				}
			}

			catch (Exception e) {

				becomeMaster = 1;
				log.writeLog(3, "!!!Warnung!! Backupserver nicht erreichbar!!");
				if (init) {
					if (Status.os.equals("win")) {
						se.syscall_win(workdir + "/setAllPorts.bat");
					} else {
						se.syscall_unix(workdir + "/bin/setAllPorts.sh");
					}

					// System.out.println("ret = "+se.returnstring+"<<<");
					if (!se.returnstring.equals(""))
						log.writeLog(3, se.returnstring);
					if (!se.errorstring.equals(""))
						log.writeLog(0, se.errorstring);
					if (se.rc == 0) {
						log.writeLog(1, "Alle Ports auf enabled geschaltet");
						Status.Heizung=1;
						Status.Licht=1;
						Status.Luft=1;
					} else {
						log.writeLog(0,
								"Error!!! Ports konnten nicht enabled werden!!!!");
					}

				}
			}
			
			init = false;
			log.writeLog(5, "init=false");
			//System.out.println("Init = false");
			if (isMaster == 0 && becomeMaster == 1) {
				isMaster = 1;
				log.isActive = true;
				Status.Server = 1;
				log.writeLog(0, "Server ist nun primaerer Server");
				try {
					// Konfig Heizungsthread
					t1 = new Thread(tempT);
					tempT.log = log;
					tempT.db = db;
					tempT.config = config;
					tempT.run = 1;

					// Konfig Lichtthread
					t2 = new Thread(lichtT);
					lichtT.log = log;
					lichtT.db = db;
					lichtT.config = config;
					lichtT.run = 1;

					// Konfig Luefterthread
					t3 = new Thread(luftT);
					luftT.log = log;
					luftT.db = db;
					luftT.config = config;
					luftT.run = 1;
					
					// Konfig Wasserstandthread
					t4 = new Thread(standT);
					standT.log = log;
					standT.db = db;
					standT.config = config;
					standT.run = 1;

					t1.start();
					t2.start();
					t3.start();
					t4.start();
					System.out.println("Threads gestartet..");
					//becomeMaster=0;
					
				} catch (Exception e) {
					isMaster = 0;
					Status.Server = 0;
					log.writeLog(3,
							"!!!Warnung!! Konnte Threads nicht starten " + e);
				}
			}

			if (isMaster == 1 && becomeMaster == 0) {
				isMaster = 0;
				log.isActive = false;
				Status.Server = 0;
				log.writeLog(0,
						"Server ist nun Backup Server und geht in Standby");
				tempT.run = 0;
				lichtT.run = 0;
				luftT.run = 0;
				// t1.interrupt();
				// t2.interrupt();
				// t3.interrupt();
				System.out.println("Threads beendet");
			}

			if (isMaster == 1 && becomeMaster == 1) {

				if (!t1.isAlive()) {
					t1 = new Thread(tempT);
					tempT.log = log;
					tempT.db = db;
					tempT.config = config;
					tempT.run = 1;
					t1.start();
					if (t1.isAlive())
					{
						log.writeLog(1,"Start erfolgreich ...");
					}
					else
					{
						log.writeLog(1,"!!Konnte Thread nicht starten...");
					}	
					log.writeLog(1,
							"!!!Warnung!! Tempthread war abgestürzt. Neu gestartet ... ");
				}

				if (!t2.isAlive()) {
					t2 = new Thread(lichtT);
					lichtT.log = log;
					lichtT.db = db;
					lichtT.config = config;
					lichtT.run = 1;
					t2.start();
					if (t2.isAlive())
					{
						log.writeLog(1,"Start erfolgreich ...");
					}
					else
					{
						log.writeLog(1,"!!Konnte Thread nicht starten...");
					}	
					log.writeLog(1,
							"!!!Warnung!! Lichtthread war abgestürzt. Neu gestartet ... ");
				}

				if (!t3.isAlive()) {
					t3 = new Thread(luftT);
					luftT.log=log;
					luftT.db=db;
					luftT.config=config;
					luftT.run=1;
					t3.start();
					if (t3.isAlive())
					{
						log.writeLog(1,"Start erfolgreich ...");
					}
					else
					{
						log.writeLog(1,"!!Konnte Thread nicht starten...");
					}	
					log.writeLog(1,"!!!Warnung!! Luefterthread war abgestürzt. Neu gestartet ... ");
				}
				
				if (!t4.isAlive()) {
					t4 = new Thread(standT);
					standT.log=log;
					standT.db=db;
					standT.config=config;
					standT.run=1;
					t4.start();
					if (t4.isAlive())
					{
						log.writeLog(1,"Start erfolgreich ...");
					}
					else
					{
						log.writeLog(1,"!!Konnte Thread nicht starten...");
					}	
					log.writeLog(1,"!!!Warnung!! Wasserstandthread war abgestürzt. Neu gestartet ... ");
				}
				 //t1.interrupt();
				 //t2.interrupt();
				 //t3.interrupt();
				
				 
			 }
			
			try {
				Thread.sleep(60000);
			} catch ( InterruptedException e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileHandling fh = new FileHandling();
		if ( args.length < 1)
		{
			System.err.println("Configfile is missing. Ussage aquarium <configfile>");
			System.exit(1);
		}
		String configfile = args [0];
		System.out.println("Read configfile "+configfile);
		Hashtable config = fh.readFileAsHashtable(configfile);		
		Aquarium aqua= new Aquarium();
		aqua.init(config);

	}

}
