package com.nuhkoca.trippo.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nuhkoca.trippo.BuildConfig;
import com.nuhkoca.trippo.R;
import com.nuhkoca.trippo.databinding.ActivityAboutBinding;
import com.nuhkoca.trippo.helper.Constants;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private ActivityAboutBinding mActivityAboutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAboutBinding = DataBindingUtil.setContentView(this, R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setupAboutPage();
    }

    private void setupAboutPage() {
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription(getString(R.string.about_description))
                .setImage(R.drawable.ic_launcher_round)
                .addItem(getVersionElement())
                .addGroup(getString(R.string.connect_with_me))
                .addEmail(getString(R.string.email), getString(R.string.email_me))
                .addWebsite(getString(R.string.personal_website), getString(R.string.visit_my_website))
                .addGroup(getString(R.string.follow_me_on_social_media))
                .addFacebook(getString(R.string.personal_facebook), getString(R.string.add_me_on_facebook))
                .addTwitter(getString(R.string.personal_twitter), getString(R.string.follow_me_on_twitter))
                .addYoutube(getString(R.string.personal_youtube), getString(R.string.follow_me_on_youtube))
                .addPlayStore(getString(R.string.personal_play_store), getString(R.string.rate_me_on_google_play_store))
                .addInstagram(getString(R.string.personal_instagram), getString(R.string.follow_me_on_instagram))
                .addGitHub(getString(R.string.github), getString(R.string.fork_me_on_github))
                .addItem(getCopyRightsElement())
                .create();

        mActivityAboutBinding.clAbout.addView(aboutPage, 0);
    }

    private Element getVersionElement() {
        Element versionElement = new Element();

        String version = String.format(getString(R.string.app_version), BuildConfig.VERSION_NAME);

        versionElement.setTitle(version);
        versionElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), getString(R.string.up_to_date_toast), Toast.LENGTH_SHORT).show();
            }
        });

        return versionElement;
    }

    private Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.ic_copy_right_icon);
        copyRightsElement.setIconTint(R.color.aboutItemIconColor);
        copyRightsElement.setIconNightTint(R.color.colorWhite);
        copyRightsElement.setGravity(Gravity.CENTER);
        return copyRightsElement;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClicked = item.getItemId();

        switch (itemThatWasClicked) {
            case android.R.id.home:
                supportFinishAfterTransition();

                int reqCode = getIntent().getIntExtra(Constants.PARENT_ACTIVITY_REQ_KEY, 0);

                if (reqCode == Constants.PARENT_ACTIVITY_REQ_CODE) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    super.onBackPressed();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        super.onBackPressed();
    }
}