package ui;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.book_club.BookViewActivity;
import com.example.book_club.LibraryActivity;
import com.example.book_club.R;
import com.example.book_club.ReviewsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import model.Book;

public class BookRecyclerAdaptor extends RecyclerView.Adapter<BookRecyclerAdaptor.ViewHolder> {
    private Context context;
    private List<Book> bookList;

    public BookRecyclerAdaptor(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookRecyclerAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view  = LayoutInflater.from(context)
                .inflate(R.layout.book_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecyclerAdaptor.ViewHolder holder, int position) {
        Book book = bookList.get(position);
        String imageUrl;

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.isbn.setText(book.getIsbn());
        //holder.name.setText(book.getUserName());
        imageUrl = book.getImageUrl();
        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(book.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeAgo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("title", book.getTitle());
                editor.apply();
                Intent intent = new Intent(context, BookViewActivity.class);
                intent.putExtra("title", book.getTitle());
                context.startActivity(intent);
            }
        });

        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_action_name)
                .fit().rotate(90).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title, author, dateAdded, name, isbn;
        public ImageView image;
        public String  userId;
        public String username;


        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public ViewHolder(@NonNull View itemView, Context ctx){

            super(itemView);
            context = ctx;

            title = itemView.findViewById(R.id.book_title_list);
            author = itemView.findViewById(R.id.book_author_list);
            isbn = itemView.findViewById(R.id.book_isbn_list);
            dateAdded = itemView.findViewById(R.id.book_timestamp_list);

            image = itemView.findViewById(R.id.book_image_list);







            //name = itemView.findViewById(R.id.user)



        }

    }
}
