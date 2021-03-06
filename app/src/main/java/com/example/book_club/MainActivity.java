package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import Util.BookApi;

public class MainActivity extends AppCompatActivity {



    private Button getStartedButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        ///check if already logged into the app before to skip out having to login again

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser!= null){
                    currentUser= firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    collectionReference.whereEqualTo("userId", currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null){
                                return;
                            }
                            if (!value.isEmpty()){
                                for (QueryDocumentSnapshot snapshot : value){
                                    BookApi bookApi = BookApi.getInstance();
                                    bookApi.setUserId(currentUserId);
                                    bookApi.setUsername(snapshot.getString("username"));
                                    startActivity(new Intent(MainActivity.this, LibraryActivity.class));
                                    finish();
                                }
                            }
                        }
                    });
                }
                else{
                    //do nothing
                }

            }
        };

        getStartedButton = findViewById(R.id.start_button);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // log in activity
                startActivity(new Intent(MainActivity.this, Login.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}