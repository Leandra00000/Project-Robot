package com.example.project_robot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener{

    private Joystick joystick;
    private DatabaseReference mDatabase;
    //private ImageView Image;
    private WebView webView;
    private SeekBar seekBar;
    private TextView val_led;
    private TextView CO2;
    private boolean Start=true;
    //private boolean firstImage=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = (Joystick) findViewById(R.id.Joystick);
        //Image= (ImageView) findViewById(R.id.imageView);
        webView = findViewById(R.id.webView);
        CO2= (TextView) findViewById(R.id.CO2);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        seekBar = findViewById(R.id.seekBar2);
        val_led = findViewById(R.id.led);

        readCO2FromDatabase();
        //readImageFromDatabase();
        setupSeekBarListener();

    }
    private void setupSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                val_led.setVisibility(View.VISIBLE);
                val_led.setText(progress + "/100");

                mDatabase.child("Sensor").child("flash").setValue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Método chamado quando o usuário toca no SeekBar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Método chamado quando o usuário para de tocar no SeekBar
            }
        });
    }

    public void buttonPlayVideo(View view) {
        String videoUrl = "http://192.168.240.209/mjpeg/1";
        webView.loadUrl(videoUrl);
    }

    public void buttonStopVideo(View view) {
        webView.loadUrl("about:blank");
    }

    @Override
    public void onJoystickMoved() {
        float xPercent=joystick.getX();
        float yPercent=joystick.getY();
        mDatabase.child("Joystick").child("Xpercentage").setValue(xPercent);
        mDatabase.child("Joystick").child("Ypercentage").setValue(yPercent);
    }

    /*
    private void readImageFromDatabase() {
        mDatabase.child("Camera").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.getValue(String.class);
                if(imageUrl!=null){
                    if(firstImage){
                        Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .noFade()
                                .into(Image);
                        Image.setVisibility(View.VISIBLE);
                        firstImage=false;
                    }else{
                        Drawable previousDrawable = Image.getDrawable();
                        Picasso.get()
                                .load(imageUrl)
                                .fit()
                                .centerCrop()
                                .noFade()
                                .placeholder(previousDrawable)
                                .into(Image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

    private void updateCO2TextView(int co2Value) {

        if (co2Value == -1) {
            CO2.setTextColor(Color.BLACK); // or any default color
        } else if (co2Value < 1000) {
            CO2.setTextColor(Color.GREEN);
        } else if (co2Value < 2000) {
            CO2.setTextColor(Color.YELLOW);
        } else {
            CO2.setTextColor(Color.RED);
        }

        CO2.setText("CO2: " + (co2Value == -1 ? "Unknown" : co2Value + " PPM"));
    }

    private void readCO2FromDatabase() {
        mDatabase.child("Sensor").child("CO2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer valueCO2 = snapshot.getValue(Integer.class);
                if (valueCO2 != null) {
                    if(Start){
                        mDatabase.child("Sensor").child("CO2").setValue(-1);
                        CO2.setText("CO2: Unknown");
                        Start=false;
                    }else{
                        updateCO2TextView(valueCO2);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}