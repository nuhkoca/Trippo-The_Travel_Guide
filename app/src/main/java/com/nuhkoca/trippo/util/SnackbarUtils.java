package com.nuhkoca.trippo.util;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;

public class SnackbarUtils {

    public enum Length {
        SHORT,
        LONG
    }

    private static SnackbarUtils INSTANCE;

    private SnackbarUtils() {
        if (INSTANCE == null) {
            INSTANCE = new SnackbarUtils();
        }
    }

    public static class Builder {
        View mView;
        CharSequence mMessage;
        Length mLength;

        public Builder setView(View view) {
            this.mView = view;
            return this;
        }

        private View getView() {
            return this.mView;
        }

        public Builder setMessage(CharSequence message) {
            this.mMessage = message;
            return this;
        }

        private CharSequence getMessage() {
            return this.mMessage;
        }

        public Builder setLength(Length length) {
            this.mLength = length;
            return this;
        }

        private Length getLength() {
            return this.mLength;
        }

        public Builder show(CharSequence resId, final IAlertDialogItemClickListener.Snackbar iSnackbarListener) {
            final Snackbar snackbar = Snackbar.make(
                    getView(),
                    getMessage(),
                    getLength() == Length.SHORT ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG);

            snackbar.setAction(resId, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();

                    if (iSnackbarListener != null) {
                        iSnackbarListener.onActionListen();
                    }
                }
            });

            snackbar.show();

            return this;
        }

        public SnackbarUtils build() {
            return INSTANCE;
        }
    }
}