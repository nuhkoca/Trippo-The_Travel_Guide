package com.nuhkoca.trippo.util;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.callback.IAlertDialogItemClickListener;

public class AlertDialogUtils {

    private static void createAlertDialog(Activity owner, String title, String[] items, final IAlertDialogItemClickListener iAlertDialogItemClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(owner);

        builder.setTitle(title);

        if (items != null) {
            builder.setItems(items, (dialog, which) -> {
                iAlertDialogItemClickListener.onIdReceived(which);

                dialog.dismiss();
            });
        }

        builder.create().show();
    }

    private static void createAlertDialog(Activity owner, boolean isTitleEnabled, @Nullable String title, String message, String positiveButtonText, boolean isNegativeButton, boolean isCancelable, final IAlertDialogItemClickListener.Alert iAlertDialogItemClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(owner);

        if (title != null && isTitleEnabled) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
        builder.setPositiveButton(positiveButtonText, (dialog, which) -> iAlertDialogItemClickListener.onPositiveButtonClicked());


        if (isNegativeButton) {
            builder.setNegativeButton(owner.getString(R.string.negative_button), (dialog, which) -> dialog.dismiss());
        }

        if (!isCancelable) {
            builder.setCancelable(false);
        }

        builder.create().show();
    }

    public static void dialogWithList(Activity owner, String title, String[] items, IAlertDialogItemClickListener iAlertDialogItemClickListener) {
        createAlertDialog(owner, title, items, iAlertDialogItemClickListener);
    }

    public static void dialogWithAlert(Activity owner, String title, String message, IAlertDialogItemClickListener.Alert iAlertDialogItemClickListener) {
        createAlertDialog(owner, true, title, message, owner.getString(R.string.positive_button), true, false, iAlertDialogItemClickListener);
    }

    public static void dialogWithLicense(Activity owner, String message, IAlertDialogItemClickListener.Alert iAlertDialogItemClickListener) {
        createAlertDialog(owner, false, "", message, owner.getString(R.string.open_button), false, true, iAlertDialogItemClickListener);
    }
}