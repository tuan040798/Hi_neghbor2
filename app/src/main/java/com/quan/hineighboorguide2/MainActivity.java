package com.quan.hineighboorguide2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.ijvpbsdiwc.adx.service.InterstitialAdsManager;
import com.quan.hineighboorguide2.utils.BackUpModel;
import com.quan.hineighboorguide2.utils.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import vn.aib.ratedialog.RatingDialog;

public class MainActivity extends AppCompatActivity {
    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;
    private String[] listvideo = new String[]{"1.mp4","2.mp4","3.1.mp4","3.2.mp4","3.3.mp4","3.mp4"};
    String sever = "http://data.aib.babylover.me/guide/helloneighbor/";
    private AdView mAdView;
    private BackUpModel backUpModel;
    private String TAG = MainActivity.class.getSimpleName();
//    public static String INTER_ID = "";
//    public static String BANNER_ID = "";

    public static String INTER_ID = "ca-app-pub-3940256099942544/1033173712";
    public static String BANNER_ID = "ca-app-pub-3940256099942544/6300978111";
    private InterstitialAd mInterstitialAd;
    private InterstitialAdsManager adsManager;
    public static int PERCENT_SHOW_BANNER_AD = 100;
    public static int PERCENT_SHOW_INTER_AD = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Void aVoid = new GetBackUp().execute().get();
            if(backUpModel != null){
                if(!backUpModel.isLive){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Notice from developer")
                            .setMessage("Please update the new application to continue using it. We are really sorry for this issue.")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    showApp(MainActivity.this, backUpModel.newAppPackage);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .show();
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        adsManager = new InterstitialAdsManager();
        adsManager.init(true, this, INTER_ID, "#000000", getString(R.string.app_name));
        SharedPreferences prefs = getSharedPreferences("rate_dialog", MODE_PRIVATE);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

//        new FlurryAgent.Builder()
//                .withLogEnabled(true)
//                .build(this, "");

        Boolean rated = prefs.getBoolean("rate", false);
        if(!rated){
            showRateDialog();
        }
        recyclerView = (RecyclerView) findViewById(R.id.rcw_1);

        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareMovieData();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent myIntent = new Intent(MainActivity.this,playvideo.class);
                        myIntent.putExtra("url", sever+listvideo[position]);
                        MainActivity.this.startActivity(myIntent);
                        Random r = new Random();
                        int ads = r.nextInt(100);

                        if (ads < MainActivity.PERCENT_SHOW_INTER_AD){
                            showInterstitial();
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId(MainActivity.INTER_ID);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }


            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                loadAds();
            }
        });
        View adContainer = findViewById(R.id.adMobView);

        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(BANNER_ID);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (adsManager != null)
            adsManager.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void showRateDialog() {
        RatingDialog ratingDialog = new RatingDialog(this);
        ratingDialog.setRatingDialogListener(new RatingDialog.RatingDialogInterFace() {
            @Override
            public void onDismiss() {
            }

            @Override
            public void onSubmit(float rating) {
                rateApp(MainActivity.this);
                SharedPreferences.Editor editor = getSharedPreferences("rate_dialog", MODE_PRIVATE).edit();
                editor.putBoolean("rate", true);
                editor.commit();
            }

            @Override
            public void onRatingChanged(float rating) {
            }
        });
        ratingDialog.showDialog();
    }

    public static void rateApp(Context context) {
        Intent intent = new Intent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private class GetBackUp extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "";
            String jsonStr = sh.makeServiceCall(url);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String appPackage = jsonObj.getString("appPackage");
                    Boolean isLive = jsonObj.getBoolean("isLive");
                    String newAppPackage = jsonObj.getString("newAppPackage");
                    Boolean isAdsShow = jsonObj.getBoolean("isAdsShow");
                    String inter = jsonObj.getString("inter");
                    String fb_inter = jsonObj.getString("fb_inter");
                    Boolean isShowGG = jsonObj.getBoolean("isShowGG");
                    String banner = jsonObj.getString("banner");
                    String nativeAd = jsonObj.getString("nativeAd");
                    String rewarded = jsonObj.getString("rewarded");
                    int percent_banner = jsonObj.getInt("percent_banner");
                    int percent_inter = jsonObj.getInt("percent_inter");
                    int percent_native = jsonObj.getInt("percent_native");
                    int numberNativeAd = jsonObj.getInt("numberNativeAd");

                    backUpModel = new BackUpModel();
                    backUpModel.appPackage = appPackage;
                    backUpModel.isLive = isLive;
                    backUpModel.newAppPackage = newAppPackage;
                    backUpModel.isAdsShow = isAdsShow;
                    backUpModel.inter = inter;
                    backUpModel.fb_inter = fb_inter;
                    backUpModel.isShowGG = isShowGG;
                    backUpModel.banner = banner;
                    backUpModel.nativeAd = nativeAd;
                    backUpModel.rewarded = rewarded;
                    backUpModel.percent_banner = percent_banner;
                    backUpModel.percent_inter = percent_inter;
                    backUpModel.percent_native = percent_native;
                    backUpModel.numberNativeAd = numberNativeAd;

//                    INTER_ID = backUpModel.inter;
//                    BANNER_ID = backUpModel.banner;
//
//                    PERCENT_SHOW_BANNER_AD = backUpModel.percent_banner;
//                    PERCENT_SHOW_INTER_AD = backUpModel.percent_inter;
//                    PERCENT_SHOW_NATIVE_AD = backUpModel.percent_native;
//                    NUMBER_OF_NATIVE_AD = backUpModel.numberNativeAd;

                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });

                }

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public static void showApp(Context context, String pkg) {
        Intent intent = new Intent(new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=" + pkg)));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
    private void loadAds() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }
    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            if (isOnline()) {
                loadAds();
            } else {
                Toast.makeText(MainActivity.this, "Please check network connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void prepareMovieData() {
        Movie movie = new Movie("Part 1",R.mipmap.vd1);
        movieList.add(movie);
        movie = new Movie("Part 2",R.mipmap.vd2);
        movieList.add(movie);
        movie = new Movie("Part 3 - 1",R.mipmap.vd3);
        movieList.add(movie);
        movie = new Movie("Part 3 - 2",R.mipmap.vd31);
        movieList.add(movie);
        movie = new Movie("Part 3 - 3",R.mipmap.vd32);
        movieList.add(movie);
        movie = new Movie("Part 3 - full",R.mipmap.vd33);
        movieList.add(movie);



        mAdapter.notifyDataSetChanged();
    }
}
