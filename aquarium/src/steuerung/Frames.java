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

	public class Frames extends javax.servlet.http.HttpServlet {

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
				out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"");
				out.println("\"http://www.w3.org/TR/html4/frameset.dtd\">");
				out.println("<html>");
				out.println("<head>");
				out.println("<title>Aquarium</title>");
				out.println("</head>");
				out.println("<frameset cols=\"200,*\">");
				out.println("<frame src=\"navigation\" name=\"Navigation\">");
				out.println("<frame src=\"index\" name=\"Daten\">");
				out.println("<noframes>");
				out.println("<body>");
				out.println("<p><a href=\"navigation\">navigation</a> <a href=\"status\">Daten</a></p>");
				out.println("</body>");
				out.println("</noframes>");
				out.println("</frameset>");
				out.println("</html>");
	
		}
			
			catch (Throwable theException) {
				theException.printStackTrace();
			}
		}
	}
