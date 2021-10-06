package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.capstoneproject.Model.FoodCategory;
import com.example.capstoneproject.Model.Rating;
import com.example.capstoneproject.ViewHolder.MenuViewHolder;
import com.example.capstoneproject.ViewHolder.RatingViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingList extends AppCompatActivity {
    TextView ratingId;
    TextView ratingCustId;
    TextView ratingComment;
    ImageView ratingStars;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference ratings;

    FirebaseRecyclerAdapter<Rating, RatingViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ratingId = findViewById(R.id.rating_id);
        ratingCustId = findViewById(R.id.rating_custId);
        ratingComment = findViewById(R.id.rating_comment);
        ratingStars = findViewById(R.id.rating_image);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        ratings = database.getReference("Rating");

        recyclerView = findViewById(R.id.listRatings);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadRatings();
    }

    private void loadRatings() {
        adapter = new FirebaseRecyclerAdapter<Rating, RatingViewHolder>(Rating.class, R.layout.rating_layout, RatingViewHolder.class, ratings) {
            @Override
            protected void populateViewHolder(RatingViewHolder ratingViewHolder, Rating rating, int i) {
                ratingViewHolder.ratingId.setText(String.format("Rating #") + adapter.getRef(i).getKey());
                ratingViewHolder.ratingCustId.setText(String.format("Customer ID: ") + rating.getCustID());
                ratingViewHolder.ratingComment.setText(String.format("Comment:       ") + rating.getRateComment());

                int rateStarValue = Integer.parseInt(rating.getRateStars());

                if (rateStarValue == 1) {
                    ratingViewHolder.ratingStars.setImageResource(R.drawable.onestar);
                } else if (rateStarValue == 2) {
                    ratingViewHolder.ratingStars.setImageResource(R.drawable.twostars);
                } else if (rateStarValue == 3) {
                    ratingViewHolder.ratingStars.setImageResource(R.drawable.threestars);
                } else if (rateStarValue == 4) {
                    ratingViewHolder.ratingStars.setImageResource(R.drawable.fourstars);
                } else {
                    ratingViewHolder.ratingStars.setImageResource(R.drawable.fivestars);
                }
            }
        };
        recyclerView.setAdapter(adapter);
    }
}