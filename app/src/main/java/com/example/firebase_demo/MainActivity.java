package com.example.firebase_demo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private EditText emailET, nameET, contactET;
    private RadioGroup maritalRG;
    private String key;
    private Map<String, User> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailET = findViewById(R.id.emailET);
        nameET = findViewById(R.id.nameET);
        contactET = findViewById(R.id.contactET);
        maritalRG = findViewById(R.id.maritalRG);

        dataMap = new HashMap<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataMap.put(dataSnapshot.getKey(), dataSnapshot.getValue(User.class));
            }

            @SuppressLint("NewApi")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                dataMap.replace(dataSnapshot.getKey(), dataSnapshot.getValue(User.class));
            }

            @SuppressLint("NewApi")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                dataMap.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addBClicked(View view){
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String contact = contactET.getText().toString();
        String marital = ((RadioButton)findViewById(maritalRG.getCheckedRadioButtonId())).getText().toString();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        User user = new User(email, name, contact, marital);
        key = UUID.randomUUID().toString();
        databaseReference.child(key).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Record added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void searchBClicked(View view){
        String email = emailET.getText().toString();
        key = null;
        for(Map.Entry<String, User> entry:dataMap.entrySet()){
            User user = entry.getValue();
            if(email.equalsIgnoreCase(user.email)){
                nameET.setText(user.name);
                contactET.setText(user.contact);
                if(user.marital.equalsIgnoreCase("Married")){
                    ((RadioButton)findViewById(R.id.marriedRB)).setChecked(true);
                }else{
                    ((RadioButton)findViewById(R.id.singleRB)).setChecked(true);
                }
                findViewById(R.id.updateB).setEnabled(true);
                findViewById(R.id.delB).setEnabled(true);
                key = entry.getKey();
                break;
            }else{
                key = null;
            }
        }

        if(key==null){
            Toast.makeText(this, "Record not found", Toast.LENGTH_SHORT).show();
            findViewById(R.id.updateB).setEnabled(false);
            findViewById(R.id.delB).setEnabled(false);
        }
    }
    public void updateClicked(View view){
        String email = emailET.getText().toString();
        String name = nameET.getText().toString();
        String contact = contactET.getText().toString();
        String marital = ((RadioButton)findViewById(maritalRG.getCheckedRadioButtonId())).getText().toString();
        User user = new User(email, name, contact, marital);
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(key).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Record updated successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void delBClicked(View view){
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MainActivity.this, "Record removed successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void resetBClicked(View view){
        emailET.setText("");
        nameET.setText("");
        contactET.setText("");
        maritalRG.clearCheck();
        findViewById(R.id.updateB).setEnabled(false);
        findViewById(R.id.delB).setEnabled(false);
    }
}