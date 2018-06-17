package com.nuhkoca.trippo.ui.content.outside;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.databinding.library.baseAdapters.BR;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.NetworkStateItemBinding;
import com.nuhkoca.trippo.databinding.OutsideContentListBinding;
import com.nuhkoca.trippo.model.remote.content.second.OutsideResult;

public class OutsideContentAdapter extends PagedListAdapter<OutsideResult, RecyclerView.ViewHolder> {

    private NetworkState mNetworkState;
    private IRetryClickListener mIRetryClickListener;

    private IPopupMenuClickListener mIPopupMenuClickListener;

    private Context mContext;

    private double mLat, mLng;

    OutsideContentAdapter(IRetryClickListener iRetryClickListener, IPopupMenuClickListener iPopupMenuClickListener) {
        super(OutsideResult.DIFF_CALLBACK);
        this.mIRetryClickListener = iRetryClickListener;
        this.mIPopupMenuClickListener = iPopupMenuClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (viewType == R.layout.outside_content_list) {
            OutsideContentListBinding outsideContentListBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.outside_content_list, parent, false);

            return new OutsideContentViewHolder(outsideContentListBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.outside_content_list:
                OutsideResult outsideResult = getItem(position);

                if (outsideResult != null) {
                    ((OutsideContentViewHolder) holder).bindTo(outsideResult);
                }

                break;

            case R.layout.network_state_item:
                ((NetworkViewHolder) holder).bindTo(mNetworkState);
                break;
        }
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
            return R.layout.outside_content_list;
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

    public void swapLatLng(double lat, double lng) {
        this.mLat = lat;
        this.mLng = lng;
    }

    class OutsideContentViewHolder extends RecyclerView.ViewHolder {

        private OutsideContentListBinding outsideContentListBinding;

        OutsideContentViewHolder(View itemView) {
            super(itemView);

            outsideContentListBinding = DataBindingUtil.getBinding(itemView);
        }

        String calculatedDistance(OutsideResult outsideResult) {
            LatLng from = new LatLng(outsideResult.getCoordinates().getLat(), outsideResult.getCoordinates().getLng());
            LatLng to = new LatLng(mLat, mLng);

            return String.format(mContext.getString(R.string.distance_format),
                    SphericalUtil.computeDistanceBetween(from, to) / 1000);
        }

        void bindTo(OutsideResult outsideResult) {
            bindThumbnail(outsideResult);
            outsideContentListBinding.setVariable(BR.outsideContentResult, outsideResult);
            outsideContentListBinding.setVariable(BR.outsideDistance, calculatedDistance(outsideResult));
            outsideContentListBinding.setVariable(BR.popupMenuItemClickListener, mIPopupMenuClickListener);
            outsideContentListBinding.setVariable(BR.adapterPosition, getLayoutPosition());
            bindPrice(outsideResult);

            outsideContentListBinding.executePendingBindings();
        }

        private void bindThumbnail(OutsideResult outsideResult) {
            if (outsideResult.getImages() != null && outsideResult.getImages().size() > 0) {
                outsideContentListBinding.setVariable(BR.outsideContentImage,
                        outsideResult.getImages().get(0).getSizes().getMedium().getUrl());
            } else {
                outsideContentListBinding.setVariable(BR.outsideContentImage,
                        "");
            }
        }

        private void bindPrice(OutsideResult outsideResult) {
            if (outsideResult.getBookingInfo() != null) {
                if (outsideResult.getBookingInfo().getPrice() != null) {
                    String[] prices = {outsideResult.getBookingInfo().getPrice().getAmount(),
                            outsideResult.getBookingInfo().getPrice().getCurrency()};

                    outsideContentListBinding.setVariable(BR.price, prices);
                } else {
                    hideViews();
                }
            } else {
                hideViews();
            }
        }

        private void hideViews() {
            outsideContentListBinding.tvOutsideMoneyInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    outsideContentListBinding.tvOutsideMoneyInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    outsideContentListBinding.tvOutsideMoneyInfoSuffix.setVisibility(View.GONE);

                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) outsideContentListBinding.tvOutsideMoneyInfo.getLayoutParams();

                    marginLayoutParams.setMarginEnd(0);

                    outsideContentListBinding.tvOutsideMoneyInfo.setLayoutParams(marginLayoutParams);

                    outsideContentListBinding.tvOutsideMoneyInfo.setText("N/A");
                }
            });
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