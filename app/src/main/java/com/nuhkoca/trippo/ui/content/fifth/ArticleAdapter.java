package com.nuhkoca.trippo.ui.content.fifth;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.android.databinding.library.baseAdapters.BR;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.api.NetworkState;
import com.nuhkoca.trippo.callback.ICatalogueItemClickListener;
import com.nuhkoca.trippo.callback.IRetryClickListener;
import com.nuhkoca.trippo.databinding.ArticleListBinding;
import com.nuhkoca.trippo.databinding.NetworkStateItemBinding;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.model.remote.content.fifth.ArticleResult;

public class ArticleAdapter extends PagedListAdapter<ArticleResult, RecyclerView.ViewHolder> {

    private NetworkState mNetworkState;
    private IRetryClickListener mIRetryClickListener;

    private ICatalogueItemClickListener.Article mIArticleItemClickListener;

    ArticleAdapter(IRetryClickListener iRetryClickListener, ICatalogueItemClickListener.Article iArticleItemClickListener) {
        super(ArticleResult.DIFF_CALLBACK);
        this.mIRetryClickListener = iRetryClickListener;
        this.mIArticleItemClickListener = iArticleItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == R.layout.article_list) {
            ArticleListBinding articleListBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.article_list, parent, false);

            return new ArticleViewHolder(articleListBinding.getRoot());
        } else {
            NetworkStateItemBinding networkStateItemBinding = DataBindingUtil.inflate(layoutInflater,
                    R.layout.network_state_item, parent, false);

            return new NetworkViewHolder(networkStateItemBinding.getRoot());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case R.layout.article_list:
                ArticleResult articleResult = getItem(position);

                if (articleResult != null) {
                    ((ArticleViewHolder) holder).bindTo(articleResult);
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
            return R.layout.article_list;
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

    class ArticleViewHolder extends RecyclerView.ViewHolder {

        private ArticleListBinding articleListBinding;

        ArticleViewHolder(View itemView) {
            super(itemView);

            articleListBinding = DataBindingUtil.getBinding(itemView);
        }

        void bindTo(ArticleResult articleResult) {

            bindImage(articleResult);
            articleListBinding.setVariable(BR.articleResult, articleResult);
            articleListBinding.setVariable(BR.articleItemClickListener, mIArticleItemClickListener);

            /*ViewCompat.setTransitionName(articleListBinding.ivArticleThumbnail,
                    Constants.CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION + getAdapterPosition());*/

            articleListBinding.tvArticleName.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    articleListBinding.tvArticleName.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    if (articleListBinding.tvArticleName.getLineCount() > 1) {
                        articleListBinding.tvArticleIntro.setLines(2);
                    } else {
                        articleListBinding.tvArticleIntro.setLines(3);
                    }
                }
            });

            articleListBinding.executePendingBindings();
        }

        private void bindImage(ArticleResult articleResult) {
            if (articleResult.getContent().getImages() != null
                    && articleResult.getContent().getImages().size() > 0) {

                if (articleResult.getContent().getImages().size() > 0) {
                    articleListBinding.setVariable(BR.articleImage,
                            articleResult.getContent().getImages().get(0).getSizes().getMedium().getUrl());
                } else {
                    articleListBinding.setVariable(BR.articleImage, "");
                }
            } else {
                articleListBinding.setVariable(BR.articleImage, "");
            }
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