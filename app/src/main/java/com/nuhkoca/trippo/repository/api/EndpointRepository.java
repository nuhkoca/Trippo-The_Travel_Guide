package com.nuhkoca.trippo.repository.api;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.TrippoApp;
import com.nuhkoca.trippo.model.remote.content.first.ContentWrapper;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.model.remote.content.second.OutsideWrapper;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceWrapper;
import com.nuhkoca.trippo.model.remote.country.CountryWrapper;
import com.nuhkoca.trippo.api.ITrippoAPI;

import retrofit2.Retrofit;
import rx.Observable;

public class EndpointRepository {
    private ITrippoAPI iTrippoAPI;
    private static EndpointRepository instance;

    private EndpointRepository() {
        iTrippoAPI = getRetrofit().create(ITrippoAPI.class);
    }

    public static EndpointRepository getInstance() {
        if (instance == null) {
            instance = new EndpointRepository();
        }

        return instance;
    }

    private static Retrofit getRetrofit() {
        return TrippoApp.provideRetrofit(BuildConfig.BASE_URL);
    }

    public Observable<CountryWrapper> getCountryList(int offset) {
        return iTrippoAPI.getCountryList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, offset);
    }

    public Observable<ContentWrapper> getContentList(String tagLabels, int offset, String partOf) {
        return iTrippoAPI.getContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, partOf);
    }

    public Observable<OutsideWrapper> getOutsideContentList(String tagLabels, int offset, String countryCode, String score, String bookable) {
        return iTrippoAPI.getOutsideContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score, bookable);
    }

    public Observable<ExperienceWrapper> getExperienceContentList(String tagLabels, int offset, String countryCode, String score) {
        return iTrippoAPI.getExperienceContentList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode, score);
    }

    public Observable<ArticleWrapper> getArticleList(String tagLabels, int offset, String countryCode) {
        return iTrippoAPI.getArticleList(BuildConfig.ACCOUNT_ID, BuildConfig.API_TOKEN, tagLabels, offset, countryCode);
    }
}