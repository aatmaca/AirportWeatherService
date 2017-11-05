package weather;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

import weather.exception.WeatherException;
import weather.model.AirportData;
import weather.model.DataPoint;
import weather.service.WeatherService;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport
 * weather collection sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

	public static final Logger logger = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

	/** shared weatherService */
	private static WeatherService weatherService = new WeatherService();

	/** shared gson json to object factory */
	public static final Gson gson = new Gson();

	@GET
	@Path("/ping")
	@Override
	public Response ping() {
		return Response.status(Response.Status.OK).entity("ready").build();
	}

	@POST
	@Path("/weather/{iata}/{pointType}")
	@Override
	public Response updateWeather(@PathParam("iata") String iataCode, @PathParam("pointType") String pointType, String datapointJson) {
		try {
			weatherService.addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
			return Response.status(Response.Status.OK).build();
		} catch (WeatherException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@GET
	@Path("/airports")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAirports() {
		Set<String> retval = new HashSet<>();
		for (AirportData ad : weatherService.getAirports()) {
			retval.add(ad.getIata());
		}
		return Response.status(Response.Status.OK).entity(retval).build();
	}

	@GET
	@Path("/airport/{iata}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Response getAirport(@PathParam("iata") String iata) {
		AirportData ad = weatherService.findAirportData(iata);
		if (ad != null) {
			return Response.status(Response.Status.OK).entity(ad).build();
		} else {
			logger.log(Level.SEVERE, "Bad parameter iata = " + iata);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@POST
	@Path("/airport/{iata}/{lat}/{long}")
	@Override
	public Response addAirport(@PathParam("iata") String iata, @PathParam("lat") String latString, @PathParam("long") String longString) {

		try {
			AirportData ad = weatherService.findAirportData(iata);
			double latitude = Double.valueOf(latString);
			double longtitude = Double.valueOf(longString);
			if (iata != null && iata.length() == 3 && ad == null && latitude <= 90 && latitude >= -90 && longtitude <= 180
					&& longtitude >= -180) {
				weatherService.addAirport(iata, latitude, longtitude);
				return Response.status(Response.Status.OK).build();
			} else {
				logger.log(Level.SEVERE, "Bad parameters: iata = " + iata + ", lat = " + latString + ", long = " + longString);
				return Response.status(Response.Status.BAD_REQUEST).build();
			}

		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}

	@DELETE
	@Path("/airport/{iata}")
	@Override
	public Response deleteAirport(@PathParam("iata") String iata) {

		boolean success = weatherService.deleteAirportData(iata);

		if (success) {
			return Response.status(Response.Status.OK).build();
		} else {
			logger.log(Level.SEVERE, "Bad parameter iata = " + iata);
			return Response.status(Response.Status.BAD_REQUEST).build();
		}

	}

	@GET
	@Path("/exit")
	@Override
	public Response exit() {
		System.exit(0);
		return Response.noContent().build();
	}
}
