package push.example.a99101.weather;


//import lpadron.me.weatherly.R;
import push.example.a99101.R;

public class Forecast {
    private push.example.a99101.weather.Currently currently;
    private push.example.a99101.weather.Daily[] dailyWeather;
    private push.example.a99101.weather.Hourly[] hourlyWeather;

    public push.example.a99101.weather.Currently getCurrently() {
        return currently;
    }

    public void setCurrently(push.example.a99101.weather.Currently currently) {
        this.currently = currently;
    }

    public push.example.a99101.weather.Daily[] getDailyWeather() {
        return dailyWeather;
    }

    public void setDailyWeather(push.example.a99101.weather.Daily[] dailyWeather) {
        this.dailyWeather = dailyWeather;
    }

    public push.example.a99101.weather.Hourly[] getHourlyWeather() {
        return hourlyWeather;
    }

    public void setHourlyWeather(push.example.a99101.weather.Hourly[] hourlyWeather) {
        this.hourlyWeather = hourlyWeather;
    }

    public static int getIconId(String iconString) {
        int iconId = R.drawable.clear_day;

        /* Set icon to appropriate weather data */
        switch(iconString){
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "cloudy-night":
                iconId = R.drawable.clear_night;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "partly-cloudy":
                iconId = R.drawable.partly_cloudy;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sunny":
                iconId = R.drawable.sunny;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
        }
        return iconId;
    }
}
