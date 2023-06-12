package com.codingbucket.batterysaver.BT;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.codingbucket.batterysaver.Configuration;
import com.codingbucket.batterysaver.Utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class BluetoothHandler {
    private static boolean ConnectionGoingOn = true;
    BluetoothAdapter myBluetooth = null;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address=null;
    private static BluetoothSocket btSocket = null;
    private static boolean isBtConnected = false;
    private static final int JOB_ID = 0;
    private Context context;

    public BluetoothHandler(String address,Context context){
        this.address=address;
        this.context=context;
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected


        @Override
        protected void onPreExecute()
        {

            System.out.println("Preconncetion called");
            //            progress = ProgressDialog.show(DeviceHandlerImpl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                System.out.println("doInBackground called");
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (Exception e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            System.out.println("onPostExecute called");

            if (!ConnectSuccess)
            {
                System.out.println("Connection Failed. Is it a SPP Bluetooth? Try again");
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
            }
            else
            {
                System.out.println("Connected");
                msg("Connected.");
                isBtConnected = true;
            }
            setConnectionGoingOn(false);
//            progress.dismiss();
        }


    }
    public static boolean isConnectionGoingOn() {
        return ConnectionGoingOn;
    }

    public static void setConnectionGoingOn(boolean connectionGoingOn) {
        ConnectionGoingOn = connectionGoingOn;
    }
    public void shutDownCharging()
    {

                System.out.println("Going to shutdown Charging");
                if(btSocket==null) {
                    connectAndWait();
                }
                if (btSocket!=null)
                {
                    System.out.println("Sending close signal");
                    try
                    {
                        btSocket.getOutputStream().write(Utils.getSetting(context, Configuration.batterysaver_saved_value_off).toString().getBytes()); //shutdown signal to the servo
                        System.out.println("Closing");
                        disconnect();
                    }
                    catch (IOException e)
                    {
                        msg("Error "+e.getMessage());
                    }
                }

    }

    public void disconnect() {
        if(btSocket!=null){
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            btSocket=null;
        }
    }

    public void msg(String error) {
        try {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
        }catch (Exception ignored){

        }
    }

    public void connectAndWait() {
        //send a commang to connect and waits till the action is done
        setConnectionGoingOn(true);
        System.out.println("Going to connect bluetooth");
        try
        {
            System.out.println("connectAndWait called");
            if (btSocket == null || !isBtConnected)
            {
                System.out.println(btSocket+" "+isBtConnected+" "+address);
                try{
                   // btSocket.close();
                }catch (Exception e){

                }
                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                btSocket.connect();//start connection

                System.out.println("connectAndWait done");

                setConnectionGoingOn(false);
            }
        }
        catch (Throwable e)
        {
            System.out.println("connectAndWait Exception "+e.getMessage());
            e.printStackTrace();

//            connectAndWait();
            setConnectionGoingOn(false);
        }
        while (isConnectionGoingOn()){
            try {
                System.out.println("Waiting for connection");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Waiting for connection Done");
    }
    public void sendCommand(String commnd){
        if (btSocket!=null)
        {
            System.out.println("Sending "+commnd);
            try
            {
                btSocket.getOutputStream().write(commnd.toString().getBytes()); //shutdown signal to the servo
                System.out.println("sent");
//                disconnect();
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void connect() {
        System.out.println("Connnecting "+address);
        new ConnectBT().execute(); //Call the class to connect
    }
}
