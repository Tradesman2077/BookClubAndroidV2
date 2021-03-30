package com.example.book_club;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import model.Book;

public class BookViewActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Library");
    private CollectionReference reviewCollection = db.collection("Reviews");
    private CollectionReference favouriteCollection = db.collection("Favourites");

    private ImageView bookViewImage;
    private TextView title;
    private TextView author;
    private TextView isbn;
    private LinearLayout reviewsLayout;
    private Button addToFavouritesButton;
    String currentTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_view);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button leaveReviewButton = (Button) findViewById(R.id.leave_review_button);

        bookViewImage = findViewById(R.id.bookView_imageView);
        title = findViewById(R.id.titile_bookView);
        author = findViewById(R.id.author_bookview);
        isbn = findViewById(R.id.isbn_bookView);
        reviewsLayout = findViewById(R.id.review_bookview_layout);
        addToFavouritesButton = findViewById(R.id.favourite_button);


        firebaseAuth = FirebaseAuth.getInstance();
        user  =firebaseAuth.getCurrentUser();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentTitle = prefs.getString("title", "def");

        leaveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookViewActivity.this, ReviewsActivity.class);
                intent.putExtra("title", currentTitle);
                startActivity(intent);
            }
        });
        addToFavouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> favouriteDataTitle = new HashMap<>();
                favouriteDataTitle.put("title", currentTitle);

                favouriteCollection.document(user.getEmail())
                        .collection("title").add(favouriteDataTitle).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), "Added to favourites", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        //get book data
        collectionReference.whereEqualTo("title", currentTitle).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Book book1 = snapshot.toObject(Book.class);
                        title.setText(book1.getTitle());
                        author.setText(book1.getAuthor());
                        isbn.setText(book1.getIsbn());


                        Picasso.get().load(book1.getImageUrl()).placeholder(R.drawable.ic_action_name)
                                .fit().rotate(90).into(bookViewImage);

                    }
                }
            }
        });

        ///get reviews
        reviewCollection.whereEqualTo("title", currentTitle).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        reviewsLayout.removeAllViews();
                        TextView newTextView = new TextView(getApplicationContext());
                        newTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        newTextView.setText(snapshot.get("username").toString() + "\n" + "'" +  snapshot.get("content") + "'");
                        reviewsLayout.addView(newTextView);
                    }
                }
            }
        });

    }


}