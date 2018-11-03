package com.example.oroz.themoviedb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements TextWatcher {

    List<ClassHelper.TopMovies> lstTopMovies = new ArrayList<>();
    AdapterTopMovies adapterTopMovies;
    ListView lstTop;
    String urlTopTV ="https://api.themoviedb.org/3/tv/top_rated?page=1&language=en-US&api_key=c3c125a3600bfdfd979c47cc4837f856";
    String urlTopMovies = "https://api.themoviedb.org/3/movie/top_rated?page=1&language=en-US&api_key=c3c125a3600bfdfd979c47cc4837f856";
    String urlSearchMovies ="https://api.themoviedb.org/3/search/movie?api_key=c3c125a3600bfdfd979c47cc4837f856&language=en-US&query=[movie_name]&page=1&include_adult=false";
    String urlSearchTVShows ="https://api.themoviedb.org/3/search/tv?api_key=c3c125a3600bfdfd979c47cc4837f856&language=en-US&query=[show_name]&page=1";
    String tipp ="";
    Button btnMovies, btnTVShows;
    EditText txtFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("The Movie Database App");

        txtFilter = findViewById(R.id.txtFilter);
        btnMovies = findViewById(R.id.btnMovies);
        btnTVShows = findViewById(R.id.btnTVShows);
        lstTop = findViewById(R.id.lstTop);
        adapterTopMovies = new AdapterTopMovies(MainActivity.this, lstTopMovies);
        txtFilter.addTextChangedListener(this);

        tipp ="shows";
        SetbtnBackgroundColor();
        LoadTopMovies loadTopMovies = new LoadTopMovies();
        loadTopMovies.execute();

        btnMovies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"movies".equals(tipp))
                {
                    tipp ="movies";
                    txtFilter.setText("");
                    SetbtnBackgroundColor();
                    LoadTopMovies loadTopMovies = new LoadTopMovies();
                    loadTopMovies.execute();
                }
            }
        });

        btnTVShows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!"shows".equals(tipp))
                {
                tipp ="shows";
                txtFilter.setText("");
                SetbtnBackgroundColor();
                LoadTopMovies loadTopMovies = new LoadTopMovies();
                loadTopMovies.execute();
                }
            }
        });

        lstTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClassHelper.TopMovies item = adapterTopMovies.getItem(i);

                Intent StartDetails = new Intent(getApplicationContext(), MoviesShowsDetails.class);
                StartDetails.putExtra("ID_NUMBER", item.ID().toString());
                StartDetails.putExtra("TIPP", tipp);
                StartDetails.putExtra("OPIS", item.Opis());
                StartDetails.putExtra("NAZIVV", item.Naziv());
                StartDetails.putExtra("POSTER_PATH", item.Poster_path());
                startActivity(StartDetails);
            }
        });



    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 2) {
            adapterTopMovies.getFilter().filter(charSequence);
        }
        else
        {
            adapterTopMovies.getFilter().filter("");
        }
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() < 2) {
            adapterTopMovies.getFilter().filter("");
            return;
        }
        LiveSearch liveSearch = new LiveSearch();
        liveSearch.execute(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() > 2) {
            adapterTopMovies.getFilter().filter(editable);
        }
        else
        {
            adapterTopMovies.getFilter().filter("");
        }
    }


    public class LiveSearch  extends AsyncTask<String,String,String> {
        String z = "";
        Boolean isSuccess = false;

        protected void onPreExecute() {
            adapterTopMovies.clear();
        }

        protected void onPostExecute(String r) {
            if (isSuccess == true) {
                adapterTopMovies = new AdapterTopMovies(MainActivity.this, lstTopMovies);
                lstTop.setAdapter(adapterTopMovies);
                adapterTopMovies.notifyDataSetChanged();
            }
        }

        protected String doInBackground(String... params) {
            URL url = null;
            try {
                if (tipp.equals("movies")) {
                    String tmpURL = urlSearchMovies.replace("[movie_name]", txtFilter.getText());
                    url = new URL(tmpURL);
                } else if (tipp.equals("shows")) {
                    String tmpURL = urlSearchTVShows.replace("[show_name]", txtFilter.getText());
                    url = new URL(tmpURL);
                }
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonResults = response.getJSONArray("results");

                            if (lstTopMovies != null) {
                                lstTopMovies.clear();
                            }

                            if (tipp.equals("movies")) {
                                for (int i = 0; i < jsonResults.length(); i++) {
                                    JSONObject json_data = jsonResults.getJSONObject(i);
                                    lstTopMovies.add(new ClassHelper.TopMovies(json_data.getInt("id"), i + 1, json_data.getString("title"), json_data.getString("overview"), json_data.getString("poster_path")));
                                }
                            } else if (tipp.equals("shows")) {
                                for (int i = 0; i < jsonResults.length(); i++) {
                                    JSONObject json_data = jsonResults.getJSONObject(i);
                                    lstTopMovies.add(new ClassHelper.TopMovies(json_data.getInt("id"), i + 1, json_data.getString("name"), json_data.getString("overview"), json_data.getString("backdrop_path")));
                                }
                            }
                            adapterTopMovies = new AdapterTopMovies(MainActivity.this, lstTopMovies);
                            lstTop.setAdapter(adapterTopMovies);
                            adapterTopMovies.notifyDataSetChanged();

                        } catch (Exception eee) {
                            String aaa = "";
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                });
                queue.add(jsObjRequest);
            } catch (Exception e) {
                String aaa = "";
            }

            return z;
        }
    }

    public class LoadTopMovies extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false;

        protected void onPreExecute() {
            adapterTopMovies.clear();
        }

        protected void onPostExecute(String r) {
            if (isSuccess == true) {
                adapterTopMovies = new AdapterTopMovies(MainActivity.this, lstTopMovies);
                lstTop.setAdapter(adapterTopMovies);
                adapterTopMovies.notifyDataSetChanged();
            }
        }

        protected String doInBackground(String... params) {
            URL url = null;
            try {
                if (tipp.equals("movies")) {
                    url = new URL(urlTopMovies.toString());
                } else if (tipp.equals("shows")) {
                    url = new URL(urlTopTV.toString());
                }

                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url.toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray jsonResults = response.getJSONArray("results");

                            if (lstTopMovies != null) {
                                lstTopMovies.clear();
                            }

                            if (tipp.equals("movies")) {
                                for (int i = 0; i < 10; i++) {
                                    JSONObject json_data = jsonResults.getJSONObject(i);
                                    lstTopMovies.add(new ClassHelper.TopMovies(json_data.getInt("id"), i + 1, json_data.getString("title"), json_data.getString("overview"), json_data.getString("poster_path")));
                                }
                            } else if (tipp.equals("shows")) {
                                for (int i = 0; i < 10; i++) {
                                    JSONObject json_data = jsonResults.getJSONObject(i);
                                    lstTopMovies.add(new ClassHelper.TopMovies(json_data.getInt("id"), i + 1, json_data.getString("name"), json_data.getString("overview"), json_data.getString("backdrop_path")));
                                }
                            }

                            adapterTopMovies = new AdapterTopMovies(MainActivity.this, lstTopMovies);
                            lstTop.setAdapter(adapterTopMovies);
                            adapterTopMovies.notifyDataSetChanged();
                        } catch (Exception eee) {
                            String aaa = "";
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
                queue.add(jsObjRequest);

            } catch (Exception e) {
                String aaa = "";
            }
            return z;
        }
    }

    private void SetbtnBackgroundColor()
    {
        if (tipp.equals("movies")) {
            btnMovies.setBackgroundColor(Color.parseColor("#007AFF"));
            btnTVShows.setBackgroundColor(Color.WHITE);

            btnMovies.setTextColor(Color.WHITE);
            btnTVShows.setTextColor(Color.parseColor("#007AFF"));

        } else if (tipp.equals("shows")) {
            btnMovies.setBackgroundColor(Color.WHITE);
            btnTVShows.setBackgroundColor(Color.parseColor("#007AFF"));

            btnMovies.setTextColor(Color.parseColor("#007AFF"));
            btnTVShows.setTextColor(Color.WHITE);
        }
    }

}
