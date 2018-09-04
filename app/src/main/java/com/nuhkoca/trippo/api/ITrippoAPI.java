package com.nuhkoca.trippo.api;

import android.support.annotation.NonNull;

import com.nuhkoca.trippo.model.remote.content.fifth.ArticleWrapper;
import com.nuhkoca.trippo.model.remote.content.first.ContentWrapper;
import com.nuhkoca.trippo.model.remote.content.second.OutsideWrapper;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceWrapper;
import com.nuhkoca.trippo.model.remote.country.CountryWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ITrippoAPI {

    @GET("location.json?tag_labels=country&count=20&fields=id,name,score,country_id,parent_id,snippet,images,coordinates&order_by=name")
    Observable<CountryWrapper> getCountryList(@Query("account") @NonNull String accountId,
                                              @Query("token") @NonNull String token,
                                              @Query("offset") long offset);


    @GET("location.json?count=20&fields=id,name,score,country_id,parent_id,snippet,images,coordinates&order_by=name")
    Observable<ContentWrapper> getContentList(@Query("account") @NonNull String accountId,
                                              @Query("token") @NonNull String token,
                                              @Query("tag_labels") @NonNull String tagLabels,
                                              @Query("offset") long offset,
                                              @Query("part_of") @NonNull String partOf);


    @GET("poi.json?count=20&order_by=score&fields=id,snippet,images,location_id,coordinates,tag_labels,name,booking_info,score")
    Observable<OutsideWrapper> getOutsideContentList(@Query("account") @NonNull String accountId,
                                                     @Query("token") @NonNull String token,
                                                     @Query("tag_labels") @NonNull String tagLabels,
                                                     @Query("offset") long offset,
                                                     @Query("countrycode") @NonNull String countryCode,
                                                     @Query("score") @NonNull String score,
                                                     @Query("bookable") String bookable);

    @GET("tour.json?count=20&order_by=score&fields=id,name,intro,booking_info,score,vendor,duration_unit,highlights,duration,tag_labels,price_is_per_person,images")
    Observable<ExperienceWrapper> getExperienceContentList(@Query("account") @NonNull String accountId,
                                                           @Query("token") @NonNull String token,
                                                           @Query("tag_labels") @NonNull String tagLabels,
                                                           @Query("offset") long offset,
                                                           @Query("countrycode") @NonNull String countryCode,
                                                           @Query("score") @NonNull String score);

    @GET("article.json?count=20&fields=id,name,intro,snippet,structured_content")
    Observable<ArticleWrapper> getArticleList(@Query("account") @NonNull String accountId,
                                              @Query("token") @NonNull String token,
                                              @Query("tag_labels") @NonNull String tagLabels,
                                              @Query("offset") long offset,
                                              @Query("countrycode") @NonNull String countryCode);
}