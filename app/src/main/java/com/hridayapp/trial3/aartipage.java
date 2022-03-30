package com.hridayapp.trial3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.icu.number.Scale;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import pl.droidsonroids.gif.GifImageView;

public class aartipage extends AppCompatActivity {

   // private ImageView thali;
    private ImageView image;
    private ViewGroup mainLayout;
    private Button bellButton;
    private Button shankButton;
    private Button sharebutton;
    RelativeLayout.LayoutParams layoutParamsbeg;
    RelativeLayout.LayoutParams lParams, lparams2;
    int mflipping = 0;
    int backgroundindex = 0;
    AnimationDrawable animationDrawable;
    float xDOwn=0, yDOwn=0;
   // private GifImageView thali;
    private ViewFlipper thali;
    public SimpleGestureFilter detector;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    public int pagenum=0;
    private String file = "myfile";
    private String filecontents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aartipage);

        mViewFlipper = (ViewFlipper) findViewById(R.id.flipper1);

        mViewFlipper.clearAnimation();

        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);


        setupUI();

        /*


        thali.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                // layoutParamsbeg.removeRule(RelativeLayout.CENTER_HORIZONTAL);
               //  layoutParamsbeg.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        Toast.makeText(aartipage.this, "new location!", Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_MOVE:

                        lParams.leftMargin = x - xDelta;
                        lParams.topMargin = y - yDelta;
                        lParams.rightMargin = 0;
                        lParams.bottomMargin = 0;

                        view.setLayoutParams(lParams);
                        break;

                }


                mainLayout.invalidate();
                return true;

            }
        });

         */

        thali.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getActionMasked()){


                    case MotionEvent.ACTION_DOWN:
                        yDOwn = event.getY();
                        xDOwn = event.getX();

                        break;


                    case MotionEvent.ACTION_MOVE:
                        float movedX, movedY;
                        movedX = event.getX();
                        movedY = event.getY();

                        float distanceX = movedX-xDOwn;
                        float distanceY = movedY-yDOwn;

                        thali.setX(thali.getX()+distanceX);
                        thali.setY(thali.getY()+distanceY);

                        break;




                }

                return true;
            }
        });


        //start flippin
      //  flipper.startFlipping();
       // mflipping=1;
        int currentIndex = 0;
        Bundle extras = getIntent().getExtras();


        if(extras!=null) {
            currentIndex = extras.getInt("currentsongno");
            pagenum = extras.getInt("pagenum");
            mViewFlipper.clearAnimation();
            mViewFlipper.setDisplayedChild(pagenum);
        }

        /*


        if(currentIndex == 0)
        {

            backgroundImage.setImageResource(R.drawable.hanuman1);

        }

        else if(currentIndex == 1)
        {

            backgroundImage.setImageResource(R.drawable.hanuman2);
        }

        else if(currentIndex == 2)
        {

            backgroundImage.setImageResource(R.drawable.hanuman3);
        }

        else if(currentIndex == 3)
        {

            backgroundImage.setImageResource(R.drawable.hanuman4);
        }

       else if(currentIndex == 4)
        {

            backgroundImage.setImageResource(R.drawable.hanuman5);
        }

       else if(currentIndex == 5)
        {

            backgroundImage.setImageResource(R.drawable.hanuman6);
        }

        else if(currentIndex == 6)
        {

            backgroundImage.setImageResource(R.drawable.hanuman7);
        }

        else{
            backgroundImage.setVisibility(View.INVISIBLE);
        }

       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {
              // backgroundImage.setVisibility(View.INVISIBLE);


               backgroundImage.animate().alpha(1).setDuration(800).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                   @Override
                   public void run() {
                       backgroundImage.animate().alpha(0).setDuration(800).setInterpolator(new AccelerateInterpolator()).start();
                   }
               }).start();

               // flipper.setDisplayedChild(100);

           }
       }, 10400);

         */







    }


    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if ((e1.getX() > e2.getX()) && Math.abs(e1.getX() - e2.getX())> SWIPE_THRESHOLD) {
                mViewFlipper.setInAnimation(aartipage.this, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(aartipage.this, R.anim.slide_out_left);
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
                mViewFlipper.setInAnimation(aartipage.this, R.anim.slide_in_right);
                mViewFlipper.setOutAnimation(aartipage.this, R.anim.slide_out_right);
                mViewFlipper.showPrevious();

                if(pagenum==0) pagenum=9;
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


    public void setupUI() {
         image = (ImageView) findViewById(R.id.moortiID);
         // image.setBackgroundResource(R.drawable.anim_slideshow);

         thali = (ViewFlipper) findViewById(R.id.view2);

         thali.startFlipping();

         mainLayout = (RelativeLayout) findViewById(R.id.relativeview1ID);

        layoutParamsbeg = (RelativeLayout.LayoutParams)
                thali.getLayoutParams();

        // layoutParamsbeg.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // layoutParamsbeg.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        lParams = (RelativeLayout.LayoutParams) thali.getLayoutParams();


        /*
        backgrounds.add(0,R.drawable.hanuman1);
        backgrounds.add(1,R.drawable.hanuman2);
        backgrounds.add(2,R.drawable.hanuman3);
        backgrounds.add(3,R.drawable.hanuman4);
        backgrounds.add(4,R.drawable.hanuman5);
        backgrounds.add(5,R.drawable.hanuman6);
        backgrounds.add(6,R.drawable.hanuman7);
        backgrounds.add(7,R.drawable.hanuman8);


        Runnable r = Runnable(){
            public void run(){
                image.setImageResource(backgrounds[backgroundindex]);

            }
        }

         */


        //TODO randomized wallpapers on aartipage

        shankButton = (Button) findViewById(R.id.shankButtonID);
        bellButton = (Button) findViewById(R.id.bellButtonID);
        final MediaPlayer bell = MediaPlayer.create(this, R.raw.bell);
        final MediaPlayer shank = MediaPlayer.create(this, R.raw.bell2);

        bellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(bell.isPlaying()){
                    bell.seekTo(0);
                }
                else bell.start();
            }
        });

        shankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(shank.isPlaying()){
                    shank.seekTo(0);
                }
                else shank.start();


            }
        });




    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mViewFlipper.clearAnimation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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

        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }




}


