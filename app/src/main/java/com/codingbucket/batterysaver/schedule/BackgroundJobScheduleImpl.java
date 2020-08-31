package com.codingbucket.batterysaver.schedule;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.codingbucket.batterysaver.BT.BluetoothHandler;
import com.codingbucket.batterysaver.MainActivity;
import com.codingbucket.batterysaver.R;
import com.codingbucket.batterysaver.Utils;
import com.codingbucket.batterysaver.deviceHandler.DeviceHandlerImpl;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class BackgroundJobScheduleImpl extends JobService {
    String address=null;
    private BluetoothHandler bluetoothHandler;


        // Notification channel ID.
        private static final String PRIMARY_CHANNEL_ID =
                "primary_notification_channel";
        // Notification manager.
        NotificationManager mNotifyManager;

        /**
         * Called by the system once it determines it is time to run the job.
         *
         * @param jobParameters Contains the information about the job.
         * @return Boolean indicating whether or not the job was offloaded to a
         * separate thread.
         * In this case, it is false since the notification can be posted on
         * the main thread.
         */
        @Override
        public boolean onStartJob(JobParameters jobParameters) {

            //Change the activity.
            address= Utils.getSetting(this,DeviceHandlerImpl.coding_bucket_batterysaver_address);
//            Toast.makeText(getApplicationContext(), "Address "+address, Toast.LENGTH_LONG).show();
            System.out.println("Address "+address);
            boolean resp = handleChargeShutAction(this);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            msg("Done");
            jobFinished(jobParameters,!resp);
            return !resp;
        }
        /**
         * Called by the system when the job is running but the conditions are no
         * longer met.
         * In this example it is never called since the job is not offloaded to a
         * different thread.
         *
         * @param jobParameters Contains the information about the job.
         * @return Boolean indicating whether the job needs rescheduling.
         */
        @Override
        public boolean onStopJob(JobParameters jobParameters) {
            return false;
        }

        /**
         * Creates a Notification channel, for OREO and higher.
         */
        public void createNotificationChannel() {

            // Create a notification manager object.
            mNotifyManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // Notification channels are only available in OREO and higher.
            // So, add a check on SDK version.
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.O) {

                // Create the NotificationChannel with all the parameters.
                NotificationChannel notificationChannel = new NotificationChannel
                        (PRIMARY_CHANNEL_ID,
                                "notification",
                                NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setDescription
                        ("test");

                mNotifyManager.createNotificationChannel(notificationChannel);
            }
        }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean handleChargeShutAction(Context context){
//        msg("Checking battery level");
        Boolean isBatteryCharged = isBatteryCharged(context);
        if(isBatteryCharged!=null&&isBatteryCharged){
            System.out.println("Battery overcharged. Going to shutdown");
            msg("Battery overcharged. Going to shutdown");
            bluetoothHandler=new BluetoothHandler(address,this);
            bluetoothHandler.shutDownCharging();
            // Create the notification channel.
            DeviceHandlerImpl.removeNotification();
            createNotificationChannel();
            // Set up the notification content intent to launch the app when
            // clicked.
            PendingIntent contentPendingIntent = PendingIntent.getActivity
                    (this, 0, new Intent(this, MainActivity.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (this, PRIMARY_CHANNEL_ID)
                    .setContentTitle(("Charging turned off since threshold reached"))
                    .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_stat_name)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true);
            mNotifyManager.notify(0, builder.build());
            return true;
        }else if(isBatteryCharged==null){
            msg("Not charging");
            return false;
        }else{
            //            DeviceHandlerImpl.scheduleJob(10);
            msg("Battery didn't reach threshold");
            return false;
        }
    }
    public static Boolean isBatteryCharged(Context context) {

        try {
            float threshold = Float.parseFloat(Utils.getSetting(context, DeviceHandlerImpl.batterysaver_saved_value, "99.0"));
            Float currentBatteryPercentage=getCurrentBatteryPercentage(context);
            if(currentBatteryPercentage==null){
                return null;

            }
            System.out.println("currentBatteryPercentage "+currentBatteryPercentage);
            System.out.println("threshold "+threshold);
            return threshold<=currentBatteryPercentage;
        }catch (Exception e){
            return null;
        }
    }

    public static Float getCurrentBatteryPercentage(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        if(plugged != BatteryManager.BATTERY_PLUGGED_AC && plugged != BatteryManager.BATTERY_PLUGGED_USB&& plugged != BatteryManager.BATTERY_PLUGGED_WIRELESS
        ){
            System.out.println("Not charging");

            return null;
        }
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = (level * 100) / (float)scale;
        return batteryPct;
    }

    // fast way to call Toast
    private void msg(String s)
    {
        try {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }catch (Exception ignored){

        }
    }


}

