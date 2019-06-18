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

public class Wasserstand extends javax.servlet.http.HttpServlet {
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
			Calendar cal_end = Calendar.getInstance();
			cal.add(cal_end.DATE, -12);
			SimpleDateFormat formatterDB = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatterAnzeige = new SimpleDateFormat(
					"dd.MM.yyyy");

			// cal.add(Calendar.DATE,-1);
			String mode = request.getParameter("mode");
			if (mode == null) {
				mode = "null";
			}
			//System.out.println("mode = " + mode);
			if (mode.equals("wasserstand")) {
				session.setAttribute("mode", "wasserstand");
				String datum_start = (String) request
						.getParameter("datum_start");
				cal.setTime(formatterAnzeige.parse(datum_start));
				
				datum_start = formatterDB.format(cal.getTime());
				//System.out.println("Datum_start = "+datum_start);
				String datum_end = (String) request.getParameter("datum_end");
				cal_end.setTime(formatterAnzeige.parse(datum_end));
				datum_end = formatterDB.format(cal_end.getTime());
				Vector temp = db.getWasserstand(datum_start, datum_end);
				session.setAttribute("chart_wasserstand", temp);
			}
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\" lang=\"de\">");
		out.println("<head>");
		out.println(" <title>Wasserstand</title>");
		out.println("<script src=\"datechooser/date-functions.js\" type=\"text/javascript\"></script>");
		out.println("<script src=\"datechooser/datechooser.js\" type=\"text/javascript\"></script>");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"datechooser/datechooser.css\">");
		out.println("</head>");
		out.println("<body background=\"icons/fische.jpg\"  text=\"#0404B4\">");
		out.println("<h1>Temperaturvergleich zwischen Ist und Soll</h1>");
		out.println("<table border=\"1\" bgcolor=\"#CCEECC\">");
		out.println("<tr><td>");
		if (mode.equals("wasserstand"))
		{
			out.println("<p><img src=chart?mode=wasserstand width'1200' height='400'>");
		}
		out.println("</td><tr>");
		out.println("<tr><td>");
		//Temperaturändern Formular
	
		out.println("<form action=\"wasserstand?mode=wasserstand\">");
		out.println("<input type=\"hidden\" name=\"mode\" value=\"wasserstand\">");
		out.println("<input type=\"submit\" value=\"Wasserstand Filterbecken\">");

		out.println("Start: <input id=\"dob1\" name=\"datum_start\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob1', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
		out.println("<div id=\"chooserSpan\" class=\"dateChooser select-free\" style=\"display: none; visibility: hidden; width: 160px;\">");
		out.println("</div>");
		out.println("End: <input id=\"dob2\" name=\"datum_end\" size=\"10\" maxlength=\"10\" type=\"text\" value=\""+formatterAnzeige.format(cal_end.getTime())+"\" /><img src=\"datechooser/calendar.gif\" onclick=\"showChooser(this, 'dob2', 'chooserSpan', 2005, 2050, 'd.m.Y', false);\"/>");
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
}