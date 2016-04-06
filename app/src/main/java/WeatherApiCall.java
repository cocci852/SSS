/**
 * Created by cocci852 on 4/6/2016.
 */
public class WeatherApiCall {
    private static WeatherApiCall ourInstance = new WeatherApiCall();

    public static WeatherApiCall getInstance() {
        return ourInstance;
    }

    private WeatherApiCall() {
    }
}
