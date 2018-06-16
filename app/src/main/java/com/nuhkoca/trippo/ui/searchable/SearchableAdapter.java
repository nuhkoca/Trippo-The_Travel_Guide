package com.nuhkoca.trippo.ui.searchable;

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
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.CountryCatalogueListBinding;
import com.nuhkoca.trippo.databinding.NetworkStateItemBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.country.CountryResult;
import com.nuhkoca.trippo.api.NetworkState;

import java.util.ArrayList;
import java.util.List;

public class SearchableAdapter extends PagedListAdapter<CountryResult, RecyclerView.ViewHolder> implements Filterable {

    private NetworkState mNetworkState;
    private IRetryClickListener mIRetryClickListener;
    private ICatalogueItemClickListener mICatalogueItemClickListener;

    private List<CountryResult> mCountryResultList;

    private IPopupMenuClickListener mIPopupMenuClickListener;

    SearchableAdapter(IRetryClickListener iRetryClickListener, ICatalogueItemClickListener iCatalogueItemClickListener, IPopupMenuClickListener iPopupMenuClickListener) {
        super(CountryResult.DIFF_CALLBACK);
        this.mIRetryClickListener = iRetryClickListener;
        this.mICatalogueItemClickListener = iCatalogueItemClickListener;
        this.mIPopupMenuClickListener = iPopupMenuClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == R.layout.country_catalogue_list) {
            CountryCatalogueListBinding countryCatalogueListBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.country_catalogue_list, parent, false);

            return new CatalogueViewHolder(countryCatalogueListBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.country_catalogue_list:
                if (getItem(position) != null) {
                    ((CatalogueViewHolder) holder).bindTo(mCountryResultList.get(position));
                }

                break;

            case R.layout.network_state_item:
                ((NetworkViewHolder) holder).bindTo(mNetworkState);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mCountryResultList != null) {
            return mCountryResultList.size();
        }
        return super.getItemCount();
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    public void swapCatalogue(List<CountryResult> countryResults) {
        mCountryResultList = countryResults;

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.country_catalogue_list;
        }
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        this.mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    @Override
    public void onCurrentListChanged(@Nullable PagedList<CountryResult> currentList) {
        super.onCurrentListChanged(currentList);

        mCountryResultList = currentList;

        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String countryName = constraint.toString();

                if (TextUtils.isEmpty(countryName)) {
                    mCountryResultList = getCurrentList();
                } else {
                    List<CountryResult> filteredList = new ArrayList<>();

                    for (CountryResult eachCountry : mCountryResultList) {
                        if (eachCountry.getName().toLowerCase().contains(countryName.toLowerCase())) {
                            filteredList.add(eachCountry);
                        }
                    }

                    mCountryResultList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mCountryResultList;

                return filterResults;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mCountryResultList = (List<CountryResult>) results.values;

                notifyDataSetChanged();
            }
        };
    }

    class CatalogueViewHolder extends RecyclerView.ViewHolder {

        private CountryCatalogueListBinding countryCatalogueListBinding;

        CatalogueViewHolder(View itemView) {
            super(itemView);

            countryCatalogueListBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(CountryResult countryResult) {
            countryCatalogueListBinding.setVariable(BR.countryResult, countryResult);
            countryCatalogueListBinding.setVariable(BR.catalogueItemClickListener, mICatalogueItemClickListener);
            countryCatalogueListBinding.setVariable(BR.popupMenuItemClickListener, mIPopupMenuClickListener);
            countryCatalogueListBinding.setVariable(BR.adapterPosition, getLayoutPosition());
            countryCatalogueListBinding.setVariable(BR.countryImageMedium, countryResult.getImages().get(0).getSizes().getMedium());

            ViewCompat.setTransitionName(countryCatalogueListBinding.ivCatalogueThumbnail,
                    Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION + getAdapterPosition());

            countryCatalogueListBinding.tvCatalogueCountryName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    countryCatalogueListBinding.tvCatalogueCountryName.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (countryCatalogueListBinding.tvCatalogueCountryName.getLineCount() > 1) {
                        countryCatalogueListBinding.tvCatalogueCountrySnippet.setLines(1);
                    } else {
                        countryCatalogueListBinding.tvCatalogueCountrySnippet.setLines(2);
                    }
                }
            });

            countryCatalogueListBinding.executePendingBindings();
        }
    }

    class NetworkViewHolder extends RecyclerView.ViewHolder {

        private NetworkStateItemBinding networkStateItemBinding;

        NetworkViewHolder(View itemView) {
            super(itemView);

            networkStateItemBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(NetworkState networkState) {
            networkStateItemBinding.setVariable(BR.retryListener, mIRetryClickListener);

            if (networkState != null && networkState.getStatus() == NetworkState.Status.RUNNING) {
                networkStateItemBinding.clNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.pbNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.tvCatalogueListErrButton.setVisibility(View.GONE);
                networkStateItemBinding.tvCatalogueListErrText.setVisibility(View.GONE);
            } else if (networkState != null && networkState.getStatus() == NetworkState.Status.FAILED) {
                networkStateItemBinding.clNetwork.setVisibility(View.VISIBLE);
                networkStateItemBinding.pbNetwork.setVisibility(View.GONE);
                networkStateItemBinding.tvCatalogueListErrButton.setVisibility(View.VISIBLE);
                networkStateItemBinding.tvCatalogueListErrText.setVisibility(View.VISIBLE);
            } else {
                networkStateItemBinding.clNetwork.setVisibility(View.GONE);
                networkStateItemBinding.pbNetwork.setVisibility(View.GONE);
                networkStateItemBinding.tvCatalogueListErrButton.setVisibility(View.GONE);
                networkStateItemBinding.tvCatalogueListErrText.setVisibility(View.GONE);
            }

            networkStateItemBinding.executePendingBindings();
        }
    }
}