package aquarium;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import common.FileHandling;

public class Logging {
	
	double solltemp;
	double isttemp;
	boolean isActive;
	
	int debug;
	String logfile;
	FileHandling fh = new FileHandling();
	
	public void writeLog(int level, String str)
	{
		//System.out.println("Start Logging "+logfile);
		
		Calendar cal= Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String akt_time=formatter.format(cal.getTime());
		if (level <= debug)
		{
		fh.writeFile(logfile, akt_time +" " + str, true);
		}
	}
	

}
