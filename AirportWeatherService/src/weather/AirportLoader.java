package weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A simple airport loader which reads a file from disk and sends entries to the
 * webservice
 *
 * 
 * @author code test administrator
 *
 */
public class AirportLoader {

	/** end point to supply updates */
	private WebTarget collect;

	public AirportLoader() {
		Client client = ClientBuilder.newClient();
		collect = client.target("http://localhost:9090").path("collect").path("airport");
	}

	public void upload(InputStream airportDataStream) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(airportDataStream));
			String sCurrentLine;
			while ((sCurrentLine = reader.readLine()) != null) {
				String[] line = sCurrentLine.split(",");

				String iataCode = line[4].replace("\"", "");
				String latitude = line[6];
				String longitude = line[7];
				String path = iataCode + "/" + latitude + "/" + longitude;

				Response post = collect.path(path).request().post(Entity.entity("", MediaType.TEXT_HTML_TYPE));
				switch (post.getStatus()) {
				case 200:
					break;
				default:
					System.out.println("Airport " + iataCode + " not loaded. Response status code: " + post.getStatus());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws IOException {

		URL airportDataUrl = AirportLoader.class.getClassLoader().getResource("airports.dat");

		if (airportDataUrl == null) {
			System.err.println(airportDataUrl + " is not a valid input");
			System.exit(1);
		}

		new AirportLoader().upload(airportDataUrl.openStream());
		System.exit(0);
	}
}
