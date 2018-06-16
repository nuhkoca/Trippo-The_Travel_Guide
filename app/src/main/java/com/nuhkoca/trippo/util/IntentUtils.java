package com.nuhkoca.trippo.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;

public class IntentUtils {

    public enum ActionType {
        GOOGLE_PLAY,
        REPORT,
        SHARE,
        WEB
    }

    private static IntentUtils INSTANCE;

    private IntentUtils() {
        if (INSTANCE == null) {
            INSTANCE = new IntentUtils();
        }
    }

    public static class Builder {

        private Context mContext;
        private Intent mIntent;
        private String mUrl;

        public Builder() {
            this.mIntent = new Intent();
        }

        public Builder setContext(Context context) {
            mContext = context;
            return this;
        }

        public Builder setUrl(String url) {
            mUrl = url;
            return this;
        }


        public Builder setAction(ActionType type) {
            switch (type) {
                case GOOGLE_PLAY:
                    mIntent.setAction(Intent.ACTION_VIEW);
                    mIntent.setData(Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
                    mContext.startActivity(mIntent);
                    return this;

                case REPORT:
                    mIntent.setAction(Intent.ACTION_SENDTO);
                    mIntent.setData(Uri.parse("mailto:" + mContext.getString(R.string.mail_address)));
                    mContext.startActivity(Intent.createChooser(mIntent, mContext.getResources().getString(R.string.send_with)));
                    return this;

                case SHARE:
                    String extraText = String.format(mContext.getString(R.string.share_exra_text), BuildConfig.APPLICATION_ID);

                    mIntent.setAction(Intent.ACTION_SEND);
                    mIntent.putExtra(Intent.EXTRA_TEXT, extraText);
                    mIntent.setType("text/plain");
                    mContext.startActivity(Intent.createChooser(mIntent, mContext.getResources().getString(R.string.share_with)));
                    return this;

                case WEB:
                    mIntent.setAction(Intent.ACTION_VIEW);
                    mIntent.setData(Uri.parse(mUrl));
                    mContext.startActivity(Intent.createChooser(mIntent, mContext.getResources().getString(R.string.open_with)));
                    return this;

                default:
                    break;
            }

            return null;
        }

        public IntentUtils create() {
            return INSTANCE;
        }
    }
}