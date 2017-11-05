package weather.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import weather.exception.WeatherException;
import weather.model.AirportData;
import weather.model.AtmosphericInformation;
import weather.model.DataPoint;
import weather.model.DataPointType;

public class WeatherService {

	/** Earth radius in KM */
	public static final double R = 6372.8;

	/** AtmosphericInformation for all known airports */
	private static Map<AirportData, AtmosphericInformation> airportAtmosphericInfoMap = new HashMap<>();

	/**
	 * Internal performance counter to better understand most requested
	 * information, this map can be improved but for now provides the basis for
	 * future performance optimizations. Due to the stateless deployment
	 * architecture we don't want to write this to disk, but will pull it off
	 * using a REST request and aggregate with other performance metrics
	 */
	private static Map<AirportData, Integer> requestFrequency = new HashMap<AirportData, Integer>();

	private static Map<Double, Integer> radiusFreq = new HashMap<Double, Integer>();

	public Map<AirportData, AtmosphericInformation> getAirportAtmosphericInfoMap() {
		return airportAtmosphericInfoMap;
	}

	public Set<AirportData> getAirports() {
		return airportAtmosphericInfoMap.keySet();
	}

	public Collection<AtmosphericInformation> getAtmosphericInformations() {
		return airportAtmosphericInfoMap.values();
	}

	public Map<AirportData, Integer> getRequestFrequency() {
		return requestFrequency;
	}

	public Map<Double, Integer> getRadiusFreq() {
		return radiusFreq;
	}

	/**
	 * Given an iataCode find the airport data
	 *
	 * @param iataCode
	 *            as a string
	 * @return airport data or null if not found
	 */
	public AirportData findAirportData(String iataCode) {
		return getAirports().stream().filter(ap -> ap.getIata().equals(iataCode)).findFirst().orElse(null);
	}

	/**
	 * Records information about how often requests are made
	 *
	 * @param iata
	 *            an iata code
	 * @param radius
	 *            query radius
	 */
	public void updateRequestFrequency(AirportData airportData, Double radius) {
		requestFrequency.put(airportData, requestFrequency.getOrDefault(airportData, 0) + 1);
		radiusFreq.put(radius, radiusFreq.getOrDefault(radius, 0) + 1);
	}

	/**
	 * Given an iataCode find the index of airport data
	 *
	 * @param iataCode
	 *            as a string
	 * @return index of airport data or -1 if not found
	 */
	public AtmosphericInformation getAtmosphericInformation(String iataCode) {
		final AirportData airportData = findAirportData(iataCode);
		if (airportData == null) {
			return null;
		}
		return airportAtmosphericInfoMap.get(airportData);
	}

	/**
	 * Haversine distance between two airports.
	 *
	 * @param ad1
	 *            airport 1
	 * @param ad2
	 *            airport 2
	 * @return the distance in KM
	 */
	public double calculateDistance(AirportData ad1, AirportData ad2) {
		double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
		double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
		double a = Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2) * Math.cos(Math.toRadians(ad1.getLatitude()))
				* Math.cos(Math.toRadians(ad2.getLatitude()));
		double c = 2 * Math.asin(Math.sqrt(a));
		return R * c;
	}

	/**
	 * Update the airports weather data with the collected data.
	 *
	 * @param iataCode
	 *            the 3 letter IATA code
	 * @param pointType
	 *            the point type {@link DataPointType}
	 * @param dp
	 *            a datapoint object holding pointType data
	 *
	 * @throws WeatherException
	 *             if the update can not be completed
	 */
	public void addDataPoint(String iataCode, String pointType, DataPoint dp) throws WeatherException {

		try {
			AtmosphericInformation ai = getAtmosphericInformation(iataCode);
			if (ai != null) {
				updateAtmosphericInformation(ai, pointType, dp);
				return;
			}
		} catch (Exception e) {
			// we will throw WeatherException below.
		}

		throw new WeatherException("update can not be completed");
	}

	/**
	 * update atmospheric information with the given data point for the given
	 * point type
	 *
	 * @param ai
	 *            the atmospheric information object to update
	 * @param pointType
	 *            the data point type as a string
	 * @param dp
	 *            the actual data point
	 */
	public void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws Exception {
		final DataPointType dptype = DataPointType.valueOf(pointType.toUpperCase());
		ai.update(dptype, dp);
	}

	/**
	 * Add a new known airport to our list.
	 *
	 * @param iataCode
	 *            3 letter code
	 * @param latitude
	 *            in degrees
	 * @param longitude
	 *            in degrees
	 *
	 * @return the added airport
	 */
	public AirportData addAirport(String iataCode, double latitude, double longitude) {

		AirportData ad = new AirportData(iataCode, latitude, longitude);

		airportAtmosphericInfoMap.put(ad, new AtmosphericInformation());

		return ad;
	}

	/**
	 * Remove airport from the list
	 *
	 * @param iata
	 *            3 letter code
	 * @param latitude
	 *            in degrees
	 * @param longitude
	 *            in degrees
	 *
	 * @return true if airport was added, false - if not (already exists)
	 */
	public boolean deleteAirportData(String iata) {
		AirportData airportData = findAirportData(iata);
		if (airportData == null) {
			return false;
		}
		airportAtmosphericInfoMap.remove(airportData);
		requestFrequency.remove(airportData);
		return true;
	}

	/**
	 * Reset method
	 */
	public void reset() {		
		airportAtmosphericInfoMap.clear();
        requestFrequency.clear();
        radiusFreq.clear();
	}
}
