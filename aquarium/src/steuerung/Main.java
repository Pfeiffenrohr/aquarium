package steuerung;


import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

	//Aufruf http://localhost:8080/filme/MainFrame

	public class Main extends javax.servlet.http.HttpServlet {

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
				String info = request.getParameter("info");
				ServletContext context = getServletContext();
				new Header().dBConnect(session,context);
				AquaDB db = (AquaDB) session.getAttribute("DBsession");
				Hashtable status = db.getStatus();
				//HeaderFooter hf = new HeaderFooter();
				out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\" lang=\"de\">");
				out.println("<body background=\"icons/fische.jpg\"  text=\"#0404B4\">");
				out.println("<h1>Aquariumsteuerung</h1>");
				//Äussere Tabelle
				out.println("<table border=\"1\">");
				out.println("<tr><td>");
				//Steuerung Tabelle
				out.println("<table border=\"0\">");
				out.println("<tr><td>");
				out.println("<font size=\"8\" color=\"black\">");
				out.println("Steuerung");
				out.println("</font>");
				out.println("</td></tr>");
				Enumeration keys = status.keys();
				while (keys.hasMoreElements()) {
					String key = (String) keys.nextElement();
				out.println("<tr><td>");
				out.println("<p>");
				out.println("<font size=\"7\">");
				out.println(key);
				out.println("</font>");
				out.println("</td><td>");
				Integer in = (Integer)status.get(key);
				if (in.intValue() == 0 )
				{
				out.println("<img src=\"icons/off.jpg\" width='80' height='80'>");
				}
				else
				{
					out.println("<img src=\"icons/on.jpg\" width='80' height='80'>");
				}	
				out.println("</td></tr>");
				}
				out.println("</table>");
				//Steuerung Tabelle Ende
				out.println("</td><td>");
				out.println("<th align=\"center\" valign=\"top\">");
				//Tabelle Heizung
				out.println("<table border=\"0\">");
				out.println("<tr><td>");
				out.println("<font size=\"8\" color=\"black\">");
				out.println("Heizung");
				out.println("</font>");
				out.println("</td><td>");

				
				Vector temp = db.getActTemp();
				if (heizungOK(db,temp))
				{
					out.println("<img src=\"icons/ampel_gruen.jpg\" width='40' height='80'>");
					}
					else
					{
						out.println("<img src=\"icons/ampel_rot.jpg\" width='40' height='80'>");
					}	
				out.println("</td></tr>");
				out.println("<tr><td>");
				
				//Ausrechnen der Durchschnittstemperatur
				DecimalFormat f = new DecimalFormat("#0.00");
				
				double sum=0.0;
				for (int i= 0 ; i< temp.size();i++)
				{
					sum=sum + (Double)temp.elementAt(i);
				}
				sum=sum/temp.size();
				out.println("Aktuelle Temperatur");
				out.println("<font size=\"7\" color=\"red\">");
				out.println(f.format(sum));
				out.println("</font>");
				Calendar cal= Calendar.getInstance();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String akt_datum=formatter.format(cal.getTime());
				temp = db.getAvgTemp(akt_datum);
				sum=0.0;
				for (int i= 0 ; i< temp.size();i++)
				{
					sum=sum + (Double)temp.elementAt(i);
				}
				sum=sum/temp.size();
				out.println("<p>");
				out.println("Durschnittstemperatur Heute "+f.format(sum));
				out.println("<p>");
				out.println("Höchste Temperatur Heute "+tmp_max(db,akt_datum));
				out.println("<p>");
				out.println("Niedrigste Temperatur Heute "+tmp_min(db,akt_datum));
				out.println("</td> </tr>");
				out.println("<tr> <td>");
				out.println("<form action=\"temperaturaendern\">");
				out.println("<input type=\"submit\" value=\"Temperaturvorgaben ändern \">");
				out.println("</form>");
				out.println("</td> </tr>");
				out.println("<tr> <td>");
				out.println("<form action=\"temperaturvergleich\">");
				out.println("<input type=\"submit\" value=\"Ist und Soll Temperatur vergleichen\">");
				out.println("</form>");
				out.println("</td> </tr>");
				out.println("</table>"); //Ende Tabelle Heizung
				out.println("</th>");
				
				out.println("</td><td>");
				out.println("<th align=\"center\" valign=\"top\">");
				//Tabelle Stroumung
				out.println("<table border=\"0\">");
				out.println("<tr><td>");
				out.println("<font size=\"8\" color=\"black\">");
				out.println("Stroemung");
				out.println("</font>");
				out.println("</td><td>");
				out.println("</td><td>");
				if (stroemungOK(db,temp))
				{
					out.println("<img src=\"icons/ampel_gruen.jpg\" width='40' height='80'>");
					}
					else
					{
						out.println("<img src=\"icons/ampel_rot.jpg\" width='40' height='80'>");
					}	
				out.println("</td></tr>");
				//out.println("<tr><td>");
				out.println("</table>"); //Ende Tabelle Stroumung
				out.println("</th>");
				
				out.println("</td></tr>");
				out.println("</table>");
			out.println("</body>");
			out.println("</html>");
			out.close();
			//db.closeConnection();
		}
			
			catch (Throwable theException) {
				theException.printStackTrace();
			}
		}
		
		private boolean heizungOK(AquaDB db,Vector temp)
		{
			Hashtable werte=db.getGrenzwerte();
			double sum=0.0;
			for (int i= 0 ; i< temp.size();i++)
			{
				sum=sum + (Double)temp.elementAt(i);
			}
			sum=sum/temp.size();
			if (sum > (Double)werte.get("temp_max"))
			{
				System.out.println("Maxwert überschritten "+ (Double)werte.get("temp_max"));
				return false;
			}
			if (sum < (Double)werte.get("temp_min"))
			{
				System.out.println("Minwert unterschritten "+ (Double)werte.get("temp_min"));
				return false;
			}
			return true;
		}
		
		private String tmp_max(AquaDB db, String datum)
		{
			Vector temp= db.getMaxTemp(datum);
			double max=0.0;
			for (int i= 0 ; i< temp.size();i++)
			{
				if ((Double)temp.elementAt(i) >max)
				{
					max=(Double)temp.elementAt(i);
				}
			}
			return (new Double(max)).toString();
		}
		
		private String tmp_min(AquaDB db, String datum)
		{
			Vector temp= db.getMinTemp(datum);
			double min=1000.0;
			for (int i= 0 ; i< temp.size();i++)
			{
				if ((Double)temp.elementAt(i) < min)
				{
					min=(Double)temp.elementAt(i);
				}
			}
			return (new Double(min)).toString();
		}
		
		private boolean stroemungOK(AquaDB db,Vector temp)
		{
			Hashtable werte=db.getGrenzwerte();
			double delta=0.0;
			delta=(Double)temp.elementAt(0) - (Double)temp.elementAt(1);
			
			if (delta > (Double)werte.get("delta_temp"))
			{
				System.out.println("Unterschied der Temperatursensoren überschritten "+ (Double)werte.get("delta_temp"));
				return false;
			}
		
			return true;
		}
	}
		