import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

	/**
	 * @throws Exception
	 * 
	 */
	public void getCPU_Used_Metrics(ArrayList<String> hostlist) throws Exception {

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
			
			dao.writeDataPointIntoInfluxDB("host_metrics", "host",host,"cpu_used",value);
		}		
	}

	public  void getMemory_Used_Metrics(ArrayList<String> hostlist) throws Exception {

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

			dao.writeDataPointIntoInfluxDB("host_metrics", "host",host,"memory_used",value);
			
		}
	}

	public void getLoad_Metrics(ArrayList<String> hostlist) throws Exception {

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
			
			dao.writeDataPointIntoInfluxDB("host_metrics", "host",host,"load",value);
			
		}
	}

	
	public void getContainer_CPU_Metrics(ArrayList<String> containerlist) throws Exception {

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

	public void getContainer_Memory_Metrics(ArrayList<String> containerlist) throws Exception {

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

	public void getContainer_IO_Metrics(ArrayList<String> containerlist) throws Exception {

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

	public  String sendRequest(String request) throws IOException {

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
	
	public void getService_Calls(ArrayList<String> servicelist) throws Exception {

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
	
	public void getService_AvgLatency(ArrayList<String> servicelist) throws Exception {

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
	
	public void getService_error_rate(ArrayList<String> servicelist) throws Exception {

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
	
	
	public void getService_Instances(ArrayList<String> servicelist) throws Exception {

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
	
	public void getEvents() throws Exception {

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
	
	public void getTraceData() throws Exception {
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
	public void getTraces() throws Exception {
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
	
	public void getEventInformation(String eventId) throws Exception {

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
	
	public void parseEventInformation(String events) throws Exception {

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

	public void writeIntoDatabase(String tablename, String componentType, String snapshotID, String metricType, double metricValue) throws Exception {

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
}
