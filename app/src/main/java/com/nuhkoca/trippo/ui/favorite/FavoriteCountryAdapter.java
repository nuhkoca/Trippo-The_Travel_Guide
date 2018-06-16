package com.nuhkoca.trippo.ui.favorite;

import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Filter;
import android.widget.Filterable;

import com.android.databinding.library.baseAdapters.BR;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.callback.ICatalogueItemClickListener;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.databinding.FavoriteCountryCatalogueListBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.local.entity.FavoriteCountries;
import com.nuhkoca.trippo.model.remote.country.CountryResult;

import java.util.ArrayList;
import java.util.List;

public class FavoriteCountryAdapter extends PagedListAdapter<FavoriteCountries, RecyclerView.ViewHolder> implements Filterable {

    private ICatalogueItemClickListener.Favorite mIFavoriteItemClickListener;
    private IPopupMenuClickListener.Favorite mIFavoritePopupMenuClickListener;

    private List<FavoriteCountries> mFavoriteCountriesList;

    FavoriteCountryAdapter(ICatalogueItemClickListener.Favorite iFavoriteItemClickListener, IPopupMenuClickListener.Favorite iFavoritePopupMenuClickListener) {
        super(FavoriteCountries.DIFF_CALLBACK);
        this.mIFavoriteItemClickListener = iFavoriteItemClickListener;
        this.mIFavoritePopupMenuClickListener = iFavoritePopupMenuClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        FavoriteCountryCatalogueListBinding favoriteCountryCatalogueListBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.favorite_country_catalogue_list, parent, false);

        return new FavoriteCountryViewHolder(favoriteCountryCatalogueListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItem(position) != null) {
            ((FavoriteCountryViewHolder) holder).bindTo(mFavoriteCountriesList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mFavoriteCountriesList != null) {
            return mFavoriteCountriesList.size();
        }
        return super.getItemCount();
    }

    public void swapCatalogue(List<FavoriteCountries> favoriteCountries) {
        mFavoriteCountriesList = favoriteCountries;

        notifyDataSetChanged();
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<FavoriteCountries> currentList) {
        mFavoriteCountriesList = currentList;

        notifyDataSetChanged();

        super.onCurrentListChanged(currentList);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String countryName = constraint.toString();

                if (TextUtils.isEmpty(countryName)) {
                    mFavoriteCountriesList = getCurrentList();
                } else {
                    List<FavoriteCountries> filteredList = new ArrayList<>();

                    for (FavoriteCountries eachCountry : mFavoriteCountriesList) {
                        if (eachCountry.getName().toLowerCase().contains(countryName.toLowerCase())) {
                            filteredList.add(eachCountry);
                        }
                    }

                    mFavoriteCountriesList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFavoriteCountriesList;

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFavoriteCountriesList = (List<FavoriteCountries>) results.values;

                notifyDataSetChanged();
            }
        };
    }

    class FavoriteCountryViewHolder extends RecyclerView.ViewHolder {

        private FavoriteCountryCatalogueListBinding favoriteCountryCatalogueListBinding;

        FavoriteCountryViewHolder(View itemView) {
            super(itemView);

            favoriteCountryCatalogueListBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(FavoriteCountries favoriteCountries) {
            favoriteCountryCatalogueListBinding.setVariable(BR.favoriteCountry, favoriteCountries);
            favoriteCountryCatalogueListBinding.setVariable(BR.adapterPosition, getLayoutPosition());
            favoriteCountryCatalogueListBinding.setVariable(BR.favoritePopupMenuItemClickListener, mIFavoritePopupMenuClickListener);
            favoriteCountryCatalogueListBinding.setVariable(BR.favoriteItemClickListener, mIFavoriteItemClickListener);

            ViewCompat.setTransitionName(favoriteCountryCatalogueListBinding.ivFavoriteThumbnail,
                    Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION + getAdapterPosition());

            favoriteCountryCatalogueListBinding.tvFavoriteName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    favoriteCountryCatalogueListBinding.tvFavoriteName.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (favoriteCountryCatalogueListBinding.tvFavoriteName.getLineCount() > 1) {
                        favoriteCountryCatalogueListBinding.tvFavoriteSnippet.setLines(1);
                    } else {
                        favoriteCountryCatalogueListBinding.tvFavoriteSnippet.setLines(2);
                    }
                }
            });

            favoriteCountryCatalogueListBinding.executePendingBindings();
        }
    }
}