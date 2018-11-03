package com.example.oroz.themoviedb;

import java.io.Serializable;

public class ClassHelper {

    public static class TopMovies implements Serializable
    {
        private Integer ID, Broj;
        private String  Naziv, Opis, Poster_path;
        public  TopMovies(Integer id, Integer broj, String naziv, String opis, String poster_path)
        {
            this.ID = id;
            this.Broj = broj;
            this.Naziv = naziv;
            this.Opis =opis;
            this.Poster_path = poster_path;
        }
        public Integer ID() {return ID;}
        public Integer Broj() {return Broj;}
        public String Naziv() {return Naziv;}
        public String Opis() {return Opis;}
        public String Poster_path() {return  Poster_path;}
    }


}
