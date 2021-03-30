package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.BookApi;

public class CreateAccount extends AppCompatActivity {

    private static final String TAG = "REGISTER";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    /// firestore connection

    private FirebaseFirestore db =  FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private Button createUserAccountButton;
    private EditText userNameEditText;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mAuth = FirebaseAuth.getInstance();

        createUserAccountButton = findViewById(R.id.register_sign_in_button);
        progressBar = findViewById(R.id.create_acct_progress);
        emailEditText = findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        userNameEditText = findViewById(R.id.email_account);


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if (currentUser!=null){
                    ///user already logged in
                }
                else {
                    ///no user yet
                }
            }
        };
        createUserAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(emailEditText.getText().toString()) &&
                        !TextUtils.isEmpty(passwordEditText.getText().toString()) &&
                        !TextUtils.isEmpty(userNameEditText.getText().toString())){

                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = userNameEditText.getText().toString().trim();

                    createUserEmailAccount(email, password, username);
                }
                else{
                    Toast.makeText(CreateAccount.this, "empty fields not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    ///method to create an account
    private void createUserEmailAccount(String email, String password, String username){
        if (!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)
        && !TextUtils.isEmpty(username)){
                progressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            Map<String, String> userObj = new HashMap<>();
                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);

                            //save to db
                            collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()){
                                                Log.d("here", "onComplete: " + "here");
                                                progressBar.setVisibility(View.INVISIBLE);
                                                String name = task.getResult().getString("username");
                                                BookApi bookApi = BookApi.getInstance(); // global api
                                                bookApi.setUserId(currentUserId);
                                                bookApi.setUsername(name);

                                                Intent intent = new Intent(CreateAccount.this, MainMenu.class);
                                                intent.putExtra("username", name);
                                                intent.putExtra("user_id", currentUserId);
                                                Toast.makeText(getApplicationContext(), "Account registered", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: + something went wrong");
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e);
                                }
                            });

                        }
                        else{
                            ///something wrong
                            Log.d(TAG, "onComplete: " + "error");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e);
                    }
                });

            }
        else{
            Toast.makeText(getApplicationContext(),
                    "please check credentials have been entered correctly",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(authStateListener);

    }
}