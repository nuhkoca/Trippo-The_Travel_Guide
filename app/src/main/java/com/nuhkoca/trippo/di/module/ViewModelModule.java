package com.nuhkoca.trippo.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.nuhkoca.trippo.di.qualifier.ViewModelKey;
import com.nuhkoca.trippo.ui.content.article.ArticleViewModel;
import com.nuhkoca.trippo.ui.content.experience.ExperienceContentViewModel;
import com.nuhkoca.trippo.ui.content.feature.ContentViewModel;
import com.nuhkoca.trippo.ui.content.outside.OutsideContentViewModel;
import com.nuhkoca.trippo.ui.favorite.FavoritesActivityViewModel;
import com.nuhkoca.trippo.ui.nearby.NearbyActivityViewModel;
import com.nuhkoca.trippo.ui.searchable.SearchableActivityViewModel;
import com.nuhkoca.trippo.ui.splash.SplashActivityViewModel;
import com.nuhkoca.trippo.viewmodel.TrippoViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchableActivityViewModel.class)
    abstract ViewModel bindsSearchableActivityViewModel(SearchableActivityViewModel searchableActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(NearbyActivityViewModel.class)
    abstract ViewModel bindsNearbyActivityViewModel(NearbyActivityViewModel NearbyActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SplashActivityViewModel.class)
    abstract ViewModel bindsSplashActivityViewModel(SplashActivityViewModel splashActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesActivityViewModel.class)
    abstract ViewModel bindsFavoritesActivityViewModel(FavoritesActivityViewModel FavoritesActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ExperienceContentViewModel.class)
    abstract ViewModel bindsExperienceContentViewModel(ExperienceContentViewModel experienceContentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(OutsideContentViewModel.class)
    abstract ViewModel bindsOutsideContentViewModel(OutsideContentViewModel outsideContentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ContentViewModel.class)
    abstract ViewModel bindsContentViewModel(ContentViewModel contentViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ArticleViewModel.class)
    abstract ViewModel bindsArticleViewModel(ArticleViewModel articleViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindsTrippoViewModelFactory(TrippoViewModelFactory trippoViewModelFactory);

}
