package com.example.kevin.watsoninnovation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.Utils;

public class SwipeActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tinder_layout);

        mSwipeView = (SwipePlaceHolderView)findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 0, "Do you want to be active?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 1, "Do you like adventures?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 2, "What about some relaxing activities?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 3, "Are you alone or with your family?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 4, "Would you consider yourself as young?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 5, "Are you into history?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 6, "What about art?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 7, "Are you a group of people?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 8, "What about some mysterious stuff?"));
        mSwipeView.addView(new TinderCard(mContext, mSwipeView, 9, "Are you playing as a couple?"));






        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });


    }

}