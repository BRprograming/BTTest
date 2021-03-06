package br.com.bttest;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MenageDevice extends AppCompatActivity {

    private BluetoothAdapter myBluetooth = null;
    int appStatus = 0;
    ListView pariedList;
    ListView noPairedList;
    ArrayList arrayListParied;
    ArrayList arrayListNoPaired;
    ArrayAdapter arrayAdapterParied;
    ArrayAdapter arrayAdaptertNoPaired;

    TextView titleParied;
    TextView titleNoParied;
    private Set<BluetoothDevice> pairedDevicesSet;
    Button buttonScan;

    String chosenDeviceAdress;
    Boolean isPairing=false;
    Boolean waitingForBonding = false;

    ProgressDialog prog1;

    static Handler handlerD;
    Timer errorConnectTimer;
    Timer errorDisconnectTimer;

    IntentFilter filter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menage_device);
        titleParied = (TextView) findViewById(R.id.title_paired_devices);
        titleNoParied = (TextView) findViewById(R.id.title_noPaired);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        pariedList = (ListView) findViewById(R.id.paired_devices);
        pairedDevicesSet = MainActivity.pairedDevices;
        pairedDevicesList();


        noPairedList = (ListView) findViewById(R.id.noPairedList);

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.EXTRA_PAIRING_VARIANT);
        this.registerReceiver(mReceiver, filter);

        prog1 = new ProgressDialog(this);
        prog1.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Bundle bundle = getIntent().getExtras();
        appStatus = bundle.getInt("appStatus");

        buttonScan = (Button) findViewById(R.id.button_scan);

        refreshTextView();
        errorDisconnectTimer = new Timer();
        errorConnectTimer = new Timer();

        handlerD = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == Constants.DISCONNECT_END_MSG) {
                    if(prog1 != null){
                        errorConnectTimer.cancel();
                        titleParied.setText(R.string.failed_connect);
                        prog1.hide();
                    }
                }

                if(msg.what == Constants.CONNECT_END_MSG) {
                    if(prog1 != null){

                        errorConnectTimer.cancel();
                        titleParied.setText(R.string.connected);
                        prog1.hide();
                        finish();
                    }
                }

                if(msg.what == Constants.DISCONNECT_TIMEOUT_MSG) {

                    errorDisconnectTimer.cancel();
                    if(prog1 != null){
                        titleParied.setText("TIMEOUT DISCONNECT");
                        prog1.hide();
                    }
                }

                if(msg.what == Constants.CONNECT_TIMEOUT_MSG) {

                    errorConnectTimer.cancel();
                    if(prog1 != null){
                        titleParied.setText("TIMEOUT CONNECT");
                        prog1.hide();
                    }
                }
            }
        };

    }

    public void ScanDevices(View view) {

        if( (appStatus & Constants.STATE_CONNECT) == Constants.STATE_CONNECT ){
            titleParied.setText(R.string.already_connected);
            buttonScan.setText(R.string.button_disconnect);

            userDisconnectDevice();

        } else {
            findViewById(R.id.title_noPaired).setVisibility(View.VISIBLE);
            findViewById(R.id.noPairedList).setVisibility(View.VISIBLE);
            noPairedDevicesList();
            discoverDevices(view);
            isPairing =false;
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, 1.0f);
            pariedList.setLayoutParams(param);
        }

    }

    public void discoverDevices(final View view) {
        {
            if (myBluetooth.isDiscovering()) {
                myBluetooth.cancelDiscovery();
            }

            // Request discover from BluetoothAdapter
            myBluetooth.startDiscovery();
            titleNoParied.setText(R.string.search);

        }
    }

    private void pairedDevicesList() {

        arrayListParied = new ArrayList();

        if (pairedDevicesSet.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice bt : pairedDevicesSet) {
                arrayListParied.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_paried_find, Toast.LENGTH_LONG).show();
        }

        arrayAdapterParied = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListParied);
        pariedList.setAdapter(arrayAdapterParied);
        pariedList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }


    private void noPairedDevicesList() {
        arrayListNoPaired = new ArrayList();
        arrayAdaptertNoPaired = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListNoPaired);
        noPairedList.setAdapter(arrayAdaptertNoPaired);
        noPairedList.setOnItemClickListener(myListClickListener2); //Method called when the device from the list is clicked
    }

    // sprawdzanie przycisków na liscie urządzeń
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            chosenDeviceAdress = info.substring(info.length() - 17);

            if (myBluetooth.isDiscovering()) {
                myBluetooth.cancelDiscovery();
            }

            if ( (appStatus & Constants.STATE_CONNECT) == Constants.STATE_CONNECT ){
                userDisconnectDevice();
            }else {

                errorConnectTimer.schedule(new connectErrorTask(),
                        Constants.TIMEOUT_CONNECT, Constants.TIMEOUT_CONNECT);
                titleParied.setText(R.string.connect);
                prog1.setMessage(getString(R.string.connect));
                prog1.show();

                Message message = new Message();
                message.what = Constants.START_CONNECT_MSG;
                message.obj = chosenDeviceAdress;
                MainActivity.handler.sendMessage(message);
            }
            //finish();
        }
    };

    // sprawdzanie przycisków na liscie nie sparowanych
    private AdapterView.OnItemClickListener myListClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View

            if (myBluetooth.isDiscovering()) {
                myBluetooth.cancelDiscovery();
            }

            String info = ((TextView) v).getText().toString();
            if (info.contains(":")){
                chosenDeviceAdress = info.substring(info.length() - 17);

                isPairing = true;
                pairDevice(myBluetooth.getRemoteDevice(chosenDeviceAdress));

                //pairDevice2(myBluetooth.getRemoteDevice(chosenDeviceAdress));
            }
        }
    };

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // gdy znajdzemy urządzenie
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                Log.i(Constants.TAG, " ACTION_FOUND");
                // za pomocą Intecji pobieramy objekt BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // wypisywanie urządzeń
                Toast.makeText(context, "Znaleziono:"+ device.getName() + "\n" + device.getAddress(),
                        Toast.LENGTH_LONG).show();
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) // jesli nie ma na liscie to dodajemy
                {
                    arrayAdaptertNoPaired.add(device.getName() + "\n" + device.getAddress());
                }

                // dla > lolipop

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

                Log.i(Constants.TAG, " ACTION_DISCOVERY_FINISHED");
                if(titleNoParied.getVisibility() == View.VISIBLE){
                    titleNoParied.setText(R.string.select_pairing_device);
                }

                if(arrayAdaptertNoPaired != null){

                    if (arrayAdaptertNoPaired.getCount() == 0) {
                        String noDevices = getResources().getText(R.string.none_found).toString();
                        arrayAdaptertNoPaired.add(noDevices);
                    }
                }
            }

            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {

                if( isPairing )
                {
                    Log.i(Constants.TAG, " ACTION_ACL_CONNECTED isParing=true");
                }
                else {
                    Log.i(Constants.TAG, " ACTION_ACL_CONNECTED isParing=false");

                    errorConnectTimer.cancel();

                    appStatus |= Constants.STATE_CONNECT;
                    prog1.hide();
                    titleParied.setText(R.string.connected);

                    Message message = new Message();
                    message.what = Constants.CONNECT_END_MSG;
                    message.obj = 255;
                    MainActivity.handler.sendMessage(message);
                    isPairing =false;

                    finish();
                }
            }

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if( isPairing )
                {
                    isPairing =false;
                }else{
                    errorDisconnectTimer.cancel();
                    Log.i(Constants.TAG, " ACTION_ACL_DISCONNECTED isParing=false");
                    prog1.hide();
                    appStatus &= ~Constants.STATE_CONNECT;
                    refreshTextView();

                    Message message = new Message();
                    message.what = Constants.DISCONNECT_END_MSG;
                    message.obj = 255;
                    MainActivity.handler.sendMessage(message);

                }
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                // finish();
                if( isPairing  ){


                    Log.i(Constants.TAG, " ACTION_BOND_STATE_CHANGED isParing=true");
                    // za pomocą Intecji pobieramy objekt BluetoothDevice
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if(device.getBondState() == BluetoothDevice.BOND_BONDING){
                        waitingForBonding= true;

                    }


                    if (device.getBondState() == BluetoothDevice.BOND_BONDED) // jesli nie ma na liscie to dodajemy
                    {

                        Log.i(Constants.TAG, " ACTION_BOND_STATE_CHANGED BOND_BONDED");
                        arrayAdaptertNoPaired.remove(device.getName() + "\n" + device.getAddress());
                        arrayAdapterParied.clear();
                        arrayAdapterParied.add(device.getName() + "\n" + device.getAddress());
                        for (BluetoothDevice bt : pairedDevicesSet) {
                           arrayAdapterParied.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                        }

                    }else{

                        Log.i(Constants.TAG, " ACTION_BOND_STATE_CHANGED BOND_BONDED else isPairing=false");
                    }
                }
            }

            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {

                Log.i(Constants.TAG, " ACTION_PAIRING_REQUEST isParing=true");
                isPairing =true;
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT);
            }
        }

    };

    //tells handler to send a message
    class connectErrorTask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = Constants.CONNECT_TIMEOUT_MSG;
            message.obj = 255;
            handlerD.sendMessage(message);
        }
    };

    //tells handler to send a message
    class disconnectErrorTask extends TimerTask {

        @Override
        public void run() {
            Message message = new Message();
            message.what = Constants.DISCONNECT_TIMEOUT_MSG;
            message.obj = 255;
            handlerD.sendMessage(message);
        }
    };

    private void pairDevice(BluetoothDevice device) {

        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void userDisconnectDevice(){

        errorDisconnectTimer.schedule(new disconnectErrorTask(),
                Constants.TIMEOUT_DISCONNECT, Constants.TIMEOUT_DISCONNECT);

        titleParied.setText(R.string.disconnecting);
        prog1.setMessage(getString(R.string.disconnecting));
        prog1.show();

        Message message = new Message();
        message.what = Constants.START_USER_DISCONNECT_MSG;
        message.obj = chosenDeviceAdress;
        MainActivity.handler.sendMessage(message);
    }

    private void refreshTextView() {

        if( (appStatus & Constants.STATE_CONNECT) == Constants.STATE_CONNECT ){
            titleParied.setText(R.string.already_connected);
            buttonScan.setText(R.string.button_disconnect);
        } else {
            titleParied.setText(R.string.select_device);
            buttonScan.setText(R.string.button_serch);
        }
    }


    public Boolean pairDevice2(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
            //Log.i("Log", "This is: "+bool.booleanValue());
            //Log.i("Log", "devicesss: "+bdDevice.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bool.booleanValue();
    }

    public void onBackPressed() {

        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }

        finish();
    }

    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }

}






