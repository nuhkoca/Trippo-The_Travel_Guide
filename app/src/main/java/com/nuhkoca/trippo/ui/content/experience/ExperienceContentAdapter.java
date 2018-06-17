package com.nuhkoca.trippo.ui.content.experience;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.databinding.library.baseAdapters.BR;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.IPopupMenuClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ExperienceContentListBinding;
import com.nuhkoca.trippo.databinding.NetworkStateItemBinding;
import com.nuhkoca.trippo.model.remote.content.third.ExperienceResult;

public class ExperienceContentAdapter extends PagedListAdapter<ExperienceResult, RecyclerView.ViewHolder> {

    private NetworkState mNetworkState;
    private IRetryClickListener mIRetryClickListener;

    private IPopupMenuClickListener mIPopupMenuClickListener;

    ExperienceContentAdapter(IRetryClickListener iRetryClickListener, IPopupMenuClickListener iPopupMenuClickListener) {
        super(ExperienceResult.DIFF_CALLBACK);
        this.mIRetryClickListener = iRetryClickListener;
        this.mIPopupMenuClickListener = iPopupMenuClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == R.layout.experience_content_list) {
            ExperienceContentListBinding experienceContentListBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.experience_content_list, parent, false);

            return new ExperienceContentViewHolder(experienceContentListBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.experience_content_list:
                ExperienceResult experienceResult = getItem(position);

                if (experienceResult != null) {
                    ((ExperienceContentViewHolder) holder).bindTo(experienceResult);
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
            return R.layout.experience_content_list;
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

    class ExperienceContentViewHolder extends RecyclerView.ViewHolder {

        private ExperienceContentListBinding experienceContentListBinding;

        ExperienceContentViewHolder(View itemView) {
            super(itemView);

            experienceContentListBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(ExperienceResult experienceResult) {

            bindImage(experienceResult);
            experienceContentListBinding.setVariable(BR.experienceContentResult, experienceResult);
            experienceContentListBinding.setVariable(BR.popupMenuItemClickListener, mIPopupMenuClickListener);
            experienceContentListBinding.setVariable(BR.adapterPosition, getLayoutPosition());
            bindDuration(experienceResult);
            bindPrice(experienceResult);

            experienceContentListBinding.executePendingBindings();
        }

        private void bindImage(ExperienceResult experienceResult) {
            if (experienceResult.getImages() != null && experienceResult.getImages().size() > 0) {
                experienceContentListBinding.setVariable(BR.experienceContentImage, experienceResult.getImages().get(0).getSizes().getMedium().getUrl());
            } else {
                experienceContentListBinding.setVariable(BR.experienceContentImage, "");
            }
        }

        private void bindDuration(ExperienceResult experienceResult) {
            if (experienceResult.getDuration() != 0 && experienceResult.getDurationUnit() != null && !TextUtils.isEmpty(experienceResult.getDurationUnit())) {

                Object[] durations = {experienceResult.getDuration(), experienceResult.getDurationUnit()};

                experienceContentListBinding.setVariable(BR.duration, durations);
            } else {
                experienceContentListBinding.setVariable(BR.duration, null);
            }
        }

        private void bindPrice(ExperienceResult experienceResult) {
            if (experienceResult.getBookingInfo() != null) {
                if (experienceResult.getBookingInfo().getPrice() != null) {
                    String[] prices = {experienceResult.getBookingInfo().getPrice().getAmount(),
                            experienceResult.getBookingInfo().getPrice().getCurrency()};

                    experienceContentListBinding.setVariable(BR.price, prices);
                } else {
                    hideViews();
                }
            } else {
                hideViews();
            }
        }

        private void hideViews() {
            experienceContentListBinding.tvExperienceMoneyInfo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    experienceContentListBinding.tvExperienceMoneyInfo.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    experienceContentListBinding.tvExperienceMoneyInfoSuffix.setVisibility(View.GONE);

                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) experienceContentListBinding.tvExperienceMoneyInfo.getLayoutParams();

                    marginLayoutParams.setMarginEnd(0);

                    experienceContentListBinding.tvExperienceMoneyInfo.setLayoutParams(marginLayoutParams);

                    experienceContentListBinding.tvExperienceMoneyInfo.setText("N/A");
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