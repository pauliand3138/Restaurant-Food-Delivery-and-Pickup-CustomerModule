package com.example.capstoneproject.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.R;

public class RatingViewHolder extends RecyclerView.ViewHolder{
    public TextView ratingId;
    public TextView ratingCustId;
    public TextView ratingComment;
    public ImageView ratingStars;


    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);
        ratingId = itemView.findViewById(R.id.rating_id);
        ratingCustId = itemView.findViewById(R.id.rating_custId);
        ratingComment = itemView.findViewById(R.id.rating_comment);
        ratingStars = itemView.findViewById(R.id.rating_image);
    }
}
