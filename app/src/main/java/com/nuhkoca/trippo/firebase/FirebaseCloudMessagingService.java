package com.nuhkoca.trippo.firebase;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import timber.log.Timber;

public class FirebaseCloudMessagingService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        Timber.d(refreshedToken);

        if (!TextUtils.isEmpty(refreshedToken)) {
            if (SharedPreferenceUtil.getInstance().getTokenFromSharedPreference().equals(refreshedToken)) {
                SharedPreferenceUtil.getInstance().updateToken(refreshedToken);
            } else {
                SharedPreferenceUtil.getInstance(refreshedToken, 1).storeToFirestore();
            }
        }
    }
}