package com.example.quizapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class splash_screen extends AppCompatActivity {

    private TextView appName;

    public static List<String> catList=new ArrayList<>();

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appName= findViewById(R.id.splash);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.myanim);
        appName.setAnimation(anim);

        firestore= FirebaseFirestore.getInstance();

        new Thread(){
            public void run(){
                    loadData();

            }
        }.start();
    }

    private void loadData(){
        catList.clear();
        firestore.collection("QUIZ").document("Categories").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc= task.getResult();
                    if(doc.exists()){
                        long count=(long)doc.get("COUNT");
                        for (int i=0;i<count;i++){
                            String catName=doc.getString("CAT"+String.valueOf(i+1));
                            catList.add(catName);
                        }

                        Intent intent=new Intent(splash_screen.this,MainActivity.class);
                        startActivity(intent);
                        splash_screen.this.finish();
                    }
                    else {
                        Toast.makeText(splash_screen.this,"No Category Document Exist!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                else{
                    Toast.makeText(splash_screen.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
