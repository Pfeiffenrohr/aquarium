package steuerung;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class TemperaturVergleich extends javax.servlet.http.HttpServlet {
	boolean debug = true;

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
			// FileHandling fh = new FileHandling();
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			HttpSession session = request.getSession(true);
			ServletContext context = getServletContext();
			new Header().dBConnect(session,context);
			AquaDB db = (AquaDB) session.getAttribute("DBsession");

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat formatterDB = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatterAnzeige = new SimpleDateFormat(
					"dd.MM.yyyy");

			// cal.add(Calendar.DATE,-1);
			String mode = request.getParameter("mode");
			if (mode == null) {
				mode = "istSollVergleich";
			}
			System.out.println("mode = " + mode);
			if (mode.equals("allSensors")) {
				session.setAttribute("mode", "allSensors");
				String datum_start = (String) request
						.getParameter("datum_start");
				cal.setTime(formatterAnzeige.parse(datum_start));
				datum_start = formatterDB.format(cal.getTime());
				String datum_end = (String) request.getParameter("datum_end");
				cal.setTime(formatterAnzeige.parse(datum_end));
				datum_end = formatterDB.format(cal.getTime());
				Vector temp = db.getTempAllSensors(datum_start, datum_end);
				session.setAttribute("chart_temp", temp);

			} else { //Einschaltdauer
				if (mode.equals("dauer")) {
					session.setAttribute("mode", "dauer");
					String datum_start = (String) request
							.getParameter("datum_dauer_start");
					cal.setTime(formatterAnzeige.parse(datum_start));
					datum_start = formatterDB.format(cal.getTime());
					String datum_end = (String) request
							.getParameter("datum__dauer_end");
					cal.setTime(formatterAnzeige.parse(datum_end));
					datum_end = formatterDB.format(cal.getTime());
					//Vector temp = db.getTempAllSensors(datum_start, datum_end);
					
					Vector tmp= db.getOnOffHistorie(datum_start, datum_end,"Heizung");
					Vector vec=computeVector(db,tmp);
					
					session.setAttribute("chart_temp", vec);

				} else {

					// Hier beginnt der Teil von Ist-SollVergleich
					String datum = (String) request.getParameter("datum");
					if (datum == null) {
						datum = formatterDB.format(cal.getTime());
					} else {
						cal.setTime(formatterAnzeige.parse(datum));
						datum = formatterDB.format(cal.getTime());

					}
					Vector soll_temp = db.getSollTemp();
					Vector ist_temp = db.getIstTemp(datum);
					session.setAttribute("mode", "solltemperatur");
					session.setAttribute("chart_soll", soll_temp);
					session.setAttribute("chart_ist", ist_temp);
				}
			}
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\" lang=\"de\">");
		out.println("<head>");
		out.println(" <title>Temperatur Vergleich</title>");
		out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
		out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
		out.println("</head>");
		out.println("<body background=\"icons/fische.jpg\"  text=\"#0404B4\">");
		out.println("<h1>Temperaturvergleich zwischen Ist und Soll</h1>");
		out.println("<table border=\"1\" bgcolor=\"#CCEECC\">");
		out.println("<tr><td>");
		if (mode.equals("allSensors"))
		{
			out.println("<p><img src=chart?mode=allSensors width'1200' height='400'>");
		}
		else
		{
		out.println("<p><img src=chart?mode=temperaturvergleich width'1200' height='400'>");
		}
		out.println("</td><tr>");
		out.println("<tr><td>");
		//Temperaturändern Formular
		out.println("<form action=\"temperaturvergleich?mode=istSollVergleich\">");
		out.println("<input type=\"hidden\" name=\"mode\" value=\"istSollVergleich\">");
		out.println("<input type=\"submit\" value=\"Ist und Soll Temperatur vergleichen\">");

		out.println("Datum: <input id=\"dob\" name=\"datum\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		
		out.println("</form>");
		out.println("</td></tr>");
		out.println("<tr><td>");
		out.println("<form action=\"temperaturvergleich?mode=allSensors\">");
		out.println("<input type=\"hidden\" name=\"mode\" value=\"allSensors\">");
		out.println("<input type=\"submit\" value=\"Temperaturverlauf der einzelnen Sensoren\">");

		out.println("Start: <input id=\"dob1\" name=\"datum_start\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		out.println("End: <input id=\"dob2\" name=\"datum_end\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		
		out.println("</form>");
		
		out.println("<tr><td>");
		out.println("<form action=\"temperaturvergleich?mode=dauer\">");
		out.println("<input type=\"hidden\" name=\"mode\" value=\"dauer\">");
		out.println("<input type=\"submit\" value=\"Auswertung Einschaltdauer Heizung\">");

		out.println("Start: <input id=\"dob3\" name=\"datum_dauer_start\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob3', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		out.println("End: <input id=\"dob4\" name=\"datum_dauer_end\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob4', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		
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

	Vector computeVector(AquaDB db,Vector tmp)
			{
		Vector vec = new Vector();
			Calendar cal = Calendar.getInstance();
			boolean istein =false;
			SimpleDateFormat formatterDBdatum = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatterDBzeit = new SimpleDateFormat("HHmmss");
			int anfZust=new Integer((Integer)((Hashtable)tmp.elementAt(0)).get("value1"));
			if (anfZust==0)
			istein=true;
			return vec;
			}
	

}