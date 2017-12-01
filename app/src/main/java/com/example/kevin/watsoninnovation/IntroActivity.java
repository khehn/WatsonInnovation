package com.example.kevin.watsoninnovation;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;


import android.content.Intent;
        import android.graphics.Color;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;

        import com.github.paolorotolo.appintro.AppIntro;
        import com.github.paolorotolo.appintro.AppIntro2;
        import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro2 {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        //setContentView(R.layout.activity_intro);
        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*addSlide(firstFragment);
        addSlide(secondFragment);
        addSlide(thirdFragment);
        addSlide(fourthFragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        String title = "Test";
        String description = "Dummy Description";
        int image = R.drawable.iamsterdam;
        int backgroundColor = Color.parseColor("#2196F3");
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));
        addSlide(AppIntroFragment.newInstance(title, description, image, backgroundColor));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));


        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(false);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
        //Intent intent = new Intent(this, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Call the AppIntro java class
        //startActivity(intent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        //Intent intent = new Intent(this, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Call the AppIntro java class
        //startActivity(intent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}