package com.quan.hineighboorguide2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {


    private List<Movie> moviesList;


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);

            image = (ImageView) view.findViewById(R.id.image);

        }

        @Override
        public void onClick(View view) {

        }
    }


    public MoviesAdapter(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rcw, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.image.setImageResource(movie.getImage());

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
