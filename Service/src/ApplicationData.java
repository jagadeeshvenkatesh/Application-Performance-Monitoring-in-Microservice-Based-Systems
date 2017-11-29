import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class to get and process application-specific data
 * 
 * @author valentin seifermann
 *
 */
public class ApplicationData {

	public static String currentTime;

	/**
	 * Get current Instana application view
	 * 
	 * @throws IOException
	 */
	public static String getApplicationView() throws IOException {

		URL obj = new URL("https://audi-audi.instana.io/api/graph/views/application");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Add authorization with API token to request header
		con.setRequestMethod("GET");
		con.setRequestProperty("authorization", "apiToken XGWkxMrdgr00Tc2s");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		return response.toString();
	}

	public static String getServiceInformation(String componentID) throws IOException {

		// Define Instana Rest URL and establish a connection
		URL url = new URL("https://audi-audi.instana.io/api/snapshots/" + componentID + "?time=" + currentTime);

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
		JsonObject service = parser.parse(response.toString()).getAsJsonObject();

		String serviceType = service.get("plugin").getAsString();
		JsonObject serviceData = service.getAsJsonObject("data");

		String serviceName = serviceData.get("service_name").getAsString();
		return serviceName + " " + serviceType;
	}

	public static HashMap<String, ArrayList<String>> getServices(String data) {
		
		HashMap<String, ArrayList<String>> serviceData = new HashMap<>();
		
		JsonParser parser = new JsonParser();
		JsonObject applicationView = parser.parse(data.toString()).getAsJsonObject();
		JsonArray services = applicationView.get("tree").getAsJsonArray();
		
		for(JsonElement serviceElement : services) {
			ArrayList<String> instances = new ArrayList<>();
			JsonObject service = serviceElement.getAsJsonObject();
			String serviceSnapshotId = service.get("snapshotId").getAsString();
			JsonArray instancesOfService = service.get("children").getAsJsonArray();
//			
//			for(JsonElement instanceElement : instancesOfService) {
//				JsonObject instance = instanceElement.getAsJsonObject();
//				instancesOfService.add(instance.get("snapshotId"));
//			}
			
			serviceData.put(serviceSnapshotId, instances);
		}
		
		return serviceData;
	}
		
		
	/**
	 * write current application data into the XML file by using the DOM API
	 * @param XMLfilePath
	 * @throws Exception
	 */
	public static void writeApplicationDataIntoXML(String XMLfilePath) throws Exception {
		
		HashMap<String, ArrayList<String>> serviceData = getServices(getApplicationView());

		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(XMLfilePath);

		Element microserviceEnvironmentData = (Element) doc.getFirstChild();

		// If XML entry applicationData does not exist --> create it
		if (microserviceEnvironmentData.getElementsByTagName("applicationData").getLength() == 0) {
			Element applicationData = doc.createElement("applicationData");
			microserviceEnvironmentData.appendChild(applicationData);
			Element services = doc.createElement("services");
			applicationData.appendChild(services);
		}
		
		if(microserviceEnvironmentData.getElementsByTagName("host").getLength() != 0) {
			System.out.println("blub");
		}
		
		
		
//		for(Map.Entry<String, ArrayList<String>> serviceEntry : serviceData.entrySet()) {
//			
//		}
//
//		// write the content into xml file
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		DOMSource source = new DOMSource(doc);
//		StreamResult result = new StreamResult(new File(XMLfilePath));
//		transformer.transform(source, result);

	}

	public void checkForApplicationChanges() throws Exception {

		boolean changeOccured = false;

		String xmlFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
				.toString() + "/MicorserviceEnvironmentData.xml".toString();
		
		writeApplicationDataIntoXML(xmlFilePath);

	}
}
