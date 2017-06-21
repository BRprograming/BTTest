package br.com.bttest;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends AppCompatActivity {

    ListView sparowaneList;
    private Set<BluetoothDevice> pairedDevicesSet;
    static String adress ="";
    private BluetoothAdapter myBluetooth = null;
    ArrayList list;
    Handler h1 =MainActivity.handler;
    ArrayAdapter adapter;

    TextView deviceListText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        sparowaneList = (ListView) findViewById(R.id.sparowaneListView);
        pairedDevicesSet = MainActivity.pairedDevices;
        pairedDevicesList();

        deviceListText = (TextView) findViewById(R.id.deviceListText);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

    }

    public void ScanDevice (View view){

        discoverDevices(view);
    }

    private void pairedDevicesList()
    {

        list = new ArrayList();

        if (pairedDevicesSet.size()>0)
        {
            for(BluetoothDevice bt : pairedDevicesSet)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        sparowaneList.setAdapter(adapter);
        sparowaneList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            adress = info.substring(info.length() - 17);

            // Tworzymy nową wiadomość
            Message wiadomosc = new Message();

            wiadomosc.what = 1;

            // Dodajemy treść, używamy jednego z dostępnych pól w Message
            wiadomosc.obj = adress;

            // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
            h1.sendMessage(wiadomosc);

            finish();


        }
    };

    public void discoverDevices (final View view) {
        {
            if (myBluetooth.isDiscovering()) {
                myBluetooth.cancelDiscovery();
            }

            // Request discover from BluetoothAdapter
            myBluetooth.startDiscovery();
            deviceListText.setText("Skanowanie...");

        }
    }

    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // gdy znajdzemy urządzenie
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // za pomocą Intecji pobieramy objekt BluetoothDevice
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // wypisywanie urządzeń
                Toast.makeText(context, "znalezino:" + device.getName() + "\n" + device.getAddress(), Toast.LENGTH_LONG).show();
                if(device.getBondState() != BluetoothDevice.BOND_BONDED) // jesli nie ma na liscie to dodajemy
                {

                    //list.add(device.getName() + "\n" + device.getAddress()); //Get the device's name and the address

                    adapter.add(device.getName() + "\n" + device.getAddress());
                    // final ArrayAdapter adapter = new ArrayAdapter( view.getContext() ,android.R.layout.simple_list_item_1, list);
                    // sparowaneList.setAdapter(adapter);
                    // sparowaneList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked

                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                deviceListText.setText("Wybierz urządzenie");
                if (adapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    adapter.add(noDevices);
                }
            }
        }

    };

}
