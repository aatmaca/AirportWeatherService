package weather.model;

/**
 * The various types of daylight saving time.
 *
 * @author Abdullah Atmaca
 */
public enum DaylightSavings {
	E("Europe"), A("US/Canada"), S("South America"), O("Australia"), Z("New Zealand"), N("None"), U("Unknown");

	String info;

	private DaylightSavings(String info) {
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	@Override
	public String toString() {
		return this.info;
	}
}