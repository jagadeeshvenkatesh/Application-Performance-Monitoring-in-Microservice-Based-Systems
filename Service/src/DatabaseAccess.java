import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataWriter;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

/**
 * Class to create database acess to write into different databases 
 * @author valentin seifermann
 *
 */
public class DatabaseAccess {
	private static String INFlUXDBADRESSE = "localhost";
	private static String INFlUXDBPORT = "8086";
	private static String INFlUXDBUSER = "root";
	private static String INFlUXDBPW = "root";
	private static String INFlUXDB = "root";

	
	/**
	 * Write data points into InfluxDB
	 * 
	 * @param tablename
	 * @param componentType
	 * @param snapshotID
	 * @param metricType
	 * @param metricValue
	 * @throws Exception
	 */
	public void writeDataPointIntoInfluxDB(String tablename, String componentType, String snapshotID, String metricType,
			double metricValue) throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "microserviceMonitoringDB");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement(tablename);

		writer.setTimeUnit(TimeUnit.MILLISECONDS);
		writer.addField(componentType, snapshotID);
		writer.addField(metricType, metricValue);

		writer.setTime(System.currentTimeMillis());
		writer.writeData();

	}
	
	public void writeDataSeriesIntoInfluxDB(String tablename, Map<String, String> dataMap) throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "microserviceMonitoringDB");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement(tablename);

		writer.setTimeUnit(TimeUnit.MILLISECONDS);
		
		for (Map.Entry<String, String> pair : dataMap.entrySet()) {
			writer.addField(pair.getKey(),pair.getValue());
		}
	

		writer.setTime(System.currentTimeMillis());
		writer.writeData();

	}
}
