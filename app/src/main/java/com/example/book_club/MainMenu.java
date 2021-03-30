package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import model.Book;
import ui.BookRecyclerAdaptor;

public class MainMenu extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Library");
    private Button libraryButton;
    private LinearLayout newsLayout;
    CollectionReference newsCollection = db.collection("News");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        newsLayout = findViewById(R.id.newsLinearLayout);
        libraryButton = findViewById(R.id.library_main_menu_button);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        user  =firebaseAuth.getCurrentUser();

        newsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    textView.setText( "New " + snapshot.get("category").toString() + " added by " +
                            snapshot.get("username").toString() + " - " + LocalDate.now().toString());
                    newsLayout.addView(textView);
                }
            }
        });

        libraryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainMenu.this, LibraryActivity.class));
            }
        });
    }


}