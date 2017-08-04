package push.example.a99101.goodweather;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by 99101 on 2017-08-02.
 */

public class FirstActivity extends Fragment {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String DAILY_FORECAST = "DAILY_FORECAST";
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    private lpadron.me.weatherly.weather.Forecast forecast;
    private GoogleApiClient mGoogleApiClient;
    private Location location;
    private LocationRequest locationRequest;
    private Context context = this;
    private double latitude;
    private double longitude;

    /* Butter knife references */
    @Bind(R.id.currentlyTempLabel)
    TextView tempLabel;
    @Bind(R.id.timeLabel) TextView timeLabel;
    @Bind(R.id.weatherIcon)
    ImageView iconView;
    @Bind(R.id.dailyLocationLabel) TextView locationLabel;
    @Bind(R.id.greetingLabel) TextView greetingLabel;
    @Bind(R.id.humidityValue) TextView humidityLabel;
    @Bind(R.id.percipValue) TextView percipLabel;
    @Bind(R.id.refreshImageView) ImageView refreshImageView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    View v;
    private int contentView;

    @Nullable
    @Override
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        ButterKnife.bind(this);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
                .setSmallestDisplacement(2000) //minimum distance of 5,000 meters before checking
                .setInterval(1 * 10000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.first_activity, container, false);
        return rootView;
    }


    public void setContentView(int contentView) {
       this.contentView = contentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    /* Location related methods */
    @Override
    public void onConnected(Bundle bundle) {
        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LocationServices
                .FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("SUSPENDING", "CONNECTION SUSPENDED");
    }

    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        getForecast();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationLabel.setText("Unable to get location.");
        greetingLabel.setText("Network error");
        reportNetworkError();
    }

    protected void stopLocationUpdates() {
        Log.i("STOPPING" , "STOPPING LOCATION UPDATES");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /* Application methods */

    private void getForecast() {
        String apiKey = "a45f738558f376111212234d47a716f6";
        String finalUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + ","
                + longitude;

        if (isNetwork()){
            toggleProgressBar();
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(finalUrl).build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleProgressBar();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        /* If we can connect and retrieve */
                        if (response.isSuccessful()) {
                            forecast = parseForecastInfo(jsonData);
                            /* When user click the refresh button
                             * recheck the forcast.io data for new one
                             * Also runs on start up */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateData();
                                }
                            });

                        } else {
                            reportHttpError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT:  ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "EXCEPTION CAUGHT:  ", e);
                    }
                }
            });
        }else {
            reportNetworkError();
        }
    }

    private void toggleProgressBar() {
        if (progressBar.getVisibility() == View.INVISIBLE){
            refreshImageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            progressBar.setVisibility(View.INVISIBLE);
            refreshImageView.setVisibility(View.VISIBLE);
        }
    }
    private lpadron.me.weatherly.weather.Forecast parseForecastInfo(String json) throws JSONException {
        lpadron.me.weatherly.weather.Forecast forecast = new lpadron.me.weatherly.weather.Forecast();

        forecast.setCurrently(getCurrentlyWeather(json));
        forecast.setDailyWeather(getDailyWeather(json));
        forecast.setHourlyWeather(getHourlyWeather(json));

        return forecast;
    }


    private lpadron.me.weatherly.weather.Daily[] getDailyWeather(String json) throws JSONException {
        JSONObject baseData = new JSONObject(json);
        String timeZone = baseData.getString("timezone");
        //Get the hourly JSON object
        JSONObject daily = baseData.getJSONObject("daily");
        //Get the data array from the JSON hourly object
        JSONArray dailyData = daily.getJSONArray("data");

        lpadron.me.weatherly.weather.Daily[] dailyWeather = new lpadron.me.weatherly.weather.Daily[dailyData.length()];

        for (int i = 0; i < dailyData.length(); i++) {
           /* Get a single json object from the json array
            * and get all the required information, save it into an
             * hour object, then save that object into the list*/
            JSONObject jsonObj = dailyData.getJSONObject(i);
            lpadron.me.weatherly.weather.Daily day = new lpadron.me.weatherly.weather.Daily();

            day.setIcon(jsonObj.getString("icon"));
            day.setSummary(jsonObj.getString("summary"));
            day.setTime(jsonObj.getLong("time"));
            day.setTempHigh(jsonObj.getDouble("temperatureMax"));
            day.setTempLow(jsonObj.getDouble("temperatureMin"));
            day.setTimeZone(timeZone);
            day.setLatitude(latitude);
            day.setLongitude(longitude);

            dailyWeather[i] = day;
        }
        return dailyWeather;
    }

    private lpadron.me.weatherly.weather.Hourly[] getHourlyWeather(String json) throws JSONException {
        JSONObject baseData = new JSONObject(json);
        //Get timezon from JSON
        String timeZone = baseData.getString("timezone");
        //Get the hourly JSON object
        JSONObject hourly = baseData.getJSONObject("hourly");
        //Get the data array from the JSON hourly object
        JSONArray hourlyData = hourly.getJSONArray("data");

        lpadron.me.weatherly.weather.Hourly[] hourlyWeather = new lpadron.me.weatherly.weather.Hourly[25];

        for (int i = 0; i < 25; i++) {
           /* Get a single json object from the json array
            * and get all the required information, save it into an
             * hour object, then save that object into the list*/
            JSONObject jsonObj = hourlyData.getJSONObject(i);
            lpadron.me.weatherly.weather.Hourly hour = new lpadron.me.weatherly.weather.Hourly();

            hour.setIcon(jsonObj.getString("icon"));
            hour.setSummary(jsonObj.getString("summary"));
            hour.setTime(jsonObj.getLong("time"));
            hour.setTemp(jsonObj.getDouble("temperature"));
            hour.setTimeZone(timeZone);
            hour.setLatitude(latitude);
            hour.setLongitude(longitude);

            /* Get correct screen color */
            ScreenColor screenColor = new ScreenColor(hour.getTime(), hour.getTimeZone());
            hour.setColor(screenColor.getCorrectColor());

            hourlyWeather[i] = hour;
        }
        return hourlyWeather;
    }

    private lpadron.me.weatherly.weather.Currently getCurrentlyWeather(String json) throws JSONException {
        JSONObject baseData = new JSONObject(json);
        String timeZone = baseData.getString("timezone");

        /* Get Current Weather data and create Weather object with data */
        JSONObject currentData = baseData.getJSONObject("currently");

        lpadron.me.weatherly.weather.Currently currently = new lpadron.me.weatherly.weather.Currently();

        currently.setHumidity(currentData.getDouble("humidity"));
        currently.setIcon(currentData.getString("icon"));
        currently.setPercip(currentData.getDouble("precipProbability"));
        currently.setSummary(currentData.getString("summary"));
        currently.setTime(currentData.getLong("time"));
        currently.setTemp(currentData.getDouble("temperature"));
        currently.setTimeZone(timeZone);

        return currently;
    }

    private void updateData() {
        lpadron.me.weatherly.weather.Currently currently = forecast.getCurrently();
        /* Set the background color */
        RelativeLayout screen = (RelativeLayout) findViewById(R.id.screen);
        ScreenColor screenColor = new ScreenColor(currently.getTime(), currently.getTimeZone());
        screen.setBackgroundColor(getResources().getColor(screenColor.getCorrectColor()));
        /* Set the temperature and time labels */
        tempLabel.setText(currently.getTemp() + "");
        timeLabel.setText(currently.getFormattedTime());
        /* Get/set the correct greeting based on time of day */
        Greeting greeting = new Greeting(currently.getTime(), currently.getTimeZone(),
                currently.getTemp(), currently.getPercip());
        greetingLabel.setText(greeting.getCorrectGreeting());
        /* Set the current humidity and percipitation labels */
        humidityLabel.setText(currently.getHumidity() + "%");
        percipLabel.setText(currently.getPercip() + "%");
        /* Get and set the correct weather icon */
        Drawable drawable = getResources().getDrawable(currently.getIconId());
        iconView.setImageDrawable(drawable);
        /* Get the users city name */
        locationLabel.setText(lpadron.me.weatherly.weather.UsersLocation.getUsersLocation(latitude, longitude, this)) ;
    }


    private boolean isNetwork() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private void reportHttpError() {
        ReportHttpErrorFragment error = new ReportHttpErrorFragment();
        error.show(getFragmentManager(), "http error");
    }
    private void reportNetworkError(){
        ReportNetworkErrorFragment error = new ReportNetworkErrorFragment();
        error.show(getFragmentManager(), "network error");
    }

    @OnClick (R.id.refreshImageView)
    public void refreshTheData(View v) {
        getForecast();
    }

    @OnClick (R.id.dailyButton)
    public void startDailyActivity(View view) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra(DAILY_FORECAST, forecast.getDailyWeather());
        startActivity(intent);
    }

    @OnClick (R.id.hourlyButton)
    public void startHourlyActivity(View view) {
        Intent intent = new Intent(this, HourlyForecastActivity.class);
        intent.putExtra(HOURLY_FORECAST, forecast.getHourlyWeather());
        startActivity(intent);
    }



}
