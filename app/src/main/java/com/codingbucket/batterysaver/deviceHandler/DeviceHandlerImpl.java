package com.codingbucket.batterysaver.deviceHandler;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.codingbucket.batterysaver.BT.BluetoothHandler;
import com.codingbucket.batterysaver.Configuration;
import com.codingbucket.batterysaver.MainActivity;
import com.codingbucket.batterysaver.R;
import com.codingbucket.batterysaver.Utils;
import com.codingbucket.batterysaver.schedule.BackgroundJobScheduleImpl;

import java.io.IOException;
import java.util.UUID;

import static com.codingbucket.batterysaver.MainActivity.EXTRA_ADDRESS;

public class DeviceHandlerImpl extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary";
    Button  btnDis,btnSave,btnScheduleOn,btnScheduleOff,btnOn,btnoff,btnCon,btnConf;
    public  static String batterysaver_saved_value="batterysaver_saved_value";
    public  static String coding_bucket_batterysaver_address="coding_bucket_batterysaver_address";
    String address=null;
    private  static NotificationManager mNotifyManager;
    BluetoothHandler bluetoothHandler;

    private static final int JOB_ID = 0;
    public static JobScheduler mScheduler;

    public DeviceHandlerImpl() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        Intent newint = getIntent();
        address = newint.getStringExtra(EXTRA_ADDRESS); //receive the address of the bluetooth device
        Utils.saveSetting(this,coding_bucket_batterysaver_address,address);
        //view of the ledControl
        setContentView(R.layout.activity_device_handler_impl);
        //call the widgtes
        btnScheduleOff = (Button) findViewById(R.id.button7);
        btnScheduleOn = (Button) findViewById(R.id.button6);
        btnDis = (Button) findViewById(R.id.button4);
        btnSave = (Button) findViewById(R.id.button5);
        btnOn = (Button) findViewById(R.id.button8);
        btnCon = (Button) findViewById(R.id.button10);

        btnoff = (Button) findViewById(R.id.button9);
        btnConf = (Button) findViewById(R.id.button11);


        bluetoothHandler=new BluetoothHandler(address,getApplicationContext());
        bluetoothHandler.connect();
        NumberPicker np = findViewById(R.id.numberPicker);

        np.setMinValue(2);
        np.setMaxValue(100);
        np.setValue(Integer.parseInt(Utils.getSetting(this,batterysaver_saved_value,"100")));

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBatterPercentage();      //method to save on
            }
        });
        btnScheduleOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleOff();      //method to turn on
            }
        });
        btnScheduleOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleJob(10);      //method to turn on
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//               v Disconnect(); //close connection
            }
        });
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;      //method to turn on
                handleOn();
            }
        });
        btnConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ;      //method to turn on
                Intent i = new Intent(DeviceHandlerImpl.this, Configuration.class);

                //Change the activity.
                Toast.makeText(getApplicationContext(), "Address "+address, Toast.LENGTH_LONG).show();
                System.out.println("Address "+address);
                i.putExtra(EXTRA_ADDRESS, address); //this will be received at ledControl (class) Activity
                startActivity(i);
            }
        });
        btnoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleOff();      //method to turn on
            }
        });
        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothHandler.connect();      //method to turn on
            }
        });
//        BackgroundJob.handleChargeShutAction(this);
    }

    private void handleOff() {
        bluetoothHandler.sendCommand(Utils.getSetting(this, Configuration.batterysaver_saved_value_off,"0"));
    }

    private void handleOn() {
        bluetoothHandler.sendCommand(Utils.getSetting(this, Configuration.batterysaver_saved_value_on,"50"));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleOff() {
        int numberofJobs =0;
        removeNotification();
        if (mScheduler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                numberofJobs = mScheduler.getAllPendingJobs().size();
            }
            mScheduler.cancelAll();
            mScheduler = null;
            Toast.makeText(this, "Jobs Cancelled. "+(numberofJobs>0?"Number of Jobs Cancelled "+numberofJobs:""), Toast.LENGTH_SHORT).show();
        }
    }

    public static void removeNotification() {
        if(mNotifyManager!=null) {
            mNotifyManager.cancel(1);
        }
    }

    private void saveBatterPercentage() {

        NumberPicker numberPicker=(NumberPicker) findViewById(R.id.numberPicker);
        System.out.println("Save BatterPercentage Called");

            msg("selected battery percentage "+numberPicker.getValue());
            Utils.saveSetting(DeviceHandlerImpl.this,batterysaver_saved_value,String.valueOf(numberPicker.getValue()));
            msg("Value Saved "+numberPicker.getValue());

    }


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob(int time) {
        if(mScheduler==null){
            mScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName serviceName = new ComponentName(getPackageName(),BackgroundJobScheduleImpl.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
        builder.setMinimumLatency(2*60 * 1000); // wait at least
        builder.setOverrideDeadline(15 * 1000); // maximum delay
        builder.setPersisted(true);
        JobInfo myJobInfo = builder.build();
        mScheduler.schedule(myJobInfo);
        Toast.makeText(this, "Scheduled", Toast.LENGTH_SHORT).show();

        creteNotification();

    }

    private void creteNotification() {
        createNotificationChannel();
        PendingIntent contentPendingIntent = PendingIntent.getActivity
                (this, 2, new Intent(this, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (this, PRIMARY_CHANNEL_ID)
                .setContentTitle(("Monitoring the Charging level"))
                .setContentText(("Running"))
                .setContentIntent(contentPendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);
        mNotifyManager.notify(1, builder.build());
    }


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
                    ("for Scheduling");

            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }
}