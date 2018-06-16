package com.nuhkoca.trippo.callback;

import android.widget.ImageView;

import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;
import com.nuhkoca.trippo.model.remote.country.CountryResult;

public interface ICatalogueItemClickListener {
    void onItemClick(CountryResult countryResult, ImageView thumbnail, int position);

    interface Favorite {
        void onItemClick(FavoriteCountries favoriteCountries, ImageView thumbnail);
    }

    interface Article {
        void onItemClick(ArticleResult articleResult, ImageView thumbnail);
    }
}