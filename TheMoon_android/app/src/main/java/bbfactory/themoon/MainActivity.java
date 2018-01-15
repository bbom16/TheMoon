package bbfactory.themoon;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    int[] array = new int[3]; //년 월 일 배열
    String moonData = "";
    //int[] moonArray = new int[365]; //월령값 저장 배열
    boolean inLuna = false;
    String Lunage = "default";
    TextView data;
    Button sendButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  StrictMode.enableDefaults();
        data = (TextView)findViewById(R.id.data);
        sendButton = (Button) findViewById(R.id.sendButton);
       // load_moon_phase();
        Intent intent = getIntent();
        moonData = intent.getStringExtra("moonData");
        data.setText(moonData);


        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra("moonData", moonData);
                startActivity(intent);
                finish();
            }
        });
    }

}