package aquarium;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
public class DB {
		boolean debug=false;
		protected Connection con = null;
		
		public boolean dataBaseConnect(String username,String password, String connectString) {
			if (debug) if (debug) System.out.println("Verbinde mich zur Datenbank");
			try {
				try {
					Class.forName("org.postgresql.Driver").newInstance(); // DB-
																			// Treiber
																			// laden
				} catch (Exception E) {
					System.err
							.println("Konnte Postgres Datenbank-Treiber nicht laden!");
					return false;
				}
				//String url = "jdbc:mysql://192.168.2.8/aquarium";
				con = DriverManager.getConnection(connectString, username, password); // Verbindung
			      													// herstellen
				//con = DriverManager.getConnection(url, "aquarium", "aquarium"); 
				if (debug) System.out.println("Verbindung erstellt");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Treiber fuer mySQL nicht gefunden");
				return false;
			}
			return true;
		}
		
		public boolean closeConnection() {
			if (con != null) {
				try {
					con.close();
					if (debug) System.out.println("Verbindung beendet");
				} catch (Exception e) {
					System.err.println("Konnte Verbindung nicht beenden!!");
					return false;
				}
			}
			return true;
		}
		public Hashtable getStatus () {
			Hashtable hash = new Hashtable();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str="select a.item, a.zustand_soll  from status As a";
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					hash.put((String) res.getString("a.item"), new Integer(res.getInt("a.zustand_soll")));		
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return hash;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return hash;
		}
		
		public boolean setStatus (String item, int wert)
		{
			try { 
				String str= "update status set zustand_soll = '"+wert +"' where item = '"+item+"'";
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
		
		/**
		 * Holt die aktuelle Temperatur
		 * @return
		 */
		public Vector getActTemp () {
			Vector vec = new Vector();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str=" select t.temp1, t.temp2 from temperatur As t order by datum desc, Zeit desc limit 1";
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					vec.add((Double) res.getDouble("t.temp1"));
					vec.add((Double) res.getDouble("t.temp2"));
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return vec;
		}
		
		public Vector getAvgTemp (String datum) {
			Vector vec = new Vector();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str="select avg(temp1),avg(temp2) from temperatur where datum="+datum;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					vec.add((Double) res.getDouble("avg(temp1)"));
					vec.add((Double) res.getDouble("avg(temp2)"));
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return vec;
		}
		
		public Vector getMaxTemp (String datum) {
			Vector vec = new Vector();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str="select max(temp1),max(temp2) from temperatur where datum="+datum;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					vec.add((Double) res.getDouble("max(temp1)"));
					vec.add((Double) res.getDouble("max(temp2)"));
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return vec;
		}

		public Vector getMinTemp (String datum) {
			Vector vec = new Vector();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str="select min(temp1),min(temp2) from temperatur where datum="+datum;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					vec.add((Double) res.getDouble("min(temp1)"));
					vec.add((Double) res.getDouble("min(temp2)"));
					
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return vec;
		}
		public Hashtable getGrenzwerte () {
			Hashtable hash = new Hashtable();
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str=" select name,value from grenzwerte";
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				while (res.next()) {
					
					
					hash.put((String) res.getString("name"), new Double(res.getDouble("value")));	
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return hash;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return hash;
		}
		
		public double getTempSoll (String zeit) {
			Double value=0.0;
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str=" select value from temperatur_soll where zeit="+zeit;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				
				while (res.next()) {
					
					
					value = new Double(res.getDouble("value"));	
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return value;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return value;
		}
		
		public int getLichtSoll (String zeit) {
			Integer value=0;
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str=" select value from licht_soll where zeit="+zeit;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				
				while (res.next()) {
					
					
					value = new Integer(res.getInt("value"));	
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return value;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return value;
		}
		
		public int getLuftSoll (String zeit) {
			Integer value=0;
			try {
				PreparedStatement stmt;
				ResultSet res = null;
				String stm_str=" select value from luft_soll where zeit="+zeit;
				if (debug) System.out.println(stm_str);
				stmt = con.prepareStatement(stm_str);
				res = stmt.executeQuery();
				
				while (res.next()) {
					
					
					value = new Integer(res.getInt("value"));	
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return value;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			return value;
		}
		
		
		public boolean setNewTemp_soll (String temp, String zeit)
		{
			try { 
				String str= "update temperatur_soll set " +"value = '"+temp +"' where zeit = "+zeit;
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
		public Vector getSollTemp() {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				String str_stm="select zeit,value from temperatur_soll order by zeit";
				if (debug) System.out.println(str_stm);
				stmt = con
						.prepareStatement(str_stm);
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("zeit", (Date)(res.getTime("zeit")));
					hash.put("value", new Double(res.getDouble("value")));
					
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}
		public Vector getIstTemp(String datum) {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				String str_stm="select zeit,temp1 from temperatur where datum="+datum+" order by zeit";
				if (debug) System.out.println(str_stm);
				stmt = con
						.prepareStatement(str_stm);
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("zeit", (Date)(res.getTime("zeit")));
					hash.put("value", new Double(res.getDouble("temp1")));
					
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}
		public Vector getTempAllSensors(String datum_start,String datum_end) {
			Vector vec = new Vector();
			try {

				PreparedStatement stmt;
				ResultSet res = null;
				String str_stm="select datum,zeit,temp1,temp2,temp3 from temperatur where datum>=" + datum_start+" and datum <="+ datum_end + " order by zeit";
				if (debug) System.out.println(str_stm);
				stmt = con
						.prepareStatement(str_stm);
				res = stmt.executeQuery();
				while (res.next()) {
					Hashtable hash = new Hashtable();
					hash.put("datum", (Date)(res.getDate("datum")));
					hash.put("zeit", (Date)(res.getTime("zeit")));
					hash.put("value1", new Double(res.getDouble("temp1")));
					hash.put("value2", new Double(res.getDouble("temp2")));
					hash.put("value3", new Double(res.getDouble("temp3")));
					vec.addElement(hash);
				}
			} catch (SQLException e) {
				System.err.println("Konnte Select-Anweisung nicht ausführen" + e);
				return vec;
			}
			if (debug) System.out.println("Select-Anweisung ausgeführt");
			// return summe/(float)getAnz(tag,monat,year);
			return vec;
		}
		
		
		public boolean insertWasserstand (int wasserstand)
		{
			try { 
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat formater = new SimpleDateFormat("HHmmss");
				String zeit = formater.format(cal.getTime());
				 formater = new SimpleDateFormat("yyyyMMdd");
				String datum =  formater.format(cal.getTime());
				String str= "insert into wasserstand values (null,"+wasserstand+","+datum+","+zeit+")";
			if (debug) System.out.println(str);
			PreparedStatement stmt;
			stmt = con.prepareStatement(str);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Konnte Update-Anweisung nicht ausführen" + e);
		    return false;
		}
		return true;
	}
	}
