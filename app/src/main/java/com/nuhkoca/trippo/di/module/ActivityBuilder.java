package com.nuhkoca.trippo.di.module;

import com.nuhkoca.trippo.ui.AboutActivity;
import com.nuhkoca.trippo.ui.AuthActivity;
import com.nuhkoca.trippo.ui.CountryDetailActivity;
import com.nuhkoca.trippo.ui.MainActivity;
import com.nuhkoca.trippo.ui.WebViewActivity;
import com.nuhkoca.trippo.ui.content.article.ArticleDetailActivity;
import com.nuhkoca.trippo.ui.searchable.SearchableActivity;
import com.nuhkoca.trippo.ui.settings.SettingsActivity;
import com.nuhkoca.trippo.ui.splash.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract MainActivity contributesMainActivityInjector();

    @ContributesAndroidInjector
    abstract CountryDetailActivity contributesCountryDetailActivityInjector();

    @ContributesAndroidInjector
    abstract WebViewActivity contributesWebViewActivityInjector();

    @ContributesAndroidInjector
    abstract AboutActivity contributesAboutActivityInjector();

    @ContributesAndroidInjector
    abstract AuthActivity contributesAuthActivityInjector();

    @ContributesAndroidInjector
    abstract SettingsActivity contributesSettingsActivityInjector();

    @ContributesAndroidInjector
    abstract SearchableActivity contributesSearchableActivityInjector();

    @ContributesAndroidInjector
    abstract SplashActivity contributesSplashActivityInjector();

    @ContributesAndroidInjector
    abstract ArticleDetailActivity contributesArticleDetailActivityInjector();
}
