package sg.edu.nyp.hackathon;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import sg.edu.nyp.hackathon.activity.HelperNeedyViewActivity;

public class PushyReciever extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getStringExtra("TYPE").equals("NEARBY")){


        String notificationTitle = intent.getStringExtra("TITLE");
        String notificationDesc = "Test notification";

        //-----------------------------
        // Attempt to grab the message
        // property from the payload
        //
        // We will be sending the following
        // test push notification:
        //
        // {"message":"Hello World!"}
        //-----------------------------

        if ( intent.getStringExtra("MESSAGE") != null )
        {
            notificationDesc = intent.getStringExtra("MESSAGE");
        }

            notifyUser(context, notificationTitle,notificationDesc,intent);
        }

    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void notifyUser(Context activity, String header,
                                  String message,Intent intent) {
        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(
                activity.getApplicationContext(), HelperNeedyViewActivity.class);
        notificationIntent.putExtra("LAT",intent.getStringExtra("LAT"));
        notificationIntent.putExtra("LNG",intent.getStringExtra("LNG"));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        stackBuilder.addParentStack(HelperNeedyViewActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(activity)
                .setContentTitle(header)
                .setContentText(message)
                .setContentIntent(pIntent)
                .setDefaults(
                        Notification.DEFAULT_SOUND
                                | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pIntent).setAutoCancel(true)
                .setSmallIcon(android.R.drawable.ic_dialog_info).build();
        notificationManager.notify(2, notification);
    }
}