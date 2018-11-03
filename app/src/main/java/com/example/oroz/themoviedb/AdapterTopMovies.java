package com.example.oroz.themoviedb;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AdapterTopMovies extends ArrayAdapter<ClassHelper.TopMovies> {
    private List<ClassHelper.TopMovies> topMoviesFull;

    public AdapterTopMovies( Context context, List<ClassHelper.TopMovies> topMovies)
    {
        super(context,0,topMovies);
        topMoviesFull = new ArrayList<>(topMovies);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return artikliFilter;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.my_top_movies, parent, false
            );
        }
        TextView textViewBroj = convertView.findViewById(R.id.txtTopMovieBroj);
        TextView textViewNaziv = convertView.findViewById(R.id.txtTopMovieNaziv);
        TextView textViewOpis = convertView.findViewById(R.id.txtTopMovieOpis);
        ImageView imgSlika = convertView.findViewById(R.id.imgTopMovie);

        ClassHelper.TopMovies topMoviesitem = getItem(position);
        textViewBroj.setText(topMoviesitem.Broj().toString());
        textViewNaziv.setText(topMoviesitem.Naziv());
        textViewOpis.setText(topMoviesitem.Opis());

        String urlll = "https://image.tmdb.org/t/p/original" + topMoviesitem.Poster_path();
        Picasso.get()
                .load(urlll)
                .fit()
                .into(imgSlika);

        return convertView;
    }

    private  Filter artikliFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ClassHelper.TopMovies> suggestions = new ArrayList<>();

            if(constraint == null || constraint.length() == 0)
            {
                suggestions.addAll(topMoviesFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ClassHelper.TopMovies item : topMoviesFull) {
                    if (item.Naziv().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            //noinspection unchecked
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ClassHelper.TopMovies) resultValue).Naziv();
        }
    };


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}
