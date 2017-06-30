package br.com.bttest;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView floortxt;
    boolean anim = true;


    //od radka
    LiftBT liftBT = null;
    public int appStatus=0;
    boolean btIsConnected=false;
    static Handler handler;
    static Set<BluetoothDevice> pairedDevices;
    String adress;

    EditText valueSet;
    EditText adressSet;


    TextView statusText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // vizualization images

        floortxt = (TextView) findViewById(R.id.textViewFloor);
        final ImageView image_floor = (ImageView) findViewById(R.id.imageViewFloorFrame);
        image_floor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Wybór piętra")
                        .setItems(R.array.Ilość_przystanków_01, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                String floor = getResources().getStringArray(R.array.Ilość_przystanków_01)[which];
                                Toast.makeText(MainActivity.this, "Wybrano piętro " + floor, Toast.LENGTH_SHORT).show();
                                floortxt.setText(floor);
                            }
                        });
                AlertDialog mDialog = builder.create();
                mDialog.show();
                //trzeba wyciagnać parametr floor_int (return floor_int;???)
            }
        });


        final ImageView imageViewWayDown = (ImageView) findViewById(R.id.imageViewWayDown);
        imageViewWayDown.setColorFilter(Color.parseColor("#3f51b5"));
        imageViewWayDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (anim == true) {
                    imageViewWayDown.setBackgroundResource(R.drawable.way_down_anim);
                    imageViewWayDown.setImageResource(0);
                    AnimationDrawable animationDrawableWayDown = (AnimationDrawable) imageViewWayDown.getBackground();
                    animationDrawableWayDown.start();
                    anim = false;
                } else {
                    imageViewWayDown.setImageResource(R.drawable.way_down_0);
                    imageViewWayDown.setBackgroundResource(0);
                    anim = true;
                }
            }
        });



        ImageView errorImage = (ImageView) findViewById(R.id.imageViewErrorFrame);
        errorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent error = new Intent(MainActivity.this, ErrorActivity.class);
                startActivity(error);
            }
        });

        ImageView imageViewWayUp = (ImageView) findViewById(R.id.imageViewWayUp);
        imageViewWayUp.setColorFilter(Color.parseColor("#3f51b5"));

        ImageView imageViewSpeed = (ImageView) findViewById(R.id.imageViewSpeed);
        imageViewSpeed.setColorFilter(Color.parseColor("#3f51b5"));

        ImageView imageViewDoor = (ImageView) findViewById(R.id.imageViewDoor);
        imageViewDoor.setColorFilter(Color.parseColor("#3f51b5"));





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //nav header status
        View header = navigationView.getHeaderView(0);
        statusText = (TextView) header.findViewById(R.id.textViewStatus);

        //od radka
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        //txt1 = (TextView) findViewById(R.id.bt_message) ;

        //statusText = (TextView) findViewById(R.id.loginStatusText);
        setConnectionStatus(getString(R.string.not_connected));
        //huj12312313




        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if(msg.what == Constants.SEND_VAL_MSG) {

                    int[] adressAndValue = new  int[2];

                    adressAndValue = (int[]) msg.obj;
                    liftBT.btSend(Constants.WRITE_FUNC, adressAndValue[0],adressAndValue[1]);
                }

                if(msg.what == Constants.REQUEST_READ_VAL_MSG) {

                    int[] adressAndValue = new  int[2];

                    adressAndValue = (int[]) msg.obj;
                    liftBT.btSend(Constants.READ_FUNC, adressAndValue[0],adressAndValue[1]);
                }

                if(msg.what == Constants.REQUEST_SET_STATUS) {

                    int[] adressAndValue = new  int[2];

                    adressAndValue = (int[]) msg.obj;
                    switch (adressAndValue[1]) {

                        case 1:
                                SetFloorVisu(adressAndValue[2]);
                            break;

                    }
                }

                if(msg.what == Constants.START_CONNECT_MSG) {

                    appStatus |= Constants.STATE_CONNECTING;
                    adress = msg.obj.toString();
                    //if (btIsConnected) {
                    //    liftBT.DisconnectDevice();
                    //}
                    liftBT.btConnect(adress);
                }



                if(msg.what == Constants.SET_DISCONNECT_MSG) {
                    appStatus &= ~(Constants.STATE_CONNECTING | Constants.STATE_CONNECT);
                    appStatus |= Constants.STATE_DISCONNECT;

                    Toast.makeText(MainActivity.this, R.string.discconnect, Toast.LENGTH_LONG).show();
                    liftBT.setDisconnect();
                    setConnectionStatus(getString(R.string.discconnect));

                    btIsConnected=false;
                    appStatus &=  ~Constants.STATE_CONNECT;
                    appStatus |=  Constants.STATE_DISCONNECT;
                }




                if(msg.what == Constants.CONNECT_END_MSG) {

                    appStatus &= ~(Constants.STATE_CONNECTING | Constants.STATE_DISCONNECT);
                    appStatus |= Constants.STATE_CONNECT;

                    Toast.makeText(MainActivity.this, R.string.connected, Toast.LENGTH_LONG).show();
                    liftBT.startReceiveBT();

                    liftBT.callLoginDialog();
                    setConnectionStatus(getString(R.string.connected));
                    btIsConnected = true;

                }

                if(msg.what == Constants.LOGIN_PASSWORD_DATA_MSG) {

                    int frameData = (int) msg.obj;

                    liftBT.errorTimer1.cancel();


                    switch (frameData){
                        case 0:
                            Toast.makeText(MainActivity.this, R.string.login_ok, Toast.LENGTH_LONG).show();
                            liftBT.hideProgressDialogLogin();
                            // liftBT.hideLoginDialog();
                            setConnectionStatus(getString(R.string.logged));
                            appStatus &= ~Constants.STATE_LOGIN_ERROR;
                            appStatus |= Constants.STATE_LOGIN;
                            break;

                        case 1:
                            Toast.makeText(MainActivity.this, R.string.login_error_name, Toast.LENGTH_LONG).show();
                            liftBT.hideProgressDialogLogin();
                            setConnectionStatus(getString(R.string.not_logged));
                            appStatus |= Constants.STATE_LOGIN_ERROR;
                            liftBT.callLoginDialog();
                            break;

                        case 2:
                            Toast.makeText(MainActivity.this, R.string.login_error_pass, Toast.LENGTH_LONG).show();
                            liftBT.hideProgressDialogLogin();
                            setConnectionStatus(getString(R.string.not_logged));
                            appStatus |= Constants.STATE_LOGIN_ERROR;
                            liftBT.callLoginDialog();
                            break;

                        case 3:
                            Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                            liftBT.hideProgressDialogLogin();
                            setConnectionStatus(getString(R.string.not_logged));
                            appStatus |= Constants.STATE_LOGIN_ERROR;
                            liftBT.callLoginDialog();
                            break;

                        default:
                            Toast.makeText(MainActivity.this, R.string.login_error, Toast.LENGTH_LONG).show();
                            liftBT.hideProgressDialogLogin();
                            appStatus |= Constants.STATE_LOGIN_ERROR;
                            setConnectionStatus("HUJ WIE CO TO ZA ERROR");
                            liftBT.callLoginDialog();
                            break;
                    }
                }

                if(msg.what == Constants.PERMISSIONS_ERROR_MSG) {

                    Toast.makeText(MainActivity.this, R.string.not_permissions, Toast.LENGTH_LONG).show();
                    setConnectionStatus(getString(R.string.not_logged));
                }

                if(msg.what == Constants.LOGIN_TIMEOUT_MSG) {

                    liftBT.hideProgressDialogLogin();
                    Toast.makeText(MainActivity.this, "LOGIN TIMEOUT", Toast.LENGTH_LONG).show();
                    liftBT.errorTimer1.cancel();
                    appStatus |= Constants.STATE_LOGIN_ERROR;
                }



            }
        };

        // przekazuje do klasy LiftBT aktualny view
        liftBT = new LiftBT(findViewById(android.R.id.content));
        //bTConnectTherad = new BTConnectTherad(findViewById(android.R.id.content));

        if( !liftBT.isEnabled())
        {
            // if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            //     Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //     startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
            // }

            liftBT.showBtOn();
        }


        // jesli wersja androida większa od loipop to pytaj o uprawnieneia do lokalizacji
        // nie wykorzystujemy w aplikacji lokalizacji ale są potrzebne do znajdywania urządzeń bluetooth dziwne
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mReceiver, filter);
    }


    BroadcastReceiver mReceiver = new BroadcastReceiver() {


        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                Toast.makeText(MainActivity.this, R.string.discconnect, Toast.LENGTH_LONG).show();
                liftBT.setDisconnect();
                setConnectionStatus(getString(R.string.discconnect));
                appStatus &= ~Constants.STATE_CONNECT;
                appStatus |= Constants.STATE_DISCONNECT;

                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
            }
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
           // Intent settings_intent = new Intent(this, SettingsActivity.class);
          //  startActivity(settings_intent);
        }
        if (id == R.id.action_BTsettings) {
           // pairedDevices = liftBT.getBondedBtDevice();
           // Intent intetn = new Intent(getApplicationContext(), DeviceListActivity.class);
           // startActivity(intetn);

            pairedDevices = liftBT.getBondedBtDevice();
            Intent intent = new Intent(getApplicationContext(), MenageDevice.class);
            intent.putExtra("appStatus", appStatus);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            // Handle navigation view item clicks here.
            int id = item.getItemId();

            if (id == R.id.Dane_sterowania) {
                Intent dane_sterowania_intent = new Intent(this, ParametersActivity.class);
                startActivityForResult(dane_sterowania_intent, 0);

            } else if (id == R.id.Licznik_czasu) {

            } else if (id == R.id.Ustawienia) {
                Intent ustawienia = new Intent(this, SettingsActivity.class);
                startActivityForResult(ustawienia, 0);


            } else if (id == R.id.Rejestr_usterek) {
                Intent intent_error = new Intent(this, ErrorActivity.class);
                intent_error.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//wraca do aktywnosci a nie uruchamia jej na nowo
                startActivity(intent_error);

            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    //od radka
    public void Connect (View view){

        pairedDevices = liftBT.getBondedBtDevice();
        Intent intetn = new Intent(getApplicationContext(), DeviceListActivity.class);
        startActivity(intetn);
    }


    public void Check (View view){



    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted!
                }
                return;
            }
        }
    }
    // funkcja ustawiajaąca visualizacje piętra
    void SetFloorVisu (Integer floor)
    {
        floortxt.setText(floor.toString());
    }

    void setConnectionStatus (String status)
    {
        statusText.setText(status);

    }

}
