package com.example.kevin.watsoninnovation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

@Layout(R.layout.tinder_card_view)
public class TinderCard {

    int pos = 0;

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    String text = "";

    public TinderCard(Context context, SwipePlaceHolderView swipeView, int pos, String text) {
        mContext = context;
        mSwipeView = swipeView;
        this.pos = pos;
        this.text = text;
    }

    @Resolve
    private void onResolved(){
        nameAgeTxt.setText(text);
        locationNameTxt.setText("Swipe left or right");

        if(pos==0)
            profileImageView.setImageResource(R.drawable.active);
        if(pos==1)
            profileImageView.setImageResource(R.drawable.adventure);
        if(pos==2)
            profileImageView.setImageResource(R.drawable.relaxing);
        if(pos==3)
            profileImageView.setImageResource(R.drawable.family);
        if(pos==4)
            profileImageView.setImageResource(R.drawable.young);
        if(pos==5)
            profileImageView.setImageResource(R.drawable.history);
        if(pos==6)
            profileImageView.setImageResource(R.drawable.art);
        if(pos==7)
            profileImageView.setImageResource(R.drawable.group);

        profileImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d("EVENT", "onSwipedOut");
        MyApplication x = (MyApplication)mContext.getApplicationContext();
        if(pos==0)
            x.setAction(0);
        if(pos==1)
            x.setAdventure(0);
        if(pos==2)
            x.setCalm(0);
        if(pos==3)
            x.setFamily(0);
        if(pos==4)
            x.setYoung(0);
        if(pos==5)
            x.setHistory(0);
        if(pos==6)
            x.setArt(0);
        if(pos==7)
            x.setGroup(0);
        if(pos==8)
            x.setMystery(0);
        if(pos==9)
            x.setCouple(0);


        if(pos==9){
            Intent intent = new Intent(mContext, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HttpClientTest httpClient0 = new HttpClientTest(mContext,"1000");
            httpClient0.execute();
            HttpClientTest httpClient1 = new HttpClientTest(mContext,"1001");
            httpClient1.execute();
            HttpClientTest httpClient2 = new HttpClientTest(mContext,"1002");
            httpClient2.execute();
            HttpClientTest httpClient3 = new HttpClientTest(mContext,"1003");
            httpClient3.execute();
            HttpClientTest httpClient4 = new HttpClientTest(mContext,"1004");
            httpClient4.execute();
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){
        Log.d("EVENT", "onSwipedIn");
        MyApplication x = (MyApplication)mContext.getApplicationContext();
        if(pos==0)
            x.setAction(1);
        if(pos==1)
            x.setAdventure(1);
        if(pos==2)
            x.setCalm(1);
        if(pos==3)
            x.setFamily(1);
        if(pos==4)
            x.setYoung(1);
        if(pos==5)
            x.setHistory(1);
        if(pos==6)
            x.setArt(1);
        if(pos==7)
            x.setGroup(1);
        if(pos==8)
            x.setMystery(1);
        if(pos==9)
            x.setCouple(1);
        if(pos==9){
            Intent intent = new Intent(mContext, MapsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HttpClientTest httpClient0 = new HttpClientTest(mContext,"1000");
            httpClient0.execute();
            HttpClientTest httpClient1 = new HttpClientTest(mContext,"1001");
            httpClient1.execute();
            HttpClientTest httpClient2 = new HttpClientTest(mContext,"1002");
            httpClient2.execute();
            HttpClientTest httpClient3 = new HttpClientTest(mContext,"1003");
            httpClient3.execute();
            HttpClientTest httpClient4 = new HttpClientTest(mContext,"1004");
            httpClient4.execute();

            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d("EVENT", "onSwipeOutState");
    }
}

