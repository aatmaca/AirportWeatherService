package weather.model;

import java.util.TimeZone;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

	/** Main city served by airport. May be spelled differently from name. */
	private String city;

	/** Country or territory where airport is located. */
	private String country;

	/** the three letter IATA code */
	private String iata;

	/** 4-letter ICAO code (blank or "" if not assigned */
	private String icao;

	/** latitude value in degrees */
	private double latitude;

	/** longitude value in degrees */
	private double longitude;

	/** In feet */
	private double altitude;

	/**
	 * Hours offset from UTC. Fractional hours are expressed as decimals. (e.g.
	 * India is 5.5)
	 */
	private TimeZone timeZone;

	/**
	 * One of E (Europe), A (US/Canada), S (South America), O (Australia), Z
	 * (New Zealand), N (None) or U (Unknown)
	 */
	private DaylightSavings dst;

	public AirportData() {
	}

	public AirportData(String city, String country, String iata, String icao, double latitude, double longitude, double altitude,
			TimeZone timeZone, DaylightSavings dst) {
		super();
		this.city = city;
		this.country = country;
		this.iata = iata;
		this.icao = icao;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.timeZone = timeZone;
		this.dst = dst;
	}

	public AirportData(String iataCode, double latitude, double longitude) {
		this.iata = iataCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIata() {
		return iata;
	}

	public void setIata(String iata) {
		this.iata = iata;
	}

	public String getIcao() {
		return icao;
	}

	public void setIcao(String icao) {
		this.icao = icao;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public DaylightSavings getDst() {
		return dst;
	}

	public void setDst(DaylightSavings dst) {
		this.dst = dst;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
	}

	public boolean equals(Object other) {
		if (other instanceof AirportData) {
			return ((AirportData) other).getIata().equals(this.getIata());
		}

		return false;
	}
}
