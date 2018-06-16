package com.nuhkoca.trippo.callback;

import android.view.View;

import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;

public interface IPopupMenuClickListener {
    void onPopupMenuItemClick(View view, int position);

    interface Favorite {
        void onPopupMenuItemClick(View view, FavoriteCountries favoriteCountries);
    }
}