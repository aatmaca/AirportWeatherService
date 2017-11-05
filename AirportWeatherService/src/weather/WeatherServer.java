package weather;

import static java.lang.String.format;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * This main method will be use by the automated functional grader. You
 * shouldn't move this class or remove the main method. You may change the
 * implementation, but we encourage caution.
 *
 * @author code test administrator
 */
public class WeatherServer {

	private static final String BASE_URL = "http://localhost:9090/";

	public static void main(String[] args) {
		try {
			System.out.println("Starting Weather App local testing server: " + BASE_URL);

			final ResourceConfig resourceConfig = new ResourceConfig();
			resourceConfig.register(RestWeatherCollectorEndpoint.class);
			resourceConfig.register(RestWeatherQueryEndpoint.class);

			HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URL), resourceConfig, false);
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				server.shutdownNow();
			}));

			HttpServerProbe probe = new HttpServerProbe.Adapter() {
				public void onRequestReceiveEvent(HttpServerFilter filter, Connection connection, Request request) {
					System.out.println(request.getRequestURI());
				}
			};
			server.getServerConfiguration().getMonitoringConfig().getWebServerConfig().addProbes(probe);

			// the autograder waits for this output before running automated
			// tests, please don't remove it
			server.start();
			System.out.println(format("Weather Server started.\n url=%s\n", BASE_URL));

			// blocks until the process is terminated
			Thread.currentThread().join();
			server.shutdown();
		} catch (IOException | InterruptedException ex) {
			Logger.getLogger(WeatherServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
