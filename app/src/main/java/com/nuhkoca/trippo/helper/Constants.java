package com.nuhkoca.trippo.helper;

import android.Manifest;

public class Constants {
    public static final String TRIPPO_SHARED_PREF = "trippo-sharedpref";
    public static final String FIRST_RUN_KEY = "first-run-key";
    public static final String ONBOARD_PASSED_KEY = "onboard-passed-key";
    public static final String IS_FIRST_AND_AUTH_REQUIRED = "is-first-and-auth-required-key";
    public static final String ONBOARD_TITLE_KEY = "onboard-title-key";
    public static final String ONBOARD_DESCRIPTION_KEY = "onboard-description-key";
    public static final String ONBOARD_IMAGE_KEY = "onboard-image-key";
    public static final String SEARCH_VIEW_FOCUSABLE_EXTRA = "search-view-focusable-extra";
    public static final String[] LOCATION_PERMISSIONS =
            {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    public static final int LOCATION_PERMISSIONS_REQ_CODE = 99;
    public static final int NEARBY_LOCATION_PERMISSIONS_REQ_CODE = 100;
    public static final int EXECUTOR_THREAD_POOL_OFFSET = 5;
    public static final int INITIAL_LOAD_SIZE_HINT = 20;
    public static final int OFFSET_SIZE = 20;
    public static final String SEARCH_VIEW_STATE = "search-view-state";
    public static final String SEARCH_VIEW_QUERY_STATE = "search-view-query-state";
    public static final String BOTTOM_SHEET_STATE = "bottom-sheet-state";
    public static final String PLACE_TYPE_STATE = "place-state";
    public static final String ZOOM_STATE = "zoom-state";
    public static final float DEFAULT_ZOOM_LEVEL = 13;
    public static final String TRIPPO_NOTIFICATION_CHANNEL_ID = "trippo-notification-channel-id";
    public static final int TRIPPO_NOTIFICATION_ID = 1267;
    public static final String FIRESTORE_COLLECTION_NAME = "devices_registered";
    public static final String FIRESTORE_KEY = "device-token";
    public static final String FIRESTORE_PUSH_KEY = "push-notification-key";
    public static final String FIRESTORE_DEVICE_MODEL_KEY = "device-model-key";
    public static final String FIRESTORE_DEVICE_PRODUCT_KEY = "device-product-key";
    public static final String FIRESTORE_DEVICE_API_KEY = "device-api-key";
    public static final String FIRESTORE_DEVICE_DEVICE_KEY = "device-device-key";
    public static final String FIRESTORE_DEVICE_BRAND_KEY = "device-brand-key";
    public static final String FIRESTORE_DOC_ID_KEY = "firestore-doc-id-key";
    public static final String FIRESTORE_TOKEN_KEY = "firestore-token-key";
    public static final String FIRESTORE_IS_NOTIFY_ME_KEY = "firestore-is-notify-me-key";
    public static final int DEFAULT_RETRY_COUNT = 3;
    public static final int PERCENTAGE_TO_ANIMATE_FAB = 80;
    public static final String CATALOGUE_IMAGE_SHARED_ELEMENT_TRANSITION = "catalogue-image-shared-element-transition";
    public static final String CATALOGUE_LAT_REQ = "catalogue-lat-req";
    public static final String CATALOGUE_LNG_REQ = "catalogue-lng-req";
    public static final int PARENT_ACTIVITY_REQ_CODE = 9745;
    public static final String PARENT_ACTIVITY_REQ_KEY = "parent-activity-req-key";
    public static final String CITY_OR_COUNTRY_NAME_KEY = "city-or-country-name-key";
    public static final String COUNTRY_CODE_KEY = "country-code-key";
    public static final String SECTION_TYPE_KEY = "section-type-key";
    public static final String COUNTRY_ID_KEY = "country-id-key";
    public static final int DEFAULT_TAG_NUMBER = 4;
    public static final int DEFAULT_TAG_LENGTH = 15;
    public static final String WEB_URL_KEY = "web-url-key";
    public static final String ACTIVITY_TYPE_KEY = "activity-type-key";
    public static final String TRIPPO_DATABASE_NAME = "Trippo.db";
    public static final String DETAIL_COUNTRY_IMAGE_KEY = "detail-country-image-key";
    public static final String DETAIL_COUNTRY_MEDIUM_IMAGE_KEY = "detail-medium-country-image-key";
    public static final String DETAIL_COUNTRY_NAME_KEY = "detail-country-name-key";
    public static final String DETAIL_COUNTRY_SNIPPET_KEY = "detail-country-snippet-key";
    public static final String DETAIL_COUNTRY_LAT_KEY = "detail-country-lat-key";
    public static final String DETAIL_COUNTRY_LNG_KEY = "detail-country-lng-key";
    public static final String ARTICLE_ENDPOINT_KEY = "article-endpoint-key";
    public static final String PARCELABLE_ARRAY_KEY = "parcelable-array-key";
    public static final int TTS_REQ_CODE = 4532;
    public static final int RC_SIGN_IN = 6749;
    public static final String TRIPPO_UTTRANCE_ID = "trippo-uttrance-id";

    private Constants() {
        throw new AssertionError();
    }
}