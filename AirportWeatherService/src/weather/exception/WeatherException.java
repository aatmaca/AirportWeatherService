package weather.exception;

/**
 * An internal exception marker
 * 
 * @author Abdullah Atmaca
 */
public class WeatherException extends Exception {

	private static final long serialVersionUID = -3717617059428945841L;

	public WeatherException(String message) {
		super(message);
	}

	public WeatherException(String message, Throwable cause) {
		super(message, cause);
	}
}
