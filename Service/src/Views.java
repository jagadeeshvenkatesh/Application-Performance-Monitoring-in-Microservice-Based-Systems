import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class to get different views
 * @author valentin seifermann
 *
 */
public class Views {

	/**
	 * Get current Instana application view
	 * @throws IOException
	 */
	public static void getApplicationView() throws IOException {

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

		System.out.println(response.toString());

		// String input = response.toString();
		// JsonParser parser = new JsonParser();
		// JsonElement element = parser.parse(input);
		//
		// if (element.isJsonObject()) {
		// JsonObject infrastructureView = element.getAsJsonObject();
		// JsonArray array = infrastructureView.getAsJsonArray("tree");
		// for (int i = 0; i < array.size(); i++) {
		// JsonObject zone = array.get(i).getAsJsonObject();
		// JsonArray array1 = zone.get("children").getAsJsonArray();
		// for (int j = 0; j < array1.size(); j++) {
		// System.out.println(array1.get(j));
		// }
		//
		// }
		// }

	}
	
	/**
	 * Get current Instana infrastructure view
	 * @throws IOException
	 */
	public static void getInfrastructureView() throws IOException {

		URL obj = new URL("https://audi-audi.instana.io/api/graph/views/infrastructure");
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

		System.out.println(response.toString());

		// String input = response.toString();
		// JsonParser parser = new JsonParser();
		// JsonElement element = parser.parse(input);
		//
		// if (element.isJsonObject()) {
		// JsonObject infrastructureView = element.getAsJsonObject();
		// JsonArray array = infrastructureView.getAsJsonArray("tree");
		// for (int i = 0; i < array.size(); i++) {
		// JsonObject zone = array.get(i).getAsJsonObject();
		// JsonArray array1 = zone.get("children").getAsJsonArray();
		// for (int j = 0; j < array1.size(); j++) {
		// System.out.println(array1.get(j));
		// }
		//
		// }
		// }

	}
}
