package com.nuhkoca.trippo.test;

import android.text.TextUtils;

import java.util.regex.Pattern;

public class EmailValidator {

    //test the email which is used for reporting if it is valid
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "[a-zA-Z0-9+._%\\-+]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static boolean isValidEmail(CharSequence email) {
        return email != null
                && !TextUtils.isEmpty(email)
                && EMAIL_PATTERN.matcher(email).matches();
    }
}