package com.hridayapp.trial3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GestureDetectorCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import com.hridayapp.trial3.Services.AlarmReceiver;
import com.hridayapp.trial3.Services.onClearFromRecentService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements Playable {

    private MediaPlayer mediaPlayer;
    private ImageView backgroundImage;
    private TextView lefttime, righttime;
    private SeekBar seekBar;
    private Button prev, pauseplay, next;
    private Thread thread;
    private View view;
    private Button lyricButton;
    private ImageView whitebackground;
    private Button aartiButton;
    List<Track> tracks;
    private Button sharebutton;
    NotificationManager notificationManager;

    ArrayList<Integer> songs = new ArrayList<>();
    int currentIndex = 0;

    TextView songName;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private int lyricbutotonclick=0;
    private int aartibuttonclick=0;

    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    private int pagenum = 0;
    private String file = "myfile";
    private String filecontents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

                createPersonalizedAd();
            }
        });



        AdView adView = new AdView(this);

        adView.setAdSize(AdSize.BANNER);
        //our banner - ca-app-pub-4562123278832586/8806151974
        //trial banner - a-app-pub-3940256099942544/6300978111

        adView.setAdUnitId("ca-app-pub-4562123278832586/8806151974");


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest1);



        mViewFlipper = (ViewFlipper) findViewById(R.id.flippermain);

        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        MainActivity.CustomGestureDetector customGestureDetector = new MainActivity.CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);


        setupUI();


        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

                int currentpos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                lefttime.setText(dateFormat.format(new Date(currentpos)));
                righttime.setText(dateFormat.format(new Date(duration-currentpos)));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                pauseplay.setBackgroundResource(R.drawable.playbuttonbg);
                thread.interrupt();

                righttime.setText("00:00");
                next();
            }
        });

        //AUTOMATIC 5 second on entry timer (CAN USE IT LATER FOR TUTORIAL or something)
        /*
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {

               Intent intent = new Intent(MainActivity.this, lyricsscreen.class);
               intent.putExtra("currentsongno", currentIndex);


               startActivity(intent);
               overridePendingTransition(R.anim.slide_up,
                       R.anim.slide_down);
           }
       }, 5000);


         */



    }


    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if ((e1.getX() > e2.getX()) && Math.abs(e1.getX() - e2.getX())> SWIPE_THRESHOLD) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_left);
                mViewFlipper.showNext();
                if(pagenum==9) pagenum=0;
                else pagenum++;
                mViewFlipper.clearAnimation();

                filecontents = Integer.toString(pagenum);
                try{
                    FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                    fout.write(filecontents.getBytes());
                    fout.close();

                    //  File fileDir = new File(getFilesDir(), file);
                    // Toast.makeText(getBaseContext(), "FIle saved at "+ fileDir +" " + filecontents, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            // Swipe right (previous)
            if (e1.getX() < e2.getX()&& Math.abs(e1.getX() - e2.getX())> SWIPE_THRESHOLD) {
                mViewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_right);
                mViewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_right);
                mViewFlipper.showPrevious();
                if(pagenum==0) pagenum = 9;
                else pagenum--;
                mViewFlipper.clearAnimation();

                filecontents = Integer.toString(pagenum);
                try{
                    FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                    fout.write(filecontents.getBytes());
                    fout.close();

                    //  File fileDir = new File(getFilesDir(), file);
                    // Toast.makeText(getBaseContext(), "FIle saved at "+ fileDir +" " + filecontents, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }

    private void createPersonalizedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        createInterstitialAd(adRequest);
    }

    private void createInterstitialAd(AdRequest adRequest ){

        //our interstital - ca-app-pub-4562123278832586/2611571845
        //trial intersitital - ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this,"ca-app-pub-4562123278832586/2611571845", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.d("---AdMob", "onAdLoaded");


                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d("---Admob", "The ad was dismissed.");




                        if(aartibuttonclick==1) {
                            Intent intent = new Intent(MainActivity.this, aartipage.class);
                            intent.putExtra("currentsongno", currentIndex);
                            intent.putExtra("pagenum", pagenum);

                            startmusic();
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            aartibuttonclick=0;
                        }
                        else if (lyricbutotonclick==1){

                            Intent intent = new Intent(MainActivity.this, lyricsscreen.class);
                            intent.putExtra("currentsongno", currentIndex);
                            intent.putExtra("pagenum", pagenum);
                            startmusic();

                            startActivity(intent);
                            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

                            lyricbutotonclick=0;
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d("---Admob", "The ad failed to show.");


                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.

                        mInterstitialAd = null;
                        Log.d("---Admob", "The ad was shown.");


                    }
                });

            }


            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d("---AdMob Ad not ready!", loadAdError.getMessage());
             //   Toast.makeText(MainActivity.this, "ad not ready!", Toast.LENGTH_SHORT).show();

                mInterstitialAd = null;
            }
        });

    }



    private void createChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(notification_player.CHANNEL_ID,
                    String.valueOf(R.string.app_name), NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);

            if(notificationManager!= null){
                notificationManager.createNotificationChannel(channel);

            }
        }

    }


    private void populateTracks(){

        tracks = new ArrayList<>();
        tracks.add(new Track("हनुमान  चालीसा", "", R.drawable.hanuman1));
        tracks.add(new Track("संकट मोचन नाम तिहारो", "", R.drawable.hanuman2));
        tracks.add(new Track("बजरँग बाण", "", R.drawable.hanuman3));
        tracks.add(new Track("दुःख भन्जन, मारुती नंदन", "", R.drawable.hanuman4));
        tracks.add(new Track("गल मुरति राम दुलारे", "", R.drawable.hanuman5));
        tracks.add(new Track("जय जय हनुमान", "", R.drawable.hanuman6));
        tracks.add(new Track("आरती कीजै हनुमान लला की", "", R.drawable.hanuman7));

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action){

                case notification_player.ACION_PREVIOUS:
                    onTrackPrevious();
                    break;

                case notification_player.ACTION_PLAY:
                    if(mediaPlayer.isPlaying())
                        onTrackPause();
                    else onTrackPlay();

                    break;

                case  notification_player.ACTION_NEXT:
                    onTrackNext();
                    break;
            }
        }
    };


    private void setupUI() {

        populateTracks();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));

            startService(new Intent(getBaseContext(),
                    onClearFromRecentService.class));


            NotificationChannel channel = new NotificationChannel("My Notification",
                    "NAME notification", NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);


        }



       backgroundImage = (ImageView) findViewById(R.id.backgroundofviewid);

        lefttime = (TextView) findViewById(R.id.lefttextID);
        righttime = (TextView) findViewById(R.id.righttextID);

        pauseplay = (Button) findViewById(R.id.playpausebutton);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        pauseplay.setBackgroundResource(R.drawable.playbuttonbg);
        next = (Button) findViewById(R.id.nextbutton);
        prev = (Button) findViewById(R.id.prevbutton);
        songName = (TextView) findViewById(R.id.songNameId);

        aartiButton = (Button) findViewById(R.id.aartiButtonId);

        whitebackground = (ImageView) findViewById(R.id.backgroundimage);


        songs.add(0,R.raw.hanumansong);
        songs.add(1,R.raw.hanuman2);
        songs.add(2,R.raw.hanuman3);
        songs.add(3,R.raw.hanuman4);
        songs.add(4,R.raw.hanuman5);
        songs.add(5,R.raw.hanuman6);
        songs.add(6,R.raw.hanuman7);


        view = findViewById(R.id.mainViewId);


        mediaPlayer= new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(),songs.get(currentIndex));

        SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

        int duration = mediaPlayer.getDuration();

        lefttime.setText("00:00");
        righttime.setText(dateFormat.format(new Date(duration)));

        lyricButton = (Button) findViewById(R.id.lyricbutton);

        lyricButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lyricbutotonclick=1;

                if (mInterstitialAd != null) {
                    pause();
                    mInterstitialAd.show(MainActivity.this);
                } else {

                    Intent intent = new Intent(MainActivity.this, lyricsscreen.class);
                    intent.putExtra("currentsongno", currentIndex);
                    intent.putExtra("pagenum", pagenum);
                    Log.d("---AdMob", "The interstitial ad wasn't ready yet.");
                    
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            }
        });


        pauseplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notification_player.CreateNotification(MainActivity.this, tracks.get(currentIndex), android.R.drawable.ic_media_pause,1,
                        tracks.size()-1);

                if(mediaPlayer!=null && mediaPlayer.isPlaying())
                    onTrackPause();
                else
                    onTrackPlay();


            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    mViewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_left);
                    mViewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_left);
                    onTrackNext();
                    if(pagenum==9) pagenum=0;
                    else pagenum++;

                    mViewFlipper.setInAnimation(MainActivity.this, android.R.anim.fade_in);
                    mViewFlipper.setOutAnimation(MainActivity.this, android.R.anim.fade_out);

                    filecontents = Integer.toString(pagenum);
                    try{
                        FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                        fout.write(filecontents.getBytes());
                        fout.close();

                        //  File fileDir = new File(getFilesDir(), file);
                        // Toast.makeText(getBaseContext(), "FIle saved at "+ fileDir +" " + filecontents, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    mViewFlipper.setInAnimation(MainActivity.this, R.anim.slide_in_right);
                    mViewFlipper.setOutAnimation(MainActivity.this, R.anim.slide_out_right);
                    onTrackPrevious();
                    if(pagenum==0) pagenum=9;
                    else pagenum--;

                    mViewFlipper.setInAnimation(MainActivity.this, android.R.anim.fade_in);
                    mViewFlipper.setOutAnimation(MainActivity.this, android.R.anim.fade_out);


                    filecontents = Integer.toString(pagenum);
                    try{
                        FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
                        fout.write(filecontents.getBytes());
                        fout.close();

                        //  File fileDir = new File(getFilesDir(), file);
                        // Toast.makeText(getBaseContext(), "FIle saved at "+ fileDir +" " + filecontents, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        aartiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                aartibuttonclick=1;

                if (mInterstitialAd != null) {
                    pause();
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("---AdMob", "The interstitial ad wasn't ready yet.");

                    Intent intent = new Intent(MainActivity.this, aartipage.class);
                    intent.putExtra("currentsongno", currentIndex);
                    intent.putExtra("pagenum", pagenum);

                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);


                }

            }
        });


        sharebutton = (Button) findViewById(R.id.share);

        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Uri imageUri = Uri.parse("android.resource://" + getPackageName()
                        + "/drawable/" + "hanuman7");
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.setType("image/jpg");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "send"));

                 */
                backgroundImage = (ImageView) mViewFlipper.getChildAt(pagenum);
                Drawable drawable= backgroundImage.getDrawable();
                Bitmap bitmap=((BitmapDrawable)drawable).getBitmap();

                try {
                    File file = new File(getApplicationContext().getExternalCacheDir(), File.separator +"hanuman7.jpg");
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    file.setReadable(true, false);
                    final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID +".provider", file);

                    intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                    intent.putExtra(Intent.EXTRA_TEXT, "\"जय श्री राम, जय हनुमान\" "+ "\n\n" + "भगवान हनुमान की आरती, सर्वश्रेष्ठ भजनों का शानदार संग्रह।" +
                            "\n" +
                            "नीचे दिए गए लिंक से Playstore पर डाउनलोड करें।\n" +
                            "\n" +
                            "प्रभु हनुमान आपको शक्ति और बुद्धि का आशीर्वाद दें।\n\nhttps://play.google.com/store/apps/details?id=com.hridayapp.trial3");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setType("image/*");

                    startActivity(Intent.createChooser(intent, "Share image via"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mViewFlipper.setInAnimation(MainActivity.this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(MainActivity.this, android.R.anim.fade_out);

        try{
            FileInputStream fin = openFileInput(file);
            int c;
            String temp = "";

            while ((c = fin.read())!= -1){
                temp = temp + Character.toString((char) c);

            }

            pagenum = Integer.valueOf(temp);
            mViewFlipper.setDisplayedChild(pagenum);
           // Toast.makeText(getBaseContext(), temp, Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startmusic() {

        if(mediaPlayer!=null)
        {
            mediaPlayer.start();
            updateThread();
            pauseplay.setBackgroundResource(R.drawable.pausebuttonbg);
        }

        songnames();
    }

    public void pause() {

        if(mediaPlayer!=null)
        {
            mediaPlayer.pause();
            pauseplay.setBackgroundResource(R.drawable.playbuttonbg);
        }
    }


    public void next(){
        pauseplay.setBackgroundResource(R.drawable.pausebuttonbg);

        if(currentIndex < songs.size()-1){
            currentIndex++;
        }
        else{
            currentIndex=0;
        }

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songs.get(currentIndex));
        mediaPlayer.start();
        startmusic();
        mViewFlipper.showNext();
        songnames();
    }



    public void prev(){
        pauseplay.setBackgroundResource(R.drawable.pausebuttonbg);

        if(currentIndex > 0){
            currentIndex--;
        }
        else{
            currentIndex = songs.size() -1 ;
        }

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(getApplicationContext(),songs.get(currentIndex));
        mediaPlayer.start();
        startmusic();
        mViewFlipper.showPrevious();

        songnames();
    }


    private void songnames(){
        if(currentIndex == 0)
        {
            songName.setText(R.string.songname1);
           // backgroundImage.setImageResource(R.drawable.hanuman1);

        }

        if(currentIndex == 1)
        {
            songName.setText(R.string.songname2);
          //  backgroundImage.setImageResource(R.drawable.hanuman2);
        }

        if(currentIndex == 2)
        {
            songName.setText(R.string.songname3);
           // backgroundImage.setImageResource(R.drawable.hanuman3);
        }

        if(currentIndex == 3)
        {
            songName.setText(R.string.songname4);
           // backgroundImage.setImageResource(R.drawable.hanuman4);
        }

        if(currentIndex == 4)
        {
            songName.setText(R.string.songname5);
           // backgroundImage.setImageResource(R.drawable.hanuman5);
        }

        if(currentIndex == 5)
        {
            songName.setText(R.string.songname6);
          //  backgroundImage.setImageResource(R.drawable.hanuman6);
        }

        if(currentIndex == 6)
        {
            songName.setText(R.string.songname7);
          //  backgroundImage.setImageResource(R.drawable.hanuman7);
        }

    }

    public void updateThread(){

        thread = new Thread(){
            @Override
            public void run() {
                try {

                    while (mediaPlayer != null && mediaPlayer.isPlaying()){

                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newpostion = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newpostion);

                                //update the text
                                lefttime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getCurrentPosition()))));

                                righttime.setText(String.valueOf(new SimpleDateFormat("mm:ss")
                                        .format(new Date(mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()))));


                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        pauseplay.setBackgroundResource(R.drawable.playbuttonbg);
                                        thread.interrupt();

                                        next();

                                        //TODO BACKGROUNDIMAGE = FLIPPER.GETDRAWABLE();
                                    }
                                });

                            }
                        });

                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }


    @Override
    protected void onDestroy() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);

        if(mediaPlayer!=null && mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
        }

        thread.interrupt();
        thread=null;


        filecontents = Integer.toString(0);

        try{
            FileOutputStream fout = openFileOutput(file,MODE_PRIVATE);
            fout.write(filecontents.getBytes());
            fout.close();

            File fileDir = new File(getFilesDir(), file);
            Toast.makeText(getBaseContext(), "FIle saved at "+ fileDir +" " + filecontents, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }



        super.onDestroy();
    }

    @Override
    public void onTrackPrevious() {

        prev();
        notification_player.CreateNotification(MainActivity.this,
                tracks.get(currentIndex), android.R.drawable.ic_media_play,
                currentIndex, tracks.size()-1);
    }

    @Override
    public void onTrackPlay() {

        startmusic();
        notification_player.CreateNotification(MainActivity.this,
                tracks.get(currentIndex), android.R.drawable.ic_media_pause,
                currentIndex, tracks.size()-1);
    }

    @Override
    public void onTrackPause() {

        pause();
        notification_player.CreateNotification(MainActivity.this,
                tracks.get(currentIndex), android.R.drawable.ic_media_play,
                currentIndex, tracks.size()-1);


    }

    @Override
    public void onTrackNext() {

        next();
        notification_player.CreateNotification(MainActivity.this,
                tracks.get(currentIndex), android.R.drawable.ic_media_pause,
                currentIndex, tracks.size()-1);


    }


}