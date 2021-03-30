package com.example.book_club;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
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

import Util.BookApi;
import model.Book;
import ui.BookRecyclerAdaptor;

public class LibraryActivity extends AppCompatActivity {

    //main library with recycler view and menu displays all books on db

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Book> bookList;
    private RecyclerView recyclerView;
    private BookRecyclerAdaptor bookRecyclerAdaptor;
    private CollectionReference collectionReference = db.collection("Library");
    private TextView noBooksAdded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        getSupportActionBar().setTitle("Library");

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noBooksAdded = findViewById(R.id.list_noBooks_textView);

        //set up recycler view
        bookList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_signout) {
            if (user != null && firebaseAuth != null) {
                //sign out
                firebaseAuth.signOut();
                startActivity(new Intent(LibraryActivity.this, MainActivity.class));
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //add all books to recycler view
    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.get().
        addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (!documentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot book : documentSnapshots){
                        Book book1 = book.toObject(Book.class);
                        bookList.add(book1);
                    }
                    bookRecyclerAdaptor = new BookRecyclerAdaptor(LibraryActivity.this,
                                    bookList);
                    recyclerView.setAdapter(bookRecyclerAdaptor);
                    bookRecyclerAdaptor.notifyDataSetChanged();
                        }
                else{
                    noBooksAdded.setVisibility(View.VISIBLE);
                        }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
    //various icons actions  for menu
    public void search(MenuItem item) {
        startActivity(new Intent(LibraryActivity.this, SearchActivity.class));
    }
    public void addBook(MenuItem item) {

        startActivity(new Intent(LibraryActivity.this, AddBookActivity.class));
    }

    public void favourites(MenuItem item) {
        startActivity(new Intent(LibraryActivity.this, FavouritesActivity.class));
    }
}