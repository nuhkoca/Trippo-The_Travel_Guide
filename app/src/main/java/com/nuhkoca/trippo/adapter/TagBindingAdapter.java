package com.nuhkoca.trippo.adapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;

import com.nuhkoca.trippo.helper.Constants;

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

public class TagBindingAdapter {

    @BindingAdapter(value = "tags")
    public static void bindTag(TagContainerLayout view, String[] tagLabels) {
        List<String> tags = new ArrayList<>(Constants.DEFAULT_TAG_NUMBER);

        if (tagLabels != null) {
            for (String tagLabel : tagLabels) {
                if (!TextUtils.isEmpty(tagLabel)
                        && tagLabel.length() <= Constants.DEFAULT_TAG_LENGTH) {
                    if (tags.size() < Constants.DEFAULT_TAG_NUMBER) {
                        tags.add(tagLabel);
                    }
                }
            }

            view.setTags(tags);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}