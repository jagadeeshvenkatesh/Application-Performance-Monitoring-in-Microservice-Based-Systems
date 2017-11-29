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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Logger logger = LoggerFactory.getLogger(ApplicationData.class);
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

		System.out.println(componentID);
		String serviceName = serviceData.get("service_name").getAsString();
		return serviceName + " " + serviceType;
	}

	public static HashMap<String, ArrayList<String>> getServices(String data) {

		HashMap<String, ArrayList<String>> serviceData = new HashMap<>();

		JsonParser parser = new JsonParser();
		JsonObject applicationView = parser.parse(data.toString()).getAsJsonObject();
		JsonArray services = applicationView.get("tree").getAsJsonArray();

		for (JsonElement serviceElement : services) {
			ArrayList<String> instances = new ArrayList<>();
			JsonObject service = serviceElement.getAsJsonObject();
			String serviceSnapshotId = service.get("snapshotId").getAsString();
			JsonArray instancesOfService = service.get("children").getAsJsonArray();
			for (int i = 0; i < instancesOfService.size(); i++) {
				JsonObject ob = instancesOfService.get(i).getAsJsonObject();
				String e = ob.get("snapshotId").getAsString();
				instances.add(e);

			}

			serviceData.put(serviceSnapshotId, instances);
		}

		return serviceData;
	}

	/**
	 * write current application data into the XML file by using the DOM API
	 * 
	 * @param XMLfilePath
	 * @throws Exception
	 */
	public static void writeApplicationDataIntoXML(String XMLfilePath, HashMap<String, ArrayList<String>> serviceData) {

		try {
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

		if (microserviceEnvironmentData.getElementsByTagName("service").getLength() != 0) {
			Node servicesNode = microserviceEnvironmentData.getElementsByTagName("services").item(0);		
			NodeList servicesList = servicesNode.getChildNodes();
			int length = servicesList.getLength();
			
			for (int i = 0; i < length; i++) {
				Node s = servicesList.item(0);
				servicesNode.removeChild(s);
			}
		}

		Node servicesNode = microserviceEnvironmentData.getElementsByTagName("services").item(0);

		for (Map.Entry<String, ArrayList<String>> entry : serviceData.entrySet()) {
			Element service = doc.createElement("service");
			Attr serviceSnapshotId = doc.createAttribute("snapshotId");
			servicesNode.appendChild(service);
			serviceSnapshotId.setValue(entry.getKey());
			service.setAttributeNode(serviceSnapshotId);

			ArrayList<String> instances = entry.getValue();

			for (String instance : instances) {
				Element instanceElement = doc.createElement("instance");
				Attr instanceSnapshotId = doc.createAttribute("snapshotId");
				service.appendChild(instanceElement);
				instanceSnapshotId.setValue(instance);
				instanceElement.setAttributeNode(instanceSnapshotId);

			}

		}

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(XMLfilePath));
		transformer.transform(source, result);
		}catch(Exception exc) {
			
		}
	}

	/**
	 * Check for Application-specific changes, such as instances or services
	 * @throws Exception
	 */
	public void checkForApplicationChanges() throws Exception {
		logger.info("Checking for application changes");
		boolean changeOccured = false;

		String xmlFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
				.toString() + "/MicorserviceEnvironmentData.xml".toString();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(new File(xmlFilePath));
		HashMap<String, ArrayList<String>> serviceData = getServices(getApplicationView());
		

		NodeList serviceNodes = document.getElementsByTagName("service");
		
		// Check if number of previous services (XML file) and current services (REST
		// call) is the same
		if (serviceNodes.getLength() == serviceData.size()) {
			
			for (int i = 0; i < serviceNodes.getLength(); i++) {
				boolean serviceChange = true;
				Element service = (Element) serviceNodes.item(i);
				String serviceSnapshotId = service.getAttribute("snapshotId");
				for (Map.Entry<String, ArrayList<String>> serviceEntry : serviceData.entrySet()) {						
					if (serviceEntry.getKey().equals(serviceSnapshotId)) {
						serviceChange = false;
						ArrayList<String> instances = serviceEntry.getValue();
						NodeList instanceNodes = service.getChildNodes();
						if (instanceNodes.getLength() == instances.size()) {
							for (int index = 0; index < instanceNodes.getLength(); index++) {
								Element instance = (Element) instanceNodes.item(index);
								if (instances.contains(instance.getAttribute("snapshotId"))) {
									serviceChange = false;
									break;
								}else{
									serviceChange = true;
								}
							}
						} else {
							serviceChange = true;
						}
						if (serviceChange) {
							break;
						}
					}
				}
				if (serviceChange) {
					changeOccured = true;
					break;
				}
			}
		} else {
			changeOccured = true;
		}

		if (changeOccured) {
			logger.info("Application change occured");
			writeApplicationDataIntoXML(xmlFilePath, serviceData);
		}	
	}	
}
