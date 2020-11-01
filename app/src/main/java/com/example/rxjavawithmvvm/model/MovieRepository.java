package com.example.rxjavawithmvvm.model;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.rxjavawithmvvm.service.MoviesDataService;
import com.example.rxjavawithmvvm.service.RetrofitInstance;
import com.example.rxjavawithmvvm.view.R;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {
    private Application application;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Movie>> moviesLiveData = new MutableLiveData<>();
    private ArrayList<Movie> movies;
    private Observable<MovieDBResponse> movieDBResponseObservable;

    public MovieRepository(Application application) {
        this.application = application;
    }

    public MutableLiveData<List<Movie>> getMoviesLiveData() {

        movies = new ArrayList<>();
        MoviesDataService getMoviesDataService = RetrofitInstance.getService();
        movieDBResponseObservable = getMoviesDataService.getPopularMoviesWithRx(application.getApplicationContext().getString(R.string.api_key));

        compositeDisposable.add(movieDBResponseObservable
                           .subscribeOn(Schedulers.io())
                           .observeOn(AndroidSchedulers.mainThread())
                           .flatMap(new Function<MovieDBResponse, Observable<Movie>>() {
                               @Override
                               public Observable<Movie> apply(@NonNull MovieDBResponse movieDBResponse) throws Exception {
                                  return Observable.fromArray(movieDBResponse.getMovies().toArray(new Movie[0]));
                               }
                           })
                           .filter(new Predicate<Movie>() {
                               @Override
                               public boolean test(@NonNull Movie movie) throws Exception {
                                   return movie.getVoteAverage()>7.0;
                               }
                           })
                           .subscribeWith(new DisposableObserver<Movie>() {
                               @Override
                               public void onNext(@NonNull Movie movie) {
                                   movies.add(movie);
                               }

                               @Override
                               public void onError(@NonNull Throwable e) {

                               }

                               @Override
                               public void onComplete() {
                                   moviesLiveData.postValue(movies);
                               }
                           }));

        return moviesLiveData;
    }

    public void clear(){
        compositeDisposable.clear();
    }

}
