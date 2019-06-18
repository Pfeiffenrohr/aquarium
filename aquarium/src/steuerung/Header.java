package steuerung;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class Header {
	
	public static void dBConnect(HttpSession session,ServletContext context) {
		AquaDB db = new AquaDB();
		String DBusername = context.getInitParameter("DBusername");
		String DBuserpassword = context.getInitParameter("DBuserpassword");
		String DBconnectString = context.getInitParameter("DBconnectstring");
		if (session.getAttribute("DBsession") ==null)
		{
		//String url = "jdbc:mysql://192.168.2.8/aquarium";
		//db.dataBaseConnect("aquarium", "aquarium", url);
			if (!db.dataBaseConnect(DBusername,DBuserpassword,DBconnectString)) {
				System.err.println("Error, can not connest to database");
				//out.println("can not connect to database");
				return;

			}
		//Datenbankverbindung in der Session speichern
		session.setAttribute("DBsession",db);
		}
	}

}
