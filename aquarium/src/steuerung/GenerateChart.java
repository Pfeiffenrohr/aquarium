package steuerung;


import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.category.*;
import org.jfree.chart.plot.*;


import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;
import java.util.Calendar;

import java.text.SimpleDateFormat;


public class GenerateChart extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("Hallo Welt");
		out.println("</body>");
		out.println("</html>");*/
		
		
		response.setContentType("image/png");
		HttpSession session = request.getSession(true);
		OutputStream outputStream = response.getOutputStream();
	
		String mode=request.getParameter("mode");
		//createDataset(chartVec);
		//System.err.println("Modus = "+mode);
		if (mode.equals("solltemperatur")) {
			Vector chartVec = (Vector)session.getAttribute("chart_vec");
			//System.err.println("Create Chart");
			XYDataset dataset = createDataset_soll(chartVec);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset,"Solltemperatur");
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("temperaturvergleich")) {
			Vector chartist = (Vector)session.getAttribute("chart_ist");
			Vector chartsoll = (Vector)session.getAttribute("chart_soll");
			//System.err.println("Create Chart");
			XYDataset dataset = createDataset(chartist,chartsoll);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset,"Temperaturvergleich");
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
	
		if (mode.equals("allSensors")) {
			Vector chartlist = (Vector)session.getAttribute("chart_temp");
			//System.err.println("Create Chart");
			XYDataset dataset = createDatasetAllSensors(chartlist);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset,"Temperaturvergleich");
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		if (mode.equals("wasserstand")) {
			Vector chartlist = (Vector)session.getAttribute("chart_wasserstand");
			//System.err.println("Create Chart");
			XYDataset dataset = createDatasetWasserstand(chartlist);
			//System.err.println("Create Dataset");
			//System.err.println(chartVec);
			JFreeChart chart = createChart(dataset,"Wasserstand Filterbecken");
			//System.err.println("Chart fertig");
			int width = 500;
			int height = 350;
			ChartUtilities.writeChartAsPNG(outputStream, chart, width, height);
		}
		
		
	}
	private static XYDataset createDataset_soll(Vector vec) {

        TimeSeries s1 = new TimeSeries("Solltemperatur", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
        	try{
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	s1.addOrUpdate(new Minute((Date)((Hashtable)vec.elementAt(i)).get("zeit")),(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        }
        dataset.addSeries(s1);
        return dataset;
	}
	
	private static XYDataset createDataset(Vector vec_ist,Vector vec_soll) {

        TimeSeries s1 = new TimeSeries("Solltemperatur", Day.class);
        TimeSeries s2 = new TimeSeries("Isttemperatur", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec_soll.size();i++)
        {
        	try{
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	s1.addOrUpdate(new Minute((Date)((Hashtable)vec_soll.elementAt(i)).get("zeit")),(Double) ((Hashtable)vec_soll.elementAt(i)).get("value"));
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        }
        dataset.addSeries(s1);
        for (int i=0; i<vec_ist.size();i++)
        {
        	try{
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	s2.addOrUpdate(new Minute((Date)((Hashtable)vec_ist.elementAt(i)).get("zeit")),(Double) ((Hashtable)vec_ist.elementAt(i)).get("value"));
        	
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        }
        dataset.addSeries(s2);
        return dataset;
	}
	
	
	private static XYDataset createDatasetAllSensors(Vector vec) {

        TimeSeries s1 = new TimeSeries("Linker Sensor", Day.class);
        TimeSeries s2 = new TimeSeries("Rechter Sensor", Day.class);
        TimeSeries s3 = new TimeSeries("Bodenensor", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
        	try{
        		Calendar cal = Calendar.getInstance();	
        		Calendar cal_zeit = Calendar.getInstance();
        		cal.setTime((Date)((Hashtable)vec.elementAt(i)).get("datum"));
        		cal_zeit.setTime((Date)((Hashtable)vec.elementAt(i)).get("zeit"));
        		//System.out.println(" Stunde "+cal_zeit.get(cal_zeit.HOUR_OF_DAY));
        		//System.out.println(" Minute "+cal_zeit.get(cal_zeit.MINUTE));
        		cal.set(cal.HOUR_OF_DAY,cal_zeit.get(cal_zeit.HOUR_OF_DAY));
        		cal.set(cal.MINUTE,cal_zeit.get(cal_zeit.MINUTE));
        		cal.set(cal.SECOND,cal_zeit.get(cal_zeit.SECOND));
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	s1.addOrUpdate(new Minute((Date)cal.getTime()),(Double) ((Hashtable)vec.elementAt(i)).get("value1"));
        	s2.addOrUpdate(new Minute((Date)cal.getTime()),(Double) ((Hashtable)vec.elementAt(i)).get("value2"));
        	s3.addOrUpdate(new Minute((Date)cal.getTime()),(Double) ((Hashtable)vec.elementAt(i)).get("value3"));
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        	
        	
        }
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.addSeries(s3);
        return dataset;
	}
	
	private static XYDataset createDatasetWasserstand(Vector vec) {

        TimeSeries s1 = new TimeSeries("Wasserstand", Day.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (int i=0; i<vec.size();i++)
        {
        	try{
        		Calendar cal = Calendar.getInstance();	
        		Calendar cal_zeit = Calendar.getInstance();
        		cal.setTime((Date)((Hashtable)vec.elementAt(i)).get("datum"));
        		cal_zeit.setTime((Date)((Hashtable)vec.elementAt(i)).get("zeit"));
        		//System.out.println(" Stunde "+cal_zeit.get(cal_zeit.HOUR_OF_DAY));
        		//System.out.println(" Minute "+cal_zeit.get(cal_zeit.MINUTE));
        		cal.set(cal.HOUR_OF_DAY,cal_zeit.get(cal_zeit.HOUR_OF_DAY));
        		cal.set(cal.MINUTE,cal_zeit.get(cal_zeit.MINUTE));
        		cal.set(cal.SECOND,cal_zeit.get(cal_zeit.SECOND));
        	//System.err.println("Eintrag "+(Double) ((Hashtable)vec.elementAt(i)).get("value"));
        	s1.addOrUpdate(new Minute((Date)cal.getTime()),(Double) ((Hashtable)vec.elementAt(i)).get("wert"));
        	//System.out.println("Wert = "+(Double) ((Hashtable)vec.elementAt(i)).get("wert") );
        	//System.err.println("fertig Eintrag "+i);
        	}
        	catch (Exception ex) {System.err.println("Exception "+ex);
        	}
        	
        	
        	
        }
        dataset.addSeries(s1);
        return dataset;
	}
	
	 private static JFreeChart createChart(XYDataset dataset,String titel) {
	JFreeChart chart = ChartFactory.createTimeSeriesChart(
            titel,  // title
            "Zeit",             // x-axis label
            "Temperatur",   // y-axis label
            dataset,            // data
            true,               // create legend?
            true,               // generate tooltips?
            false               // generate URLs?
        );
	  chart.setBackgroundPaint(Color.white);

      XYPlot plot = (XYPlot) chart.getPlot();
      plot.setBackgroundPaint(Color.lightGray);
      plot.setDomainGridlinePaint(Color.white);
      plot.setRangeGridlinePaint(Color.white);
      //plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
      plot.setDomainCrosshairVisible(true);
      plot.setRangeCrosshairVisible(true);
      
      XYItemRenderer r = plot.getRenderer();
      if (r instanceof XYLineAndShapeRenderer) {
          XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
          //renderer.setShapesVisible(true);
          //renderer.setShapesFilled(true);
      }
      
      DateAxis axis = (DateAxis) plot.getDomainAxis();
      //axis.setDateFormatOverride(new SimpleDateFormat("HH:MM:SS"));
      
      return chart;

  }
	 
	

}
