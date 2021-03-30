package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import model.Book;

public class Login extends AppCompatActivity {


    private Button loginButton;
    private Button createAccountButton;
    private AutoCompleteTextView email;
    private EditText password;
    private ProgressBar progressBar;
    private FirebaseUser currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;



    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.email_sign_in_button);
        createAccountButton = findViewById(R.id.register_sign_in_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);


        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, CreateAccount.class));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmailPasswordUser(email.getText().toString().trim(),
                        password.getText().toString().trim());

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();

    }

    //method to check if input is an email
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void loginEmailPasswordUser(String email, String pwd) {

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(pwd) && isValidEmail(email)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                String currentUserId = user.getUid();
                                collectionReference
                                        .whereEqualTo("userId", currentUserId)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                                @Nullable FirebaseFirestoreException e) {
                                                if (e != null) {
                                                }
                                                assert queryDocumentSnapshots != null;
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    progressBar.setVisibility(View.INVISIBLE);
                                                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                                        BookApi bookApi = BookApi.getInstance();
                                                        bookApi.setUsername(snapshot.getString("username"));
                                                        bookApi.setUserId(snapshot.getString("userId"));

                                                        //Go to LibraryActivity
                                                        startActivity(new Intent(Login.this,
                                                                MainMenu.class));
                                                    }
                                                }

                                            }
                                        });
                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Login.this,
                                        "Please enter a valid email and password",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        }
        else {

            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(Login.this,
                    "Please enter a valid email and password",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }
}