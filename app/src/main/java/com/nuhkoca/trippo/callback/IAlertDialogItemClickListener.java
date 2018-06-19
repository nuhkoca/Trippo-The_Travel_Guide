package com.nuhkoca.trippo.callback;

public interface IAlertDialogItemClickListener {
    void onIdReceived(int which);

    interface Alert {
        void onPositiveButtonClicked();
    }

    interface Snackbar {
        void onActionListen();
    }

    interface Version {
        void onVersionReceived(int versionCode);
    }
}