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

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class EndpointRepository {
    
    private ITrippoAPI iTrippoAPI;

    @Inject
    public EndpointRepository(ITrippoAPI iTrippoAPI) {
        this.iTrippoAPI = iTrippoAPI;
    }

    public Observable<CountryWrapper> getCountryList(int offset) {
        return iTrippoAPI.getCountryList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error);
    }

    public Observable<ContentWrapper> getContentList(String tagLabels, int offset, String partOf) {
        return iTrippoAPI.getContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, partOf)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error);
    }

    public Observable<OutsideWrapper> getOutsideContentList(String tagLabels, int offset, String countryCode, String score, String bookable) {
        return iTrippoAPI.getOutsideContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score, bookable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error);
    }

    public Observable<ExperienceWrapper> getExperienceContentList(String tagLabels, int offset, String countryCode, String score) {
        return iTrippoAPI.getExperienceContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error);
    }

    public Observable<ArticleWrapper> getArticleList(String tagLabels, int offset, String countryCode) {
        return iTrippoAPI.getArticleList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(Constants.DEFAULT_RETRY_COUNT)
                .onErrorResumeNext(Observable::error);
    }
}