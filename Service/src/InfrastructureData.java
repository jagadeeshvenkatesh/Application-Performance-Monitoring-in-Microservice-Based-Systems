import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.language.bm.Rule.RPattern;
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
 * Class to get and process infrastructure-specific data
 * 
 * @author valentin seifermann
 *
 */
public class InfrastructureData {

	private static Logger logger = LoggerFactory.getLogger(InfrastructureData.class);
	public static String currentTime;

	/**
	 * Get current Instana infrastructure view and parse it
	 * 
	 * @throws IOException
	 */
	public static String getInfrastructureView() throws IOException {

		URL obj = new URL("https://audi-audi.instana.io/api/graph/views/infrastructure");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Add authorization with API token to request header
		con.setRequestMethod("GET");
		con.setRequestProperty("authorization", "apiToken hlhYqfPTeLFXpEo0");

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();


		return response.toString();
	}

	public static String getComponentInformation(String componentID) throws IOException {

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

//		JsonParser parser = new JsonParser();
//		JsonObject componentData = parser.parse(response.toString()).getAsJsonObject();
//		
//		String componentType="";
//		String componentLabel="";
//		if(!componentData.get("plugin").getAsString().equals("host")) {
//			componentType = componentData.get("plugin").getAsString();
//			componentLabel = componentData.get("label").getAsString();
//			return componentType+", label: "+componentLabel;
//		}else {
//			componentLabel = componentData.get("label").getAsString();
//			return componentLabel;
//		}
		
		return response.toString();
		
	}

	public static HashMap<String, HashMap<String, String>> getHostAndProcessesData(String data) {

		HashMap<String, HashMap<String, String>> infrastructureData = new HashMap<>();

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(data);
		JsonObject infrastructureView = element.getAsJsonObject();
		JsonArray array = infrastructureView.getAsJsonArray("tree");
		for (int i = 0; i < array.size(); i++) {
			JsonObject zone = array.get(i).getAsJsonObject();
			JsonArray hostsOfZone = zone.get("children").getAsJsonArray();
			for (int j = 0; j < hostsOfZone.size(); j++) {

				JsonObject host = hostsOfZone.get(j).getAsJsonObject();

				if (host.get("type").getAsString().equals("host")
						&& !host.get("snapshotId").getAsString().contains("unmon-host")) {
					String hostSnapshotId = host.get("snapshotId").getAsString();

					HashMap<String, String> dataOfProcesses = new HashMap<>();
				
					JsonArray processes = host.get("children").getAsJsonArray();

					for (int y = 0; y < processes.size(); y++) {
						JsonObject process = processes.get(y).getAsJsonObject();
						if (process.get("type").getAsString().equals("process")) {
							String processSnapshotID = process.get("snapshotId").getAsString();
							try {
								String compInfo = getComponentInformation(processSnapshotID);
								dataOfProcesses.put(processSnapshotID, compInfo);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

					infrastructureData.put(hostSnapshotId, dataOfProcesses);
				}
			}

		}

		return infrastructureData;

	}
	
	public static HashMap<String, ArrayList<String>> getHostConnections(String data) {

		HashMap<String, ArrayList<String>> hostConnections = new HashMap<>();
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(data);

		if (element.isJsonObject()) {
			JsonObject infrastructureView = element.getAsJsonObject();
			JsonArray zones = infrastructureView.getAsJsonArray("tree");
			for (int i = 0; i < zones.size(); i++) {
				JsonObject zone = zones.get(i).getAsJsonObject();
				JsonArray hostsOfZone = zone.get("children").getAsJsonArray();
				for (int j = 0; j < hostsOfZone.size(); j++) {
					JsonObject host = hostsOfZone.get(j).getAsJsonObject();
					ArrayList<String> processes = new ArrayList<>();
					
					if (host.get("type").getAsString().equals("host")
							&& !host.get("snapshotId").getAsString().contains("unmon-host")) {				
						String hostSnapshotId = host.get("snapshotId").getAsString();
						String hostInfo = "";
						try {

							hostInfo = getComponentInformation(hostSnapshotId);

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						JsonArray outgoingConncetions = host.get("outgoingConnection").getAsJsonArray();

						for (int k = 0; k < outgoingConncetions.size(); k++) {
							JsonObject connectedHost = outgoingConncetions.get(k).getAsJsonObject();
							String connectedHostName = connectedHost.get("connectedSnapshotId").getAsString()
									.replace("unmon-host=","");
							processes.add(connectedHostName);
						}
						hostConnections.put(host.get("snapshotId").getAsString()+" "+hostInfo, processes);
					}
					
					
				}
			}
		}
		
		return hostConnections;
	}
	
	/**
	 * Check for infrastructure changes by comparing  previous snapshotIds(XML file) with current snapshotIds(REST API response) of each component
	 * @throws Exception
	 */
	public void checkForInfrastructureChanges() throws Exception {
		logger.info("Checking for infrastructure changes");
		boolean changeOccured = false;

		String xmlFilePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
				.toString() + "/MicorserviceEnvironmentData.xml".toString();
		File xmlFile = new File(xmlFilePath);

		if (!xmlFile.exists()) {
			writeInfrastructureDataIntoXML(xmlFilePath);
		} else {

			HashMap<String, ArrayList<String>> hostConnections = getHostConnections(getInfrastructureView());
			HashMap<String, HashMap<String, String>> hostsAndProcesses = getHostAndProcessesData(
					getInfrastructureView());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(xmlFile);

			NodeList hostNodes = document.getElementsByTagName("host");

			// iterate trough listed host connections (value=snapshotID+label of host | key=
			// list of ip connections)
			for (Map.Entry<String, ArrayList<String>> hostConnectionsEntry : hostConnections.entrySet()) {
				boolean hostChange = false;
				for (int i = 0; i < hostNodes.getLength(); i++) {

					String hostSnapshotId = hostConnectionsEntry.getKey().split(" ")[0];
					Element hostElement = (Element) hostNodes.item(i);

					// Check if host snapshotID changed or number of hosts changed
					if (hostElement.getAttribute("snapshotId").equals(hostSnapshotId)
							&& hostNodes.getLength() == hostConnections.keySet().size()) {
						hostChange = false;
						boolean outgoingConnectionsChange = false;
						boolean processChange = false;

						NodeList previousOutgoingConnections = hostElement.getElementsByTagName("outgoingConnection");

						ArrayList<String> currentOutgoingConnections = hostConnectionsEntry.getValue();

						for (int index = 0; index < previousOutgoingConnections.getLength(); index++) {

							if (currentOutgoingConnections
									.contains(previousOutgoingConnections.item(index).getTextContent())
									&& currentOutgoingConnections.size() == previousOutgoingConnections.getLength()) {
								outgoingConnectionsChange = false;
								continue;

							} else {
								outgoingConnectionsChange = true;
							}
							// If during last iteration outgoingConnection IP not found or number of
							// outgoingConnections is still
							// not the same
							if (outgoingConnectionsChange == true
									&& index == previousOutgoingConnections.getLength() - 1) {
								changeOccured = true;
								break;

							}
						}

						NodeList previousProcesses = hostElement.getElementsByTagName("process");
						HashMap<String, String> currentProcesses = hostsAndProcesses.get(hostSnapshotId);

						for (Map.Entry<String, String> currentProcessesEntry : currentProcesses.entrySet()) {
							for (int index1 = 0; index1 < previousProcesses.getLength(); index1++) {
								Element processElement = (Element) previousProcesses.item(index1);

								if (processElement.getAttribute("snapshotId").equals(currentProcessesEntry.getKey())) {
									processChange = false;
									break;

								} else {
									processChange = true;
								}
								if (processChange == true && index1 == previousProcesses.getLength() - 1) {
									System.out.println("t");
									changeOccured = true;
									break;
								}
							}
						}

					} else {
						hostChange = true;
						break;
					}

					// If during last iteration host snapshotId not found or number of host is still
					// not the same

					if (hostChange == true && i == hostNodes.getLength() - 1) {
						changeOccured = true;
					}
				}
			}
		}
		//check if a change occured and rewrite XML
		if (changeOccured) {
			logger.info("Infrastructure change occured");
			Files.delete(xmlFile.toPath());
			writeInfrastructureDataIntoXML(xmlFilePath);
		}
	}
	


	/**
	 * write current infrastructure data into the XML file by using the DOM API
	 * @param XMLfilePath
	 * @throws Exception
	 */
	public static void writeInfrastructureDataIntoXML(String XMLfilePath) throws Exception {
		
		
		// Infrastructure data
		HashMap<String, ArrayList<String>> hostConnections = getHostConnections(getInfrastructureView());
		HashMap<String, HashMap<String, String>> hostsAndProcesses = getHostAndProcessesData(getInfrastructureView());
		
		
		

		// Create DOM
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.newDocument();
		Element rootElement = document.createElement("microserviceEnvironmentData");
		document.appendChild(rootElement);

		// Hosts element
		Element infrastructure = document.createElement("infrastructureData");
		rootElement.appendChild(infrastructure);
		Element hosts = document.createElement("hosts");
		infrastructure.appendChild(hosts);

		// Host elements with name, outgoingConnections, processes
		for (Map.Entry<String, ArrayList<String>> HostConnectionsEntry : hostConnections.entrySet()) {
			Element host = document.createElement("host");
			Attr hostSnapshotId = document.createAttribute("snapshotId");
			hosts.appendChild(host);
			hostSnapshotId.setValue(HostConnectionsEntry.getKey().split(" ")[0]);
			host.setAttributeNode(hostSnapshotId);
			Element hostName = document.createElement("name");
			hostName.appendChild(document.createTextNode(HostConnectionsEntry.getKey().split(" ")[1]));
			host.appendChild(hostName);

			ArrayList<String> liste = HostConnectionsEntry.getValue();

			//Outgoing Connections
			Element outCons = document.createElement("outgoingConnections");
			host.appendChild(outCons);
			for (String outgoingConnection : liste) {
				Element outCon = document.createElement("outgoingConnection");
				outCon.appendChild(document.createTextNode(outgoingConnection));
				outCons.appendChild(outCon);
			}
			Element processes = document.createElement("processes");
			host.appendChild(processes);

			//Processes
			for (Map.Entry<String, HashMap<String, String>> hostsAndProcessesEntry : hostsAndProcesses.entrySet()) {

				if (hostsAndProcessesEntry.getKey().equals(HostConnectionsEntry.getKey().split(" ")[0])) {
					HashMap<String, String> processesData = hostsAndProcessesEntry.getValue();

					for (Map.Entry<String, String> processesEntry : processesData.entrySet()) {
						Element process = document.createElement("process");
						Attr processSnapshotId = document.createAttribute("snapshotId");
						processSnapshotId.setValue(processesEntry.getKey());
						process.setAttributeNode(processSnapshotId);
						process.appendChild(document.createTextNode(processesEntry.getValue()));
						processes.appendChild(process);
					}
				}
			}
		}

		// Write into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(XMLfilePath));
		transformer.transform(source, result);

	}
}
