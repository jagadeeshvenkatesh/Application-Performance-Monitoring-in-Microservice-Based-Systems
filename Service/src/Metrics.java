import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.db.influxdb.Configuration;
import com.db.influxdb.DataWriter;
import com.db.influxdb.Utilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Metrics {

	 static DatabaseAccess dao = new DatabaseAccess();
	public static void main(String[] args) throws Exception {

		ArrayList<String> hostlist = new ArrayList<>();
//      	hostlist.add("R6x_nuhhLZq1wrr9X7aVH1N0nCs");
		hostlist.add("N0n3QwIlX358zNC_Xp8QKW0NSnY");

		ArrayList<String> containerlist2 = new ArrayList<>();
		containerlist2.add("GfVYqknxuiLBWvCgihkltUKoPgU");
		containerlist2.add("rx3xhns6KKaQax1ghrjHje9_j6k");
		containerlist2.add("7uHhWaExHla_-v2wFtzkS5CT-TA");
		
		ArrayList<String> servicelist = new ArrayList<>();
		servicelist.add("b9xeCfs_8x6YIqZ06Rv3bzxpOHA");
		servicelist.add("sHN0tA3GC3NXC926h3oNBxq8ROA");
	
	
		
		ArrayList<String> containerlist1 = new ArrayList<>();
		containerlist1.add("X3zF6QXImBlWIufTyR9MlKpqXrQ");
		containerlist1.add("nf7fy9kn632CgwBmIQY13IqGSLE");
		containerlist1.add("KXxcRow3mOUOjHbzO1l4IFI3Zq8");
		containerlist1.add("ZKdcVJ6Io9m8bykScofxAJCb9hQ");
		containerlist1.add("Q-NcFqVUxATUkWmsveCvfKaeYmQ");
		containerlist1.add("2P_tVjHNDCc9QSgpsMtLt2k1MOc");
		containerlist1.add("AKSOnsz8ZwBxVfZBAQZrVZOXGMU");
		containerlist1.add("fUnzjvWoVnrwxMhVrhDog_n1ifk");
	
		
		
		
	

//		while (true) {	
//			try {
//			getCPU_Used_Metrics(hostlist);
//			getMemory_Used_Metrics(hostlist);
//			getLoad_Metrics(hostlist);
//			getContainer_CPU_Metrics(containerlist1);
//			getContainer_Memory_Metrics(containerlist1);
//			getContainer_IO_Metrics(containerlist1);
//			getContainer_CPU_Metrics(containerlist2);
//			getContainer_Memory_Metrics(containerlist2);
//			getContainer_IO_Metrics(containerlist2);
//			getService_AvgLatency(servicelist);
//			getService_Calls(servicelist);
//			getService_error_rate(servicelist);
//			getService_Instances(servicelist);
//				getComponentInformantion("ujAP46NqxH64mzMVmlVkB0gHNd0");
//			}catch(Exception exc) {
//				
//			}	
//			parseEventInformation("[{\"eventId\":\"LcNQpHlcR2ig68n_yDAwRQ\",\"start\":1511125875000,\"end\":1511125971271,\"problem\":\"online\",\"fixSuggestion\":\"Web Service Instance\",\"severity\":-1,\"snapshotId\":\"KL6YyjA0wn_I9l3TOM8BYiuh9Bk\"},"
//					+ "{\"eventId\":\"nuH2msDtTQWgN1cYebtbvQ\",\"start\":1511125930000,\"end\":1511126031271,\"problem\":\"offline\",\"fixSuggestion\":\"Web Service Instance\",\"severity\":5,\"snapshotId\":\"JkbM79nIPQn1zWIayr-MpKL2Lcg\"}]");
			
//		}
		
			getTraceData();
	}
		
	

	/**
	 * @throws Exception
	 * 
	 */
	public static void getCPU_Used_Metrics(ArrayList<String> hostlist) throws Exception {

		for (String host : hostlist) {

			// Define Instana REST call and establish a connection
			URL url = new URL("https://audi-audi.instana.io/api/metric?metric=cpu.used&time=" + getTime()
					+ "&aggregation=mean&snapshotId=" + host + "&rollup=1000");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Add authorization with API token to request header
			connection.setRequestMethod("GET");
			connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

			// Read response
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse JSON response
			JSONParser parser = new JSONParser();
			Object object = parser.parse(response.toString());
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			// Connect to InfluxDB
			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);

			// create table and add field
			writer.setMeasurement("host_metrics_" + host);
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("cpu_used", value);

			// Write into InfluxDB
			writer.setTime(System.currentTimeMillis());
			writer.writeData();
		}
	}

	public static void getMemory_Used_Metrics(ArrayList<String> hostlist) throws Exception {

		for (String host : hostlist) {
			// Define Instana Rest URL and establish a connection
			URL url = new URL("https://audi-audi.instana.io/api/metric?metric=memory.used&time=" + getTime()
					+ "&aggregation=mean&snapshotId=" + hostlist.get(0) + "&rollup=1000");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Add authorization with API token to request header
			connection.setRequestMethod("GET");
			connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse JSON response
			JSONParser parser = new JSONParser();
			Object object = parser.parse(response.toString());
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);
			writer.setMeasurement("host_metrics_" + host);
			// Default is in seconds
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("memory_used", value);

			writer.setTime(System.currentTimeMillis());
			writer.writeData();
		}
	}

	public static void getLoad_Metrics(ArrayList<String> hostlist) throws Exception {

		for (String host : hostlist) {
			// Define Instana Rest URL and establish a connection
			URL url = new URL("https://audi-audi.instana.io/api/metric?metric=load.1min&time=" + getTime()
					+ "&aggregation=mean&snapshotId=" + hostlist.get(0) + "&rollup=1000");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Add authorization with API token to request header
			connection.setRequestMethod("GET");
			connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			// Parse JSON response
			JSONParser parser = new JSONParser();
			Object object = parser.parse(response.toString());
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);
			writer.setMeasurement("host_metrics_" + host);
			// Default is in seconds
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("load", value);

			writer.setTime(System.currentTimeMillis());
			writer.writeData();
		}

	}

	public static void getComponentInformantion(String component) throws IOException {

		// Define Instana Rest URL and establish a connection
		URL url = new URL(
				"https://audi-audi.instana.io/api/snapshots/" + component + "?time=" + getTime());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Add authorization with API token to request header
		connection.setRequestMethod("GET");
		connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());

	}

	public static void getContainer_CPU_Metrics(ArrayList<String> containerlist) throws Exception {

		for (String container : containerlist) {

			// Parse JSON response
			JSONParser parser = new JSONParser();
			Object object = parser
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=cpu.system_usage&time=" + getTime()
							+ "&aggregation=mean&snapshotId=" + container + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);
			writer.setMeasurement("container_metrics_");
			// Default is in seconds
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("container", container);
			writer.addField("cpu_used", value);

			writer.setTime(System.currentTimeMillis());
			writer.writeData();
		}

	}

	public static void getContainer_Memory_Metrics(ArrayList<String> containerlist) throws Exception {

		for (String container : containerlist) {
			// Define Instana Rest URL and establish a connection

			// Parse JSON response
			JSONParser parser = new JSONParser();
			Object object = parser.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=memory.usage&time="
					+ getTime() + "&aggregation=mean&snapshotId=" + container + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);
			writer.setMeasurement("container_metrics_");
			// Default is in seconds
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("container", container);
			writer.addField("memory_used", value);

			writer.setTime(System.currentTimeMillis());
			writer.writeData();
		}

	}

	public static void getContainer_IO_Metrics(ArrayList<String> containerlist) throws Exception {

		for (String container : containerlist) {

			// Parse JSON response (Write metric)
			JSONParser parser = new JSONParser();
			Object object = parser
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=blkio.blk_write&time=" + getTime()
							+ "&aggregation=mean&snapshotId=" + container + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			writeIntoDatabase("container_metrics_", "container", container, "write", value);

			// Parse JSON response (Read metric)
			JSONParser parser1 = new JSONParser();
			Object object1 = parser1
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=blkio.blk_read&time=" + getTime()
							+ "&aggregation=mean&snapshotId=" + container + "&rollup=1000"));
			JSONObject jsonObject1 = (JSONObject) object1;

			double value1 = (double) jsonObject1.get("value");

			writeIntoDatabase("container_metrics_", "container", container, "read", value1);

		}
	}

	public static String sendRequest(String request) throws IOException {

		// Define Instana Rest URL and establish a connection
		URL url = new URL(request);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Add authorization with API token to request header
		connection.setRequestMethod("GET");
		connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
	
	public static void getService_Calls(ArrayList<String> servicelist) throws Exception {

		for (String service : servicelist) {
			
  
			JSONParser parser = new JSONParser();
			Object object = parser
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=count&time=" + getTime()
					+ "&aggregation=sum&snapshotId=" + service + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			writeIntoDatabase("sevice_metrics", "service", service, "calls", value);
		}
	}
	
	public static void getService_AvgLatency(ArrayList<String> servicelist) throws Exception {

		for (String service : servicelist) {
			
			
			JSONParser parser = new JSONParser();
			Object object = parser
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=duration.mean&time=" + getTime()
					+ "&aggregation=stats&snapshotId=" + service + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");

			writeIntoDatabase("sevice_metrics", "service", service, "latency", value);
		}
	}
	
	public static void getService_error_rate(ArrayList<String> servicelist) throws Exception {

		for (String service : servicelist) {
			// Parse JSON response (Write metric)
			JSONParser parser = new JSONParser();
			Object object = parser
					.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=error_rate&time=" + getTime()
					+ "&aggregation=mean&snapshotId=" + service + "&rollup=1000"));
			JSONObject jsonObject = (JSONObject) object;

			double value = (double) jsonObject.get("value");
			
			System.out.println(value);

			writeIntoDatabase("sevice_metrics", "service", service, "error", value);

			
		}
	}
	
	
	public static void getService_Instances(ArrayList<String> servicelist) throws Exception {

		for (String service : servicelist) {			

						// Parse JSON response (Write metric)
						JSONParser parser = new JSONParser();
						Object object = parser
								.parse(sendRequest("https://audi-audi.instana.io/api/metric?metric=instances&time=" + getTime()
								+ "&aggregation=mean&snapshotId=" + service + "&rollup=1000"));
						JSONObject jsonObject = (JSONObject) object;

						double value = (double) jsonObject.get("value");

						writeIntoDatabase("sevice_metrics", "service", service, "instances", value);
		}
	}
	
	public static void getEvents() throws Exception {

		// Define Instana Rest URL and establish a connection
		URL url = new URL(
				"https://audi-audi.instana.io/api/events/?windowsize=1000000&to="+getTime());

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Add authorization with API token to request header
		connection.setRequestMethod("GET");
		connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());

	}
	
	public static void getTraceData() throws Exception {
		// Define Instana Rest URL and establish a connection
				URL url = new URL(
						"https://audi-audi.instana.io/api/traces/-1827782056356300223");

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				// Add authorization with API token to request header
				connection.setRequestMethod("GET");
				connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				
				JsonParser parser = new JsonParser();
				JsonObject trace = parser.parse(response.toString()).getAsJsonObject();
				String traceDuration = trace.get("duration").getAsString();
				String traceErrors = trace.get("totalErrorCount").getAsString();
				String traceName = trace.get("name").getAsString();
				JsonObject traceData = trace.get("data").getAsJsonObject();
				String traceType ="";
				 for (Map.Entry<String, JsonElement> dataEntry: traceData.entrySet()) {
				     traceType = dataEntry.getKey()+" request";
				     break;
				 }
				 
				 Map<String, String> data = new HashMap<>();
				 data.put("name", traceName);
				 data.put("type", traceType);
				 data.put("duration", traceDuration);
				 data.put("errors", traceErrors);
				 
				
				 dao.writeDataSeriesIntoInfluxDB("traces", data);
				 
				 
				System.out.println(traceType);
				System.out.println(trace.toString());
				System.out.println(traceDuration);
	}
	public static void getTraces() throws Exception {
		// Define Instana Rest URL and establish a connection
				URL url = new URL(
						"https://audi-audi.instana.io/api/traces?windowsize={1948194165805255939&to="+getTime()+"&sortBy=duration&sortMode=\"\"&query=\"\"");

				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				// Add authorization with API token to request header
				connection.setRequestMethod("GET");
				connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				System.out.println(response.toString());
	}
	
	public static void getEventInformation(String eventId) throws Exception {

		// Define Instana Rest URL and establish a connection
		URL url = new URL(
				"https://audi-audi.instana.io/api/events/"+eventId);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Add authorization with API token to request header
		connection.setRequestMethod("GET");
		connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());

	}
	
	public static void parseEventInformation(String events) throws Exception {

//		// Define Instana Rest URL and establish a connection
//		URL url = new URL("https://audi-audi.instana.io/api/events/?windowsize=1000000&to=" + getTime());
//
//		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//		// Add authorization with API token to request header
//		connection.setRequestMethod("GET");
//		connection.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");
//
//		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//		String inputLine;
//		StringBuffer response = new StringBuffer();
//		while ((inputLine = in.readLine()) != null) {
//			response.append(inputLine);
//		}
//		in.close();
		
		String input = events.toString();
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(input);
		JsonArray array = element.getAsJsonArray();
		 
		for(int i=0;i<array.size();i++) {
			
			JsonObject event = array.get(i).getAsJsonObject();
			
			String problem= event.get("problem").toString();
			String severity = event.get("severity").toString();
			String fixSuggestion = event.get("fixSuggestion").toString();
			fixSuggestion=fixSuggestion.replaceAll(" ", "_");
			
		
		
			if(severity.equals("-1")) {
				severity="Change";
			}else if (severity.equals("5")) {
				severity="Issue";
			}else {
				severity="Incident";
			}
			
			Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
			DataWriter writer = new DataWriter(configuration);
			writer.setMeasurement("events");
			
			
			writer.setTimeUnit(TimeUnit.MILLISECONDS);
			writer.addField("problem", problem);
			writer.addField("severity", severity);
			writer.addField("Fix-Suggestion", fixSuggestion);
			
			if(problem!=null) {
				writer.addField("event_occured", "1");
				System.out.println(severity);
			}
						
			writer.setTime(System.currentTimeMillis());
			writer.writeData();
			
			
		}

		
	}

	public static void writeIntoDatabase(String tablename, String componentType, String snapshotID, String metricType, double metricValue) throws Exception {

		Configuration configuration = new Configuration("localhost", "8086", "root", "root", "infrastructure");
		DataWriter writer = new DataWriter(configuration);
		writer.setMeasurement(tablename);
		
		writer.setTimeUnit(TimeUnit.MILLISECONDS);
		writer.addField(componentType, snapshotID);
		writer.addField(metricType, metricValue);

		writer.setTime(System.currentTimeMillis());
		writer.writeData();
	}
	
	
	public static String getTime() {

		return String.valueOf(System.currentTimeMillis());
		
	}
	
	
	public static void sendDashboard(String dashboard) throws IOException {
		URL obj = new URL("http://localhost:3000/api/dashboards/db");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Add authorization with API token to request header
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Authorization",
				"Bearer eyJrIjoiWW8xU0dNNm5uUHQ5QVB5bDhJNmZWN3VFRm9DVzNOTHMiLCJuIjoidGhlc2lzIiwiaWQiOjF9");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(dashboard);
		wr.flush();
		wr.close();
	}
}
