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

	public class Navigation extends javax.servlet.http.HttpServlet {

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
				out.println("<title>Navigation</title>");
				out.println("</head>");
				out.println("<body bgcolor=\"#E0C0FF\" text=\"#000000\" link=\"#804080\" vlink=\"#603060\" alink=\"#804080\">");

				out.println("<h1>Navigation</h1>");
				out.println("<p>");
				out.println("<a href=\"status\" target=\"Daten\"><b>Status</b></a><br>");
				out.println("<a href=\"temperaturvergleich\" target=\"Daten\"><b>Temperatur</b></a><br>");
				out.println("<a href=\"licht\" target=\"Daten\"><b>Licht</b></a><br>");
				out.println("<a href=\"wasserstand\" target=\"Daten\"><b>Wasserstand</b></a><br>");
				out.println("</p>");
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

