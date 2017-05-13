package mis3.musicplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


//

public class MainActivity extends AppCompatActivity {

    String statusString = "Standing";
    Button startJogging;
    Button startCycling;
    Button stopAll;
    TextView status;
    TextView credits;

    Context context;

    // Music for Jogging - "Goodbye to Spring" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsJoggingString = "Music - \"Goodbye to Spring\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";

    // Music for Cycling - "Go" by Josh Woodward. Free download: http://joshwoodward.com/
    // published under Creative Commons 4.0 Attribution
    String creditsCyclingString = "Music - \"Go\" by Josh Woodward. \nl Free download: http://joshwoodward.com/ \nl published under Creative Commons 4.0 Attribution";

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        startJogging = (Button) findViewById(R.id.startJogging);
        startCycling = (Button) findViewById(R.id.startCycling);
        stopAll = (Button) findViewById(R.id.stopAll);

        status = (TextView) findViewById(R.id.status);
        credits = (TextView) findViewById(R.id.credits);

        stopAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusString = "Standing";
                status.setText(statusString);
                credits.setText("");
                mediaPlayer.release();

            }
        });
        startJogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusString =="Standing"){
                    statusString = "Jogging";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                else if (statusString =="Cycling"){
                    mediaPlayer.release();
                    statusString = "Jogging";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.goodbyetospring);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        });

        startCycling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusString =="Standing"){
                    statusString = "Cycling";
                    status.setText(statusString);
                    credits.setText(creditsJoggingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.go);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                else if (statusString =="Jogging"){
                    mediaPlayer.release();
                    statusString = "Cycling";
                    status.setText(statusString);
                    credits.setText(creditsCyclingString);

                    mediaPlayer = MediaPlayer.create(context, R.raw.go);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            }
        });

    }
}
