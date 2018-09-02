package com.nuhkoca.trippo.di.module;

import com.nuhkoca.trippo.ui.OnboardingFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract OnboardingFragment contributesOnboardingFragmentInjector();
}
