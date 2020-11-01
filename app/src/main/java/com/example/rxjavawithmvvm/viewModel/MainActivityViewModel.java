package com.example.rxjavawithmvvm.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.rxjavawithmvvm.model.Movie;
import com.example.rxjavawithmvvm.model.MovieRepository;

import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {
    MovieRepository movieRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        movieRepository=new MovieRepository(application);
    }

    public LiveData<List<Movie>> getAllMovies(){
        return movieRepository.getMoviesLiveData();
    }

    public void clear(){
        movieRepository.clear();
    }
}
