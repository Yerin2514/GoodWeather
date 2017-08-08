package push.example.a99101.goodweather;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by 99101 on 2017-08-02.
 */

public class FourthActivity extends Fragment {

    Button btn1, btn2;
    View v;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fourth_activity, container, false);

        btn1 = (Button) rootView.findViewById(R.id.imageButton1);
        btn2 = (Button) rootView.findViewById(R.id.imageButton2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://web.kma.go.kr/weather/lifenindustry/currentworld.jsp"));
                startActivity(intent);

            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.weather.kr/index.jsp"));
                startActivity(intent);

            }
        });

        return rootView;
    }
}