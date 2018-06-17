package com.nuhkoca.trippo.ui.content.feature;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.FirstContentListBinding;
import com.nuhkoca.trippo.databinding.NetworkStateItemBinding;
import com.nuhkoca.trippo.model.remote.content.first.ContentResult;

public class ContentAdapter extends PagedListAdapter<ContentResult, RecyclerView.ViewHolder> {

    private NetworkState mNetworkState;
    private IRetryClickListener mIRetryClickListener;

    private IPopupMenuClickListener mIPopupMenuClickListener;

    private double mLat, mLng;

    private Context mContext;

    ContentAdapter(IRetryClickListener iRetryClickListener, IPopupMenuClickListener iPopupMenuClickListener) {
        super(ContentResult.DIFF_CALLBACK);
        this.mIRetryClickListener = iRetryClickListener;
        this.mIPopupMenuClickListener = iPopupMenuClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (viewType == R.layout.first_content_list) {
            FirstContentListBinding firstContentListBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.first_content_list, parent, false);

            return new ContentViewHolder(firstContentListBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.first_content_list:
                ContentResult contentResult = getItem(position);

                if (contentResult != null) {
                    ((ContentViewHolder) holder).bindTo(contentResult);
                }

                break;

            case R.layout.network_state_item:
                ((NetworkViewHolder) holder).bindTo(mNetworkState);
                break;
        }
    }

    public void swapLatLng(double lat, double lng){
        this.mLat = lat;
        this.mLng = lng;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState != NetworkState.LOADED;
    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return R.layout.network_state_item;
        } else {
            return R.layout.first_content_list;
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

    class ContentViewHolder extends RecyclerView.ViewHolder {

        private FirstContentListBinding firstContentListBinding;

        ContentViewHolder(View itemView) {
            super(itemView);

            firstContentListBinding = DataBindingUtil.getBinding(itemView);
        }

        String calculatedDistance(ContentResult contentResult) {
            LatLng from = new LatLng(contentResult.getCoordinates().getLat(), contentResult.getCoordinates().getLng());
            LatLng to = new LatLng(mLat, mLng);

            return String.format(mContext.getString(R.string.distance_format),
                    SphericalUtil.computeDistanceBetween(from, to) / 1000);
        }

        void bindTo(ContentResult contentResult) {
            if (contentResult.getImages() != null && contentResult.getImages().size() > 0) {
                firstContentListBinding.setVariable(BR.contentImage, contentResult.getImages().get(0).getSizes().getMedium().getUrl());
            } else {
                firstContentListBinding.setVariable(BR.contentImage, "");
            }

            firstContentListBinding.setVariable(BR.contentResult, contentResult);
            firstContentListBinding.setVariable(BR.contentDistance, calculatedDistance(contentResult));
            firstContentListBinding.setVariable(BR.popupMenuItemClickListener, mIPopupMenuClickListener);
            firstContentListBinding.setVariable(BR.adapterPosition, getLayoutPosition());

            firstContentListBinding.executePendingBindings();
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