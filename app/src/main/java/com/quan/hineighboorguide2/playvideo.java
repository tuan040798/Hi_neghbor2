package com.quan.hineighboorguide2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.Random;

public class playvideo extends AppCompatActivity {
    private AndExoPlayerView andExoPlayerView;

    String video_url ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playvideo);
        andExoPlayerView = findViewById(R.id.andExoPlayerView);
        video_url = getIntent().getStringExtra("url");
        loadMP4ServerSide();
        findViewById(R.id.mp3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMP4ServerSide();
            }
        });
        View adContainer = findViewById(R.id.adMobView);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        mAdView.setAdUnitId(MainActivity.BANNER_ID);
        ((RelativeLayout)adContainer).addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Random r = new Random();
        int ads = r.nextInt(100);

        if (ads >= MainActivity.PERCENT_SHOW_BANNER_AD){
            mAdView.destroy();
            mAdView.setVisibility(View.GONE);
        }
    }
    private void loadMP4ServerSide() {
        andExoPlayerView.setSource(video_url);
    }
}
