package com.example.project_robot;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements Joystick.JoystickListener{

    Joystick joystick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = findViewById(R.id.Joystick);
    }

    @Override
    public void onJoystickMoved() {
        float xPercent=joystick.getX();
        float yPercent=joystick.getY();
        Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
    }
}