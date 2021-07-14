package com.nuhkoca.trippo.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.helper.Constants;
import com.nuhkoca.trippo.ui.MainActivity;
import com.nuhkoca.trippo.util.DeviceUtils;
import com.nuhkoca.trippo.util.SharedPreferenceUtil;

import java.util.Objects;

import javax.inject.Inject;

import timber.log.Timber;

public class FirebaseMessagingHelper extends FirebaseMessagingService {

    @Inject
    SharedPreferenceUtil sharedPreferenceUtil;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        Timber.d(s);

        if (!BuildConfig.DEBUG && !DeviceUtils.isEmulator()) {
            if (!TextUtils.isEmpty(s)) {
                if (sharedPreferenceUtil.getStringData(Constants.FIRESTORE_TOKEN_KEY, "").equals(s)) {
                    sharedPreferenceUtil.updateToken(s);
                } else {
                    sharedPreferenceUtil.storeToFirestore(s,1);
                }
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        handleNotification(remoteMessage);
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Constants.TRIPPO_NOTIFICATION_CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications_24dp))
                .setSmallIcon(R.drawable.ic_notifications_24dp)
                .setContentTitle(Objects.requireNonNull(notification).getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Constants.TRIPPO_NOTIFICATION_ID, notificationBuilder.build());
    }
}