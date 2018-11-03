package com.example.oroz.themoviedb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoviesShowsDetails extends AppCompatActivity {

   String urll= "https://api.themoviedb.org/3/movie/[movie_id]?api_key=c3c125a3600bfdfd979c47cc4837f856&language=en-US";
    String idd="";
    TextView overview, movie_name;
    ImageView imgMovieDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_shows_details);
        ((AppCompatActivity)this).getSupportActionBar().hide();

        overview = findViewById(R.id.overview);
        movie_name = findViewById(R.id.movie_name);
        imgMovieDetails = findViewById(R.id.imgMovieDetails);

        HttpURLConnection urlConnection = null;
        URL url = null;

        idd = getIntent().getExtras().getString("ID_NUMBER","");
        String opiss =getIntent().getExtras().getString("OPIS","");
        String nazivv = getIntent().getExtras().getString("NAZIVV","");
        String poster_path = getIntent().getExtras().getString("POSTER_PATH","");

        overview.setText(opiss);
        movie_name.setText(nazivv);

        String urlll = "https://image.tmdb.org/t/p/original" + poster_path;
        Picasso.get()
                .load(urlll)
                .fit()
                .into(imgMovieDetails);

        LoadDetails loadDetails = new LoadDetails();
        loadDetails.execute();

    }


    public class LoadDetails extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        protected void onPreExecute() {}

        protected void onPostExecute(String r) {         }

        protected String doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;
            InputStream inStream = null;
            try {
                url = new URL(urll.replace("[movie_id]",idd));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.connect();
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null) {
                    response += temp;
                }
                object = (JSONObject) new JSONTokener(response).nextValue();
                String results = object.optString("results");
                JSONArray jsonResults = new JSONArray(results);


                isSuccess = true;

            } catch (Exception e) {
                String aaa = "";
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException ignored) {
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return z;
        }
    }

}

