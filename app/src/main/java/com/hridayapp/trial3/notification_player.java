package com.hridayapp.trial3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;

import com.hridayapp.trial3.Services.NotificationActionService;

public class notification_player extends AppCompatActivity {

    public static final String CHANNEL_ID = "channel1";

    public static final String ACION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;


    public static void CreateNotification(Context context, Track track, int playbutton, int pos, int size){

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), track.getImage());

            PendingIntent pendingIntentprevious;
            int drw_previous;

            if(pos==0){
                pendingIntentprevious=null;
                drw_previous=0;
                drw_previous = android.R.drawable.ic_media_previous;
            }
            else{
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACION_PREVIOUS);

                pendingIntentprevious = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);

                drw_previous = android.R.drawable.ic_media_previous;


            }

            Intent intentplay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);

            PendingIntent pendingIntentplay = PendingIntent.getBroadcast(context, 0,
                    intentplay, PendingIntent.FLAG_UPDATE_CURRENT);


            PendingIntent pendingIntentNext;
            int drw_next;
            if(pos==size){
                pendingIntentNext=null;
                drw_next=0;
                drw_next = android.R.drawable.ic_media_next;
            }
            else{
                Intent intentNext = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);

                pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                        intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

                drw_next = android.R.drawable.ic_media_next;


            }



            //create notification
            Intent clickintent = new Intent(context, MainActivity.class);

            PendingIntent contentappActivity = PendingIntent.getActivity(
                    context, 0 ,clickintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.miniicon)
                    .setContentTitle(track.getTitle())
                    .setContentText(track.getArtist())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(contentappActivity)
                    //show notifcation only first time
            .setShowWhen(false)
                    .addAction(drw_previous,"Previous", pendingIntentprevious)
                    .addAction(playbutton,"Play", pendingIntentplay)
                    .addAction(drw_next,"Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0,1,2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setOngoing(true)
                    .build();

            notificationManagerCompat.notify(1,notification);

        }

    }
    /*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_player);
    }

     */


}