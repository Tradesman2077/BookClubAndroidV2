package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.BookApi;

public class ReviewsActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText reviewEditText;
    private Button submitButton;
    private CollectionReference newsColleciton = db.collection("News");
    private CollectionReference reviewsCollection = db.collection("Reviews");
    final String TAG = "REVIEWS_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        String username = BookApi.getInstance().getUsername();


        Objects.requireNonNull(getSupportActionBar()).hide();
        String titleString = getIntent().getExtras().getString("title");
        submitButton = findViewById(R.id.submit_button);
        reviewEditText = findViewById(R.id.editTextTextMultiLine);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map newsData = new HashMap<String, String>();
                Map reviewData = new HashMap<String, String>();
                //add to news timeline
                if (reviewEditText.getText().toString().trim()!=null){
                    newsData.put("category", "review");
                    newsData.put("username", username);
                //add to reviews
                    reviewData.put("content", reviewEditText.getText().toString().trim());
                    reviewData.put("title", titleString);
                    reviewData.put("username", username);

                    newsColleciton.add(newsData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "onSuccess: data added to news ");
                            Toast.makeText(getApplicationContext(), "review added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    reviewsCollection.add(reviewData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getApplicationContext(), "review added", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ReviewsActivity.this, LibraryActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });



    }
}