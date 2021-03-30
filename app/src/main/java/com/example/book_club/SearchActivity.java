package com.example.book_club;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Library");

    private TextView noResults;
    boolean resultsFound = false;
    private LinearLayout searchResultsLayout;
    private EditText searchText;
    private Button searchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Objects.requireNonNull(getSupportActionBar()).hide();

        searchText = findViewById(R.id.search_Box);
        searchButton =findViewById(R.id.search_button);
        noResults = findViewById(R.id.no_results_textView);
        searchResultsLayout = findViewById(R.id.results_layout);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get keywords convert to arr and pattern match each word
                searchResultsLayout.removeAllViews();
                String keyWords = searchText.getText().toString().trim();
                String[] searchStringWordArr = keyWords.split(" ");

                collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
                            if (snapshot!= null && !keyWords.equals("")){
                                String bookTitle = snapshot.get("title").toString().trim();
                                String authorName = snapshot.get("author").toString().trim();

                                for (String word : searchStringWordArr){
                                    Pattern pattern = Pattern.compile(".*" + word.toLowerCase() + ".*");
                                    Matcher matcher = pattern.matcher(bookTitle.toLowerCase() + authorName.toLowerCase());
                                    if (matcher.find()) {
                                        //if results found then add text view to layout for each result
                                        resultsFound = true;
                                        TextView newTextView = new TextView(getApplicationContext());
                                        newTextView.setText(String.format("%s\n%s\n", bookTitle, authorName));
                                        searchResultsLayout.addView(newTextView);
                                        newTextView.setTextSize(18);
                                        newTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                        newTextView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                //onclick for textViews that takes user to book view
                                                Intent bookViewIntent = new Intent(SearchActivity.this, BookViewActivity.class);
                                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("title", bookTitle);
                                                editor.apply();
                                                startActivity(bookViewIntent);
                                            }
                                        });
                                    }
                                }
                                if (!resultsFound){
                                    noResults.setVisibility(View.VISIBLE);
                                }
                                else{
                                    noResults.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                });





            }
        });
    }
}