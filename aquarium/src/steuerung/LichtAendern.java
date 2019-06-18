package steuerung;


import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpSession;

public class LichtAendern extends javax.servlet.http.HttpServlet {
	boolean debug=true;

private static final long serialVersionUID = 1L;

public void doGet(javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException {

	performTask(request, response);

}

public void doPost(javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response)
		throws javax.servlet.ServletException, java.io.IOException {
	performTask(request, response);

}

public void performTask(javax.servlet.http.HttpServletRequest request,
		javax.servlet.http.HttpServletResponse response) {
	try {
		//FileHandling fh = new FileHandling();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);

		
		
		String startstunde=(String)request.getParameter("startstunde");
		System.out.println("startstunde = "+startstunde);
		
		String startminute=(String) request.getParameter("startminute");
		System.out.println("startminute = "+startminute);
		String endstunde=(String) request.getParameter("endstunde");
		System.out.println("endstunde = "+endstunde);
		String endminute=(String) request.getParameter("endminute");
		System.out.println("endminute = "+endminute);
		String temp=(String) request.getParameter("temp");
		String error =checkInput(startstunde,startminute,endstunde,endminute,temp);
		System.out.println("error = "+error);
		AquaDB db = (AquaDB) session.getAttribute("DBsession"); 
		if (error.equals("OK"))
		{
			System.out.println("insert");
			
			insertData(db,startstunde,startminute,endstunde,endminute,temp);
		}
		Vector soll_temp=db.getSollTemp();
		session.setAttribute("mode","solltemperatur");
		session.setAttribute("chart_vec", soll_temp);
		
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\" lang=\"de\">");
		out.println("<body background=\"icons/fische.jpg\"  text=\"#0404B4\">");
		out.println("<h1>Temperatureinstellung Ändern</h1>");
		out.println("<table border=\"1\" bgcolor=\"#CCEECC\">");
		out.println("<tr><td>");
		out.println("<p><img src=chart?mode=solltemperatur width'1200' height='400'>");
		out.println("</td><tr>");
		out.println("<tr><td>");
		//Temperaturändern Formular
		out.println("<form action=\"temperaturaendern\">");
		out.println("<p>");
		out.println("Startzeit:");
		out.println("<select name=\"startstunde\" size=\"1\">");
			for (int i=0;i<24;i++)
			{
				out.println("<option>"+i+"</option>");
			}
		out.println("</select>");
		out.println(":");
		out.println("<select name=\"startminute\" size=\"1\">");
		for (int i=0;i<60;i++)
		{
			out.println("<option>"+i+"</option>");
		}
	out.println("</select>");
	
	out.println("Endzeit:");
	out.println("<select name=\"endstunde\" size=\"1\">");
		for (int i=0;i<24;i++)
		{
			out.println("<option>"+i+"</option>");
		}
	out.println("</select>");
	out.println(":");
	out.println("<select name=\"endminute\" size=\"1\">");
	for (int i=0;i<60;i++)
	{
		out.println("<option>"+i+"</option>");
	}
	out.println("</select>");
	out.println("EndTemperatur:");
	out.println("<input name=\"temp\" type=\"text\" size=\"5\" maxlength=\"5\">");
	out.println("</p>");
	out.println(" <input type=\"submit\" value=\" Eintragen \">");		
			out.println("</form>");
		out.println("</td></tr>");
		out.println("</table>"); 
		out.println("</body>");
		out.println("</html>");
		out.close();
	}catch (Throwable theException) {
			theException.printStackTrace();
		}

}
String checkInput(String startstunde,String startminute, String endstunde, String endminute,String temp)
{
	if (startstunde == null)
	{
		return "Startzeit nicht korrekt";
	}
	if (startminute == null)
	{
		return "Startzeit nicht korrekt";
	}
	if (endstunde == null)
	{
		return "Endzeit nicht korrekt";
	}
	if (endminute == null)
	{
		return "Endzeit nicht korrekt";
	}
	if (temp == null)
	{
		return "Temperatur nicht korrekt";
	}
	
	return "OK";
}

String insertData(AquaDB db,String startstunde,String startminute,String endstunde,String endminute,String endTemp_str)
{
	Calendar cal= Calendar.getInstance();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	String akt_datum=formatter.format(cal.getTime());
	Calendar calStart= Calendar.getInstance();
	Calendar calEnd= Calendar.getInstance();
	Double endTemp= new Double(endTemp_str);
	//Zuerst die Anfanstemperatur holen;
	if ((new Integer(startstunde)).intValue()<10)
	{
		startstunde="0"+startstunde;
	}
	if ((new Integer(startminute)).intValue()<10)
	{
		startminute="0"+startminute;
	}
	Double startTemp = db.getTempSoll(startstunde+startminute+"00");
	if (debug)
	{
	System.out.println("Starttemperatur="+startTemp);
	}
	calStart.set(Calendar.HOUR_OF_DAY,new Integer(startstunde));
	calStart.set(Calendar.MINUTE,new Integer(startminute));
	calEnd.set(Calendar.HOUR_OF_DAY,new Integer(endstunde));
	calEnd.set(Calendar.MINUTE,new Integer(endminute));
	long diff_minuten = ((calEnd.getTimeInMillis() - calStart.getTimeInMillis())/1000)/60 ; 
	if (debug)
	{
	System.out.println("Zeitifferenz in Minuten ="+diff_minuten);
	}
	
	if (diff_minuten <= 0)
	{
		diff_minuten=diff_minuten * -1;
		//return "Endzeit muss nach der Startzeit liegen !!!";
	}
	Double diff_temp=endTemp - startTemp;
	Double delta =diff_temp/diff_minuten;
	if (debug)
	{
	System.out.println("Temperaturdifferenz pro Minute ="+delta);
	}
	for (int i=0; i<=diff_minuten;i++)
	{
		startTemp=startTemp+delta;
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.setDecimalSeparator('.');
		DecimalFormat f = new DecimalFormat("#0.0",dfs);
		System.out.println(f.format(startTemp));
		int stunde=calStart.get(Calendar.HOUR_OF_DAY);
		if (stunde<10)
		{
			startstunde="0"+stunde;
		}
		else{
			startstunde=""+stunde;
		}
		int minute=calStart.get(Calendar.MINUTE);
		if (minute<10)
		{
			startminute="0"+minute;
		}
		else
		{
			startminute=""+minute;
		}
	
		String zeit=startstunde+startminute+"00";		
		System.out.println("Zeit = "+zeit);
		db.setNewTemp_soll(f.format(startTemp), zeit);
		calStart.add(Calendar.MINUTE,1);
	}
return "OK";	
}
}