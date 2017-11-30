import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to get or send Dashboards from Grafana through HTTP API
 * Dashboards will be transmited in JSON format
 * @author valentinoauretino
 *
 */
public class DashboardAccess {

	private static Logger logger = LoggerFactory.getLogger(DashboardAccess.class);
	
	/**
	 * Send Dashboard as JSON
	 * @param dashbboard
	 * @return
	 * @throws IOException
	 */
	public void sendDashboard(String dashboardAsJson) throws IOException {
		logger.info("Sending dashboard");
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
		wr.writeBytes(dashboardAsJson);
		wr.flush();
		wr.close();
	}
	
	/**
	 * Get Dashboard as JSON
	 * @param dashbboard
	 * @return
	 * @throws IOException
	 */
	public String getDashboard(String dashbboard) throws IOException {
		logger.info("Receiving dashboard");
		URL url = new URL("http://localhost:3000/api/dashboards/db/"+dashbboard);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		// Add authorization with API token to request header
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				connection.setRequestProperty("Authorization",
						"Bearer eyJrIjoiWW8xU0dNNm5uUHQ5QVB5bDhJNmZWN3VFRm9DVzNOTHMiLCJuIjoidGhlc2lzIiwiaWQiOjF9");

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}
