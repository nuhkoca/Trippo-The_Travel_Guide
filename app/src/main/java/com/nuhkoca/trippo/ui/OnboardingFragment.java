package com.nuhkoca.trippo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.FragmentOnboardingBinding;
import com.nuhkoca.trippo.helper.Constants;

import dagger.android.support.DaggerFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class OnboardingFragment extends DaggerFragment {

    private FragmentOnboardingBinding mFragmentOnboardingBinding;

    public static OnboardingFragment newInstance(String title,
                                                 String description,
                                                 @DrawableRes int resId) {
        OnboardingFragment f = new OnboardingFragment();

        Bundle args = new Bundle();
        args.putString(Constants.ONBOARD_TITLE_KEY, title);
        args.putString(Constants.ONBOARD_DESCRIPTION_KEY, description);
        args.putInt(Constants.ONBOARD_IMAGE_KEY, resId);

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

        mFragmentOnboardingBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_onboarding, container, false);

        return mFragmentOnboardingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mFragmentOnboardingBinding.title.setText(getArguments().getString(Constants.ONBOARD_TITLE_KEY));
            mFragmentOnboardingBinding.description.setText(getArguments().getString(Constants.ONBOARD_DESCRIPTION_KEY));
            mFragmentOnboardingBinding.image.setImageResource(getArguments().getInt(Constants.ONBOARD_IMAGE_KEY));
        }
    }
}