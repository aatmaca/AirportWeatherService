package weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import weather.model.AirportData;
import weather.model.AtmosphericInformation;
import weather.service.WeatherService;

/**
 * The Weather App REST endpoint allows clients to query, update and check
 * health stats. Currently, all data is held in memory. The end point deploys to
 * a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

	public static final Logger logger = Logger.getLogger(RestWeatherQueryEndpoint.class.getName());

	/** shared weatherService */
	private static WeatherService weatherService = new WeatherService();

	/** shared gson json to object factory */
	public static final Gson gson = new Gson();

	// static {
	// init();
	// }

	/**
	 * Retrieve service health including total size of valid data points and
	 * request frequency information.
	 *
	 * @return health stats for the service as a string
	 */
	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String ping() {
		Map<String, Object> retval = new HashMap<>();

		int datasize = 0;
		for (AtmosphericInformation ai : weatherService.getAtmosphericInformations()) {
			// we only count recent readings
			if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPressure() != null || ai.getPrecipitation() != null
					|| ai.getTemperature() != null || ai.getWind() != null) {
				// updated in the last day
				if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
					datasize++;
				}
			}
		}
		retval.put("datasize", datasize);

		double totalNoOfRequests = 0;
		if (weatherService.getRequestFrequency().size() != 0) {
			for (Integer value : weatherService.getRequestFrequency().values()) {
				totalNoOfRequests += value;
			}
		}

		Map<String, Double> freq = new HashMap<>();
		// fraction of queries
		for (AirportData data : weatherService.getAirports()) {
			double frac = 0;
			if (totalNoOfRequests != 0) {
				frac = ((double) weatherService.getRequestFrequency().getOrDefault(data, 0)) / totalNoOfRequests;
			}
			freq.put(data.getIata(), frac);
		}
		retval.put("iata_freq", freq);

		int m = weatherService.getRadiusFreq().keySet().stream().max(Double::compare).orElse(1000.0).intValue() + 1;
		
		//Do not permit radius query greater than 1001
		if (m>1001) {
			m=1001;
		}

		int[] hist = new int[m];
		for (Map.Entry<Double, Integer> e : weatherService.getRadiusFreq().entrySet()) {
			int i = e.getKey().intValue();
			if (i>1000) {
				i=1000;
			}
			hist[i] += e.getValue();
		}
		retval.put("radius_freq", hist);

		return gson.toJson(retval);
	}

	/**
	 * Given a query in json format {'iata': CODE, 'radius': km} extracts the
	 * requested airport information and return a list of matching atmosphere
	 * information.
	 *
	 * @param iata
	 *            the iataCode
	 * @param radiusString
	 *            the radius in km
	 *
	 * @return a list of atmospheric information
	 */
	@GET
	@Path("/weather/{iata}/{radius}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response weather(@PathParam("iata") String iata, @PathParam("radius") String radiusString) {

		AirportData airportData = weatherService.findAirportData(iata);
		if (airportData == null) {
			logger.info("Airport not found");
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
		double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);
		weatherService.updateRequestFrequency(airportData, radius);

		List<AtmosphericInformation> retval = new ArrayList<>();
		if (radius == 0) {
			AtmosphericInformation ai = weatherService.getAtmosphericInformation(iata);
			if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null || ai.getPressure() != null
					|| ai.getTemperature() != null || ai.getWind() != null) {
				retval.add(ai);
			}
		} else {
			for (AirportData ad : weatherService.getAirports()) {
				if (weatherService.calculateDistance(airportData, ad) <= radius) {
					AtmosphericInformation ai = weatherService.getAirportAtmosphericInfoMap().get(ad);
					if (ai.getCloudCover() != null || ai.getHumidity() != null || ai.getPrecipitation() != null || ai.getPressure() != null
							|| ai.getTemperature() != null || ai.getWind() != null) {
						retval.add(ai);
					}
				}
			}
		}
		return Response.status(Response.Status.OK).entity(retval).build();
	}

	/**
	 * A dummy init method that loads hard coded data
	 */
	protected static void init() {

		weatherService.reset();

		weatherService.addAirport("BOS", 42.364347, -71.005181);
		weatherService.addAirport("EWR", 40.6925, -74.168667);
		weatherService.addAirport("JFK", 40.639751, -73.778925);
		weatherService.addAirport("LGA", 40.777245, -73.872608);
		weatherService.addAirport("MMU", 40.79935, -74.4148747);
	}
}