package push.example.a99101.goodweather;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by 99101 on 2017-08-02.
 */

public class FourthActivity extends Activity {

    Button btn1, btn2;
    View v;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.imageButton1);
        btn2 = (Button)findViewById(R.id.imageButton2);

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

    }

}
