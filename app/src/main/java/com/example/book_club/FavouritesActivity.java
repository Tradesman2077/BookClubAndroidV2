package com.example.book_club;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import Util.BookApi;

public class FavouritesActivity extends AppCompatActivity {

    private LinearLayout favouritesLayout;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference favouritesCollection = db.collection("Favourites");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Objects.requireNonNull(getSupportActionBar()).hide();

        favouritesLayout = findViewById(R.id.favourites_layout);
        String user = BookApi.getInstance().getUsername();



        favouritesCollection.document(user).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!= null){

                    favouritesCollection.document(user).collection("title").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override

                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){

                                TextView textView = new TextView(getApplicationContext());
                                String currentTitle = snapshot.get("title").toString();
                                textView.setText(currentTitle);
                                textView.setTextSize(16);
                                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                favouritesLayout.addView(textView);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FavouritesActivity.this, BookViewActivity.class);
                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("title", currentTitle);
                                        editor.apply();
                                        startActivity(intent);
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });



    }
}