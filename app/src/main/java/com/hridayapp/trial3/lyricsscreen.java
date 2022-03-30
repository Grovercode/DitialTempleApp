package com.hridayapp.trial3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class lyricsscreen extends AppCompatActivity {

    private TextView lyricsText;
    private ScrollView scrollView;
    private StringBuilder text = new StringBuilder();
    int currentIndex;
    private ImageView backgroundImage;
    private String file = "myfile";
    private String filecontents;
    private ViewFlipper mViewFlipper;
    private GestureDetector mGestureDetector;
    public int pagenum = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyricsscreen);

        mViewFlipper = (ViewFlipper) findViewById(R.id.flipperlyrics);

        mViewFlipper.setInAnimation(this, android.R.anim.fade_in);
        mViewFlipper.setOutAnimation(this, android.R.anim.fade_out);

        lyricsscreen.CustomGestureDetector customGestureDetector = new lyricsscreen.CustomGestureDetector();
        mGestureDetector = new GestureDetector(this, customGestureDetector);

        Intent intent = getIntent();


        setuptheUI();

        //scrollView = (ScrollView) findViewById(R.id.scrollviewId);
        lyricsText = (TextView) findViewById(R.id.lyricsTextId);
        //scrollView.addView(lyricsText);
        loadText();

    }

    class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Swipe left (next)
            if ((e1.getX() > e2.getX()) && Math.abs(e1.getX() - e2.getX())> SWIPE_THRESHOLD) {
                mViewFlipper.setInAnimation(lyricsscreen.this, R.anim.slide_in_left);
                mViewFlipper.setOutAnimation(lyricsscreen.this, R.anim.slide_out_left);
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
                mViewFlipper.setInAnimation(lyricsscreen.this, R.anim.slide_in_right);
                mViewFlipper.setOutAnimation(lyricsscreen.this, R.anim.slide_out_right);
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


    public void setuptheUI() {
        lyricsText = (TextView) findViewById(R.id.lyricsTextId);
        Bundle extras = getIntent().getExtras();
        if(extras!=null) {
            currentIndex = extras.getInt("currentsongno");
            pagenum = extras.getInt("pagenum");
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

         */

    }



    public void loadText() {
        String s = "";

            BufferedReader reader = null;

            try {
                if(currentIndex == 0)
                reader = new BufferedReader(new InputStreamReader(
                        getAssets().open("hanumanchalisa.txt")));
                else {
                    String lyricfilename= "hanuman" + String.valueOf(currentIndex+1) + ".txt";

                    reader = new BufferedReader(new InputStreamReader(
                            getAssets().open(lyricfilename)));
                }

                String mLine;

                while((mLine=reader.readLine())!= null){
                    text.append(mLine);
                    text.append('\n');
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Error reading file!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            } finally {
                if(reader!=null){
                    try {
                        reader.close();
                    } catch (IOException e){
                        Toast.makeText(getApplicationContext(),"Error reading file2!", Toast.LENGTH_LONG).show();
                    }
                }
            }

        TextView output= (TextView) findViewById(R.id.lyricsTextId);
        output.setText((CharSequence) text);

        // lyricsText.setText(s);
        output.setMovementMethod(new ScrollingMovementMethod());
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
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}