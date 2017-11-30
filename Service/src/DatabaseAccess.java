
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataWriter;

/**
 * Class to create database acess to write into different databases 
 * @author valentin seifermann
 *
 */
public class DatabaseAccess {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseAccess.class);
	
	// Default data of InfluxDB server
	private static String INFlUXDBADRESSE = "localhost";
	private static String INFlUXDBPORT = "8086";
	private static String INFlUXDBUSER = "root";
	private static String INFlUXDBPW = "root";
	private static String INFlUXDB = "root";

	
	/**
	 * Write single data points into InfluxDB
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
	
	/**
	 * Write container metrics into InfluxDB
	 * @param host
	 * @param containerSnapshotId
	 * @param metric
	 * @param value
	 * @throws Exception
	 */
	public void writeContainerDataSeriesIntoInfluxDB(String host, String containerSnapshotId, String metric, double value) throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "microserviceMonitoringDB");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement("container_metrics");

		writer.setTimeUnit(TimeUnit.MILLISECONDS);
		
		writer.addField("host", host);
		writer.addField("container", containerSnapshotId);
		writer.addField(metric, value);
		

		writer.setTime(System.currentTimeMillis());
		writer.writeData();

	}
	
	/**
	 * Write series of data points into InfluxDB
	 * @param tablename
	 * @param data
	 * @throws Exception
	 */
	public void writeDataSeriesIntoInfluxDB(String tablename,Map<String, String> data) throws Exception {
		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "microserviceMonitoringDB");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement(tablename);

		writer.setTimeUnit(TimeUnit.MILLISECONDS);
		
		for(Map.Entry<String, String> entry : data.entrySet()) {
			writer.addField(entry.getKey(),entry.getValue());
		}
	
		writer.setTime(System.currentTimeMillis());
		writer.writeData();

	}
}
