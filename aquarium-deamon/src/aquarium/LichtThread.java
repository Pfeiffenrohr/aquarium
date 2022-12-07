package aquarium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;

import common.FileHandling;
import common.SystemExecute;;

/**
 * Diese Klasse steuert das Licht
 * 
 * @author richard
 *
 */
public class LichtThread implements Runnable {
	Logging log;
	DB db;
	Hashtable config;
	int run;

	public LichtThread(Logging log, DB db, Hashtable config) {
		this.log = log;
		this.db = db;
		this.config = config;
	}

	public LichtThread() {

	}

	public void run() {
		FileHandling fh = new FileHandling();
		SystemExecute se = new SystemExecute();
		log.writeLog(2, "LichtThread gestarted!");
		// Zustand der Heizung holen.

		log.writeLog(2, "LichtThread gestarted!");

		// for (int i = 0; i < 5; i++) {
		while (run == 1) {
			if (db == null) {
				if (db.dataBaseConnect((String) config.get("db_user"), (String) config.get("db_password"),
						(String) config.get("db_server"))) {
					log.writeLog(4, "Verbindung zur Datenbank erstellt ..");
				} else {
					log.writeLog(0, "Error!!!! Kann mich nicht zur Datenbank verbinden !!!!");
					try {
						Thread.sleep(300000);
					} catch (Exception e) {
						System.out.println(e);
						log.writeLog(0, e.toString());
					}
					continue;
				}
			}
			if (db.con != null) {
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("HHmm");
				String zeit = formatter.format(cal.getTime());
				Hashtable status_hash = (Hashtable) db.getStatus();
				Status.Licht = ((Integer) status_hash.get("Licht")).intValue();
				// SollWert auslesen
				int sollLicht = db.getLichtSoll(zeit + "00");
				// Isttemperatur auslesen
				String workdir = (String) config.get("workdir");
				log.writeLog(8, "Licht soll = "+ sollLicht + " Status.Licht = "+Status.Licht);
				try {
				
					if (Status.Licht == 0 && sollLicht == 1) {
						// licht einschalten
						if (Status.os.equals("win")) {
							se.syscall_win(workdir + "\\licht_ein.bat");
						} else {
							se.syscall_unix(workdir + "/bin/licht_ein " + workdir);
						}

						if (!se.returnstring.equals(""))
							log.writeLog(3, se.returnstring);
						if (!se.errorstring.equals(""))
							log.writeLog(0, se.errorstring);
						if (se.rc == 0) {
							db.setStatus("Licht", 1);
							Status.Licht = 1;
							log.writeLog(2, "Licht eingeschaltet");
						} else {
							log.writeLog(0, "!!!!Error Licht konnte nicht eingeschaltet werden");
						}

						// Status auf 1 setzen
					}
					if (Status.Licht == 1 && sollLicht == 0) {
						// licht einschalten
						if (Status.os.equals("win")) {
							se.syscall_win(workdir + "\\licht_aus.bat");
						} else {
							se.syscall_unix(workdir + "/bin/licht_aus " + workdir);
						}

						if (!se.returnstring.equals(""))
							log.writeLog(3, se.returnstring);
						if (!se.errorstring.equals(""))
							log.writeLog(0, se.errorstring);
						if (se.rc == 0) {
							db.setStatus("Licht", 0);
							Status.Licht = 0;
							log.writeLog(2, "Licht ausgeschaltet");
						} else {
							log.writeLog(0, "!!!!Error Licht konnte nicht ausgeschaltet werden");
						}

						// Status auf 1 setzen
					}

				} catch (Exception e) {
					System.out.println(e);
					log.writeLog(0, "!!!!Error Ein interner Fehler ist aufgetreten!!!");
				}
				// db.closeConnection();
			}
			try {
				Thread.sleep(12000);
			} catch (Exception e) {
				System.out.println(e);
				log.writeLog(0, e.toString());
			}
		}
		log.writeLog(2, "LichtThread beendet!");
	}
}
