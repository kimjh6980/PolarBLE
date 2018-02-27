package jinhyuk_kim.chosun.polarble;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.StringTokenizer;

public class MainActivity extends FragmentActivity {

    private SharedPreferences prefs;

    PolarBleService mPolarBleService;
    // String mpolarBleDeviceAddress="00:22:D0:A4:96:72";
      String mpolarBleDeviceAddress="00:22:D0:A4:9D:83";  // 우리꺼
    int batteryLevel=0;

    //------------------------------
    String mDefaultDeviceAddress;

    TextView heartratevalue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w(this.getClass().getName(), "onCreate()");

        prefs = getSharedPreferences(HConstants.DEVICE_CONFIG, Context.MODE_MULTI_PROCESS);
        mDefaultDeviceAddress = prefs.getString(HConstants.CONFIG_DEFAULT_DEVICE_ADDRESS, null);
        heartratevalue = (TextView)findViewById(R.id.heartratevalue);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(this.getClass().getName(), "onDestroy()");

        deactivatePolar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activatePolar();
    }


    protected void activatePolar() {
        Log.w(this.getClass().getName(), "** activatePolar()");

        Intent gattactivatePolarServiceIntent = new Intent(this, PolarBleService.class);
        bindService(gattactivatePolarServiceIntent, mPolarBleServiceConnection, BIND_AUTO_CREATE);
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        Log.w(this.getClass().getName(), "deactivatePolar()");
        try{
            if(mPolarBleService!=null){
                Log.w(this.getClass().getName(), "**** unbindService()");
                unbindService(mPolarBleServiceConnection);
                Log.w(this.getClass().getName(), "bindService()");
            }
        }catch(Exception e){
            Log.e(this.getClass().getName(), e.toString());
        }

        try{
            unregisterReceiver(mPolarBleUpdateReceiver);
        }catch(Exception e){
            Log.e(this.getClass().getName(), e.toString());
        }
    }

    private final BroadcastReceiver mPolarBleUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            //Log.w(TAG, "####BroadcastReceiver Polar BLE Service ");

            final String action = intent.getAction();
            if (PolarBleService.ACTION_GATT_CONNECTED.equals(action)) {
            } else if (PolarBleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.w(this.getClass().getName(), "mPolarBleUpdateReceiver received ACTION_GATT_DISCONNECTED");
            } else if (PolarBleService.ACTION_HR_DATA_AVAILABLE.equals(action)) {

                //heartRate+";"+pnnPercentage+";"+pnnCount+";"+rrThreshold+";"+bioHarnessSessionData.totalNN
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int hr = Integer.parseInt(tokens.nextToken());
                int prrPercenteage = Integer.parseInt(tokens.nextToken());
                int prrCount = Integer.parseInt(tokens.nextToken());
                int rrThreshold = Integer.parseInt(tokens.nextToken());	//50%, 30%, etc.
                int rrTotal = Integer.parseInt(tokens.nextToken());
                int rrValue = Integer.parseInt(tokens.nextToken());
                long sid = Long.parseLong(tokens.nextToken());

                Log.e(this.getClass().getName(), "Heart rate is " + hr);
                heartratevalue.setText(Integer.toString(hr));

            }else if (PolarBleService.ACTION_BATTERY_DATA_AVAILABLE.equals(action)) {
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                batteryLevel = Integer.parseInt(data);
            }else if (PolarBleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                String data = intent.getStringExtra(PolarBleService.EXTRA_DATA);
                //String data = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                StringTokenizer tokens = new StringTokenizer(data, ";");
                int totalNN = Integer.parseInt(tokens.nextToken());
                long lSessionId = Long.parseLong(tokens.nextToken());


            }
        }
    };

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PolarBleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(PolarBleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(PolarBleService.ACTION_HR_DATA_AVAILABLE);
        intentFilter.addAction(PolarBleService.ACTION_BATTERY_DATA_AVAILABLE);
        return intentFilter;
    }

    private final ServiceConnection mPolarBleServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mPolarBleService = ((PolarBleService.LocalBinder) service).getService();
            if (!mPolarBleService.initialize()) {
                Log.e(this.getClass().getName(), "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            //mPolarBleService.connect(app.polarBleDeviceAddress, false);
            mPolarBleService.connect(mpolarBleDeviceAddress, false);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            mPolarBleService = null;
        }
    };

}

