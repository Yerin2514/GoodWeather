package push.example.a99101.goodweather;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import push.example.a99101.adapters.HourlyAdapter;
import push.example.a99101.weather.Hourly;

public class SecondActivity extends AppCompatActivity {

    private Hourly[] hours;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(FirstActivity.HOURLY_FORECAST);
        hours = Arrays.copyOf(parcelables, parcelables.length, Hourly[].class);

        HourlyAdapter adapter = new HourlyAdapter(this, hours);
        recyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

}
