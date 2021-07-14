package com.nuhkoca.trippo.util;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;
import com.nuhkoca.trippo.helper.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class SharedPreferenceUtil {

    private SharedPreferences sharedPreferences;
    private FirebaseFirestore firebaseFirestore;

    @Inject
    public SharedPreferenceUtil(SharedPreferences sharedPreferences, FirebaseFirestore firebaseFirestore) {
        this.sharedPreferences = sharedPreferences;
        this.firebaseFirestore = firebaseFirestore;
    }

    public boolean isFirstRun() {
        int savedVersionCode = sharedPreferences.getInt(Constants.VERSION_CODE_KEY, -1);

        if (BuildConfig.VERSION_CODE == savedVersionCode) {
            // normal run
            return false;
        } else if (savedVersionCode == -1) {
            // first run
            return true;
        } else if (BuildConfig.VERSION_CODE > savedVersionCode) {
            //update
            return false;
        } else {
            return false;
        }
    }

    public void checkAndSaveToken(String token, int isNotify) {
        if (!TextUtils.isEmpty(getStringData(Constants.FIRESTORE_DOC_ID_KEY, ""))) {
            firebaseFirestore.collection(Constants.FIRESTORE_COLLECTION_NAME)
                    .document(getStringData(Constants.FIRESTORE_DOC_ID_KEY, ""))
                    .get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {
                            Timber.d("document exists");
                        } else {
                            storeToFirestore(token, isNotify);
                            Timber.d("new data added");
                        }
                    });
        } else {
            storeToFirestore(token, isNotify);
        }
    }

    public void storeToFirestore(String token, int isNotify) {
        Map<String, Object> userToken = new HashMap<>();
        userToken.put(Constants.FIRESTORE_KEY, token);
        userToken.put(Constants.FIRESTORE_PUSH_KEY, isNotify);
        userToken.put(Constants.FIRESTORE_DEVICE_MODEL_KEY, DeviceUtils.model());
        userToken.put(Constants.FIRESTORE_DEVICE_PRODUCT_KEY, DeviceUtils.product());
        userToken.put(Constants.FIRESTORE_DEVICE_API_KEY, DeviceUtils.api());
        userToken.put(Constants.FIRESTORE_DEVICE_DEVICE_KEY, DeviceUtils.device());
        userToken.put(Constants.FIRESTORE_DEVICE_BRAND_KEY, DeviceUtils.brand());

        firebaseFirestore.collection(Constants.FIRESTORE_COLLECTION_NAME)
                .add(userToken)
                .addOnSuccessListener(documentReference -> {
                    Timber.d("token saved successfully.");
                    Timber.d("Token: %s", token);

                    putStringData(Constants.FIRESTORE_DOC_ID_KEY, documentReference.getId());
                })
                .addOnFailureListener(Timber::d);
    }

    public void updateNotification(int isNotifyTheDevice) {
        firebaseFirestore.collection(Constants.FIRESTORE_COLLECTION_NAME)
                .document(getStringData(Constants.FIRESTORE_DOC_ID_KEY, ""))
                .update(Constants.FIRESTORE_PUSH_KEY, isNotifyTheDevice)
                .addOnSuccessListener(aVoid -> Timber.d("notification update success"))
                .addOnFailureListener(Timber::d);
    }

    public void updateToken(String token) {
        firebaseFirestore.collection(Constants.FIRESTORE_COLLECTION_NAME)
                .document(getStringData(Constants.FIRESTORE_DOC_ID_KEY, ""))
                .update(Constants.FIRESTORE_TOKEN_KEY, token)
                .addOnSuccessListener(aVoid -> Timber.d("token update success"))
                .addOnFailureListener(Timber::d);
    }

    public void checkAppVersion(final IAlertDialogItemClickListener.Version iAlertDialogItemClickListener) {
        firebaseFirestore.collection(Constants.FIRESTORE_APP_COLLECTION_NAME)
                .document(Constants.VERSION_DOCUMENT_ID)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Timber.e(e);
                    } else {

                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            iAlertDialogItemClickListener.onVersionReceived(
                                    Integer.parseInt(
                                            Objects.requireNonNull(documentSnapshot.get(Constants.VERSION_COLUMN_NAME)).toString())
                            );
                        } else {
                            Timber.d("document not exists");
                        }
                    }
                });
    }

    public void putIntData(String key, int val) {
        sharedPreferences.edit().putInt(key, val).apply();
    }

    public int getIntData(String key, int defVal) {
        return sharedPreferences.getInt(key, defVal);
    }

    public void putStringData(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getStringData(String key, String defVal) {
        return sharedPreferences.getString(key, defVal);
    }

    public void putBooleanData(String key, boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public boolean getBooleanData(String key, boolean defVal) {
        return sharedPreferences.getBoolean(key, defVal);
    }
}