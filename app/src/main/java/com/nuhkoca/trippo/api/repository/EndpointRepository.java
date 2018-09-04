package com.nuhkoca.trippo.api.repository;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.api.ITrippoAPI;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.model.remote.content.first.ContentWrapper;
import com.nuhkoca.trippo.model.remote.content.second.OutsideWrapper;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceWrapper;
import com.nuhkoca.trippo.model.remote.country.CountryWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class EndpointRepository {
    
    private ITrippoAPI iTrippoAPI;

    @Inject
    public EndpointRepository(ITrippoAPI iTrippoAPI) {
        this.iTrippoAPI = iTrippoAPI;
    }

    public Observable<CountryWrapper> getCountryList(long offset) {
        return iTrippoAPI.getCountryList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends CountryWrapper>>) Observable::error);
    }

    public Observable<ContentWrapper> getContentList(String tagLabels, long offset, String partOf) {
        return iTrippoAPI.getContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, partOf)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends ContentWrapper>>) Observable::error);
    }

    public Observable<OutsideWrapper> getOutsideContentList(String tagLabels, long offset, String countryCode, String score, String bookable) {
        return iTrippoAPI.getOutsideContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score, bookable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends OutsideWrapper>>) Observable::error);
    }

    public Observable<ExperienceWrapper> getExperienceContentList(String tagLabels, long offset, String countryCode, String score) {
        return iTrippoAPI.getExperienceContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends ExperienceWrapper>>) Observable::error);
    }

    public Observable<ArticleWrapper> getArticleList(String tagLabels, long offset, String countryCode) {
        return iTrippoAPI.getArticleList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends ArticleWrapper>>) Observable::error);
    }
}