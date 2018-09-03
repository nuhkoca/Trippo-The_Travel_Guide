package com.nuhkoca.trippo.di.module;

import com.nuhkoca.trippo.ui.OnboardingFragment;
import com.nuhkoca.trippo.ui.settings.SettingsFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilder {

    @ContributesAndroidInjector
    abstract OnboardingFragment contributesOnboardingFragmentInjector();

    @ContributesAndroidInjector
    abstract SettingsFragment contributesSettingsFragmentInjector();
}
