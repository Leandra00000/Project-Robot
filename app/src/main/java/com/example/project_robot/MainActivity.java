package com.example.project_robot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
    private ImageView Image;
    private TextView CO2;
    private boolean Start=true;
    private boolean firstImage=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = (Joystick) findViewById(R.id.Joystick);
        Image= (ImageView) findViewById(R.id.imageView);
        CO2= (TextView) findViewById(R.id.CO2);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        readCO2FromDatabase();
        readImageFromDatabase();
    }

    @Override
    public void onJoystickMoved() {
        float xPercent=joystick.getX();
        float yPercent=joystick.getY();
        mDatabase.child("Joystick").child("Xpercentage").setValue(xPercent);
        mDatabase.child("Joystick").child("Ypercentage").setValue(yPercent);
    }

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
    }

    private void updateCO2TextView(int co2Value) {

        if (co2Value == -1) {
            CO2.setTextColor(Color.BLACK); // or any default color
        } else if (co2Value < 400) {
            CO2.setTextColor(Color.GREEN);
        } else if (co2Value < 1000) {
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