package com.example.project_robot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joystick = findViewById(R.id.Joystick);
        Image= findViewById(R.id.imageView);
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        mDatabase.child("Camera").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imageUrl = snapshot.getValue(String.class);
                Image.setVisibility(View.VISIBLE);
                Picasso.get().load(imageUrl).fit().centerCrop().into(Image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}