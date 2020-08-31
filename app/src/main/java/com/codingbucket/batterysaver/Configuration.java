package com.codingbucket.batterysaver;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codingbucket.batterysaver.BT.BluetoothHandler;

public class Configuration extends AppCompatActivity {
    Button btnOn, btnTest,bttnOff,btnfinish,btnCon;
    public static String batterysaver_saved_value_off="batterysaver_saved_value_off";
    public static String batterysaver_saved_value_on="batterysaver_saved_value_on";
    private BluetoothHandler bluetoothHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        bluetoothHandler=new BluetoothHandler("98:D3:51:F5:E2:58",this);
        btnOn = (Button) findViewById(R.id.button7);
        btnTest = (Button) findViewById(R.id.button5);
        bttnOff = (Button) findViewById(R.id.button6);
        btnfinish=(Button) findViewById(R.id.button2);
        btnCon=(Button) findViewById(R.id.button3);
        NumberPicker np = findViewById(R.id.numberPicker);
        System.out.println("oncreate");
        np.setMinValue(0);
        np.setMaxValue(360);
        np.setValue(0);
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSave();      //method to save on
            }
        });
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTest();      //method to save on
            }
        });
        bttnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOff();      //method to save on
            }
        });
        btnfinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishFun();      //method to save on
            }
        });
        btnCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothHandler.connect();      //method to save on
            }
        });
        bluetoothHandler.connect();



    }

    private void finishFun() {
//        Intent i = new Intent(Configuration.this, MainActivity.class);
//        startActivity(i);
        finish();
    }

    private void onSave() {
        NumberPicker numberPicker=(NumberPicker) findViewById(R.id.numberPicker);
        System.out.println("Save BatterPercentage Called");
        msg("selected battery percentage "+numberPicker.getValue());
        Utils.saveSetting(Configuration.this,batterysaver_saved_value_on,String.valueOf(numberPicker.getValue()));
        msg("Value Saved "+numberPicker.getValue());
    }

    private void onTest() {
        String value = String.valueOf(((NumberPicker) findViewById(R.id.numberPicker)).getValue());
        msg("Testing value"+value);
        bluetoothHandler.sendCommand(value);
    }

    private void onOff() {
        NumberPicker numberPicker=(NumberPicker) findViewById(R.id.numberPicker);
        System.out.println("Save BatterPercentage Called");

        msg("selected battery percentage "+numberPicker.getValue());
        Utils.saveSetting(Configuration.this,batterysaver_saved_value_off,String.valueOf(numberPicker.getValue()));
        msg("Value Saved "+numberPicker.getValue());
    }
    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothHandler.disconnect();
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
}
