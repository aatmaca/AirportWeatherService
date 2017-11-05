package weather.model;

/**
 * encapsulates sensor information for a particular location
 */
public class AtmosphericInformation {

	/** temperature in degrees celsius */
	private DataPoint temperature;

	/** wind speed in km/h */
	private DataPoint wind;

	/** humidity in percent */
	private DataPoint humidity;

	/** precipitation in cm */
	private DataPoint precipitation;

	/** pressure in mmHg */
	private DataPoint pressure;

	/** cloud cover percent from 0 - 100 (integer) */
	private DataPoint cloudCover;

	/** the last time this data was updated, in milliseconds since UTC epoch */
	private long lastUpdateTime;

	public AtmosphericInformation() {

	}

	protected AtmosphericInformation(DataPoint temperature, DataPoint wind, DataPoint humidity, DataPoint percipitation, DataPoint pressure,
			DataPoint cloudCover) {
		this.temperature = temperature;
		this.wind = wind;
		this.humidity = humidity;
		this.precipitation = percipitation;
		this.pressure = pressure;
		this.cloudCover = cloudCover;
		this.lastUpdateTime = System.currentTimeMillis();
	}

	public DataPoint getTemperature() {
		return temperature;
	}

	public void setTemperature(DataPoint temperature) {
		this.temperature = temperature;
	}

	public DataPoint getWind() {
		return wind;
	}

	public void setWind(DataPoint wind) {
		this.wind = wind;
	}

	public DataPoint getHumidity() {
		return humidity;
	}

	public void setHumidity(DataPoint humidity) {
		this.humidity = humidity;
	}

	public DataPoint getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(DataPoint precipitation) {
		this.precipitation = precipitation;
	}

	public DataPoint getPressure() {
		return pressure;
	}

	public void setPressure(DataPoint pressure) {
		this.pressure = pressure;
	}

	public DataPoint getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(DataPoint cloudCover) {
		this.cloudCover = cloudCover;
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public boolean checkValidity(DataPointType dptype, DataPoint dp) {

		boolean validity = true;

		switch (dptype) {
		case WIND:
			validity = dp.getMean() >= 0;
			break;
		case TEMPERATURE:
			validity = dp.getMean() >= -50 && dp.getMean() < 100;
			break;
		case HUMIDITY:
			validity = dp.getMean() >= 0 && dp.getMean() < 100;
			break;
		case PRESSURE:
			validity = dp.getMean() >= 650 && dp.getMean() < 800;
			break;
		case CLOUDCOVER:
			validity = dp.getMean() >= 0 && dp.getMean() < 100;
			break;
		case PRECIPITATION:
			validity = dp.getMean() >= 0 && dp.getMean() < 100;
			break;
		default:
			validity = false;
			break;
		}

		return validity;
	}

	public void update(DataPointType dptype, DataPoint dp) {

		if (checkValidity(dptype, dp)) {

			switch (dptype) {
			case WIND:
				setWind(dp);
				break;
			case TEMPERATURE:
				setTemperature(dp);
				break;
			case HUMIDITY:
				setHumidity(dp);
				break;
			case PRESSURE:
				setPressure(dp);
				break;
			case CLOUDCOVER:
				setCloudCover(dp);
				break;
			case PRECIPITATION:
				setPrecipitation(dp);
				break;
			default:
				throw new IllegalStateException("couldn't update atmospheric data");
			}
			setLastUpdateTime(System.currentTimeMillis());
		} else {
			throw new IllegalStateException("couldn't update atmospheric data");
		}
	}
}