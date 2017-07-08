package br.com.bttest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;




/**
 * Created by Radek on 16.05.2017.
 */

public class LiftBT extends AppCompatActivity {

    private boolean btIsConnected=false;
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private OutputStream os = null;
    private BluetoothSocket btSocket;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothDevice myDevice = null;
    private ReceiveBT reciveBT = null;
    private View mianView;

    // login variables
    Dialog loginDialog;
    EditText editTextLoginName;
    EditText editTextLoginPassword;
    TextView textViewLoginTitle;
    TextView textViewPasswordTitle;
    Boolean loginDataOk =false;
    ProgressDialog progressDialogLogin;
    String tempLogin="";

    Timer timerErrorLogin;
    Boolean SaveLoginData = false;



    public LiftBT(View view ){
        //get the mobile bluetooth device
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        mianView = view;
    }

    private void enableBt(){
        myBluetooth.enable();
    }


    public boolean isEnabled (){
        return myBluetooth.isEnabled();
    }


    public void setDisconnect (){
        btIsConnected = false;
    }

    public void DisconnectDevice (){

        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }
        
        try {
            btSocket.close();
            btIsConnected = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean btIsConnected (){

        return btIsConnected;

    }

    public void btConnect( final String addressSet) {

        if (!isEnabled()){
            showDialogBTOn();
        }

        if (btIsConnected)
        {
            DisconnectDevice();
        }

        if( addressSet.length() > 0 )
        {
            ThreadConnect threadConnect = new ThreadConnect(addressSet);
            threadConnect.start();
        }

    }

    Set<BluetoothDevice> getBondedBtDevice(){
        return myBluetooth.getBondedDevices();
    }

    void startReceiveBT(){
        reciveBT = new ReceiveBT(btSocket);
        reciveBT.start();
    }

    void showDialogBTOn(){

        AlertDialog.Builder builder = new AlertDialog.Builder(mianView.getContext(),
                R.style.Theme_AppCompat_Light_Dialog);

        builder.setMessage(R.string.on_now);

        builder.setCancelable(true);

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(mianView.getContext(), R.string.on_bt_to_connect,
                        Toast.LENGTH_LONG).show();

            }
        });

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(mianView.getContext(),  R.string.run_bt,
                        Toast.LENGTH_LONG).show();
                enableBt();
            }
        });

        AlertDialog alert = builder.create();
        alert.setTitle(R.string.bt_is_off);
        alert.setIcon(R.drawable.bluetooth_alt);

        alert.show();

        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        //alert.getButton(AlertDialog.BUTTON_NEGATIVE).setGravity(Gravity.LEFT);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        //alert.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.LEFT);
    }

    public BluetoothAdapter getmyBluetooth() {
        return  BluetoothAdapter.getDefaultAdapter();
    }

    void btSend(int func, int parameterCode, int value) {

        if(btIsConnected)
        {
            byte[] bytes = new byte[4];
            int crc=0;

            if ( (func == Constants.WRITE_FUNC) || (func == Constants.READ_FUNC)
                    || (func == Constants.LOGOUT_FUNC) )
            {
                bytes[Constants.FUNC_POS] = (byte) (func & 0xFF);
                bytes[Constants.CODE_POS] = (byte) (parameterCode & 0xFF);
                bytes[Constants.VAL_POS] = (byte) (value & 0xFF);

                crc = calculateLRC(bytes,3);
                bytes[Constants.CRC_POS] = (byte) (crc & 0xFF);

                try {
                    os.write(bytes);
                    //socket.getOutputStream().write("Test bt11".toString().getBytes());
                } catch (IOException e) {
                }
            }
            else
            {
                for (int i=0; i<4; i++){
                    bytes[i] = 0;
                }

                try {
                    os.write(bytes);
                    //socket.getOutputStream().write("Test bt11".toString().getBytes());
                } catch (IOException ignored) {
                }
            }
        }
        else{
            Toast.makeText(mianView.getContext(), R.string.not_connected_lift,
                    Toast.LENGTH_LONG).show();
        }
    }

    void btSend(int func, String dataStr) {
        // jeśli jestem połączony
        if(btIsConnected)
        {
            byte[] bytes;
            int crc=0;

            int counterData = dataStr.length();
            // dodaje 5 bity bo 1 funkcja 2 jest pusta daje 255 i 3 ilosć bitów, i 4,5 bo razy 2 lrc
            byte[] newBytes = new byte[counterData+Constants.CRC_POS+2];
            bytes = dataStr.getBytes();

            if (func == Constants.LOGIN_PASSWORD_DATA_FUNC)
            {
                newBytes[Constants.FUNC_POS] = (byte) (func & 0xFF);
                newBytes[Constants.CODE_POS] = (byte) (255 & 0xFF);
                newBytes[Constants.VAL_POS] = (byte) ((counterData+1) & 0xFF);

                crc = calculateLRC(newBytes,3);
                newBytes[Constants.CRC_POS] = (byte) (crc & 0xFF);

                for (int i=0; i<counterData; i++)
                {
                    newBytes[Constants.CRC_POS+i+1] = bytes[i];
                }

                crc = calculateLRC(bytes,counterData);
                //bytes[Constants.CRC_POS] = (byte) (crc & 0xFF);

                newBytes[Constants.CRC_POS+counterData+1] = (byte) (crc & 0xFF);

                try {
                    os.write(newBytes);
                    //socket.getOutputStream().write("Test bt11".toString().getBytes());
                } catch (IOException ignored) {
                }
            }
            else
            {
                for (int i=0; i<4; i++){
                    bytes[i] = 0;
                }

                try {
                    os.write(bytes);
                    //socket.getOutputStream().write("Test bt11".toString().getBytes());
                } catch (IOException ignored) {
                }
            }
        }
        else{
            Toast.makeText(mianView.getContext(), R.string.not_connected_lift,
                    Toast.LENGTH_LONG).show();
        }

    }

    public int calculateLRC(byte[] bytes, int number )
    {
        byte LRC = 0;

        for (int i = 0; i < number; i++)
        {
            LRC += bytes[i];
        }
        LRC = (byte)((-LRC) & 0xFF);

        Integer testLRC = (LRC & 0xFF);
        //Toast.makeText(mianView.getContext(),"LRC:"+ testLRC.toString(), Toast.LENGTH_LONG).show();
        return LRC;
    }


    class ThreadConnect extends Thread {

        String addressSet;

        public ThreadConnect(String deviceAdress){
            addressSet = deviceAdress;
        }

        public void run() {

            if(!btIsConnected){
                try {
                    Log.i(Constants.TAG, "Start connecting ");
                    myDevice = myBluetooth.getRemoteDevice(addressSet);//connects to the device's address and checks if it's available
                    btSocket = myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    try {
                        btSocket.connect();//start connection
                    }
                    catch (Exception e) {
                        Log.e(Constants.TAG, "btSocket.connect failed: " + e);
                    }

                    if (btSocket.isConnected()) {
                        btIsConnected = true;
                        Message message = new Message();
                        message.what = Constants.CONNECT_END_MSG;
                        message.obj = 255;
                        MenageDevice.handlerD.sendMessage(message);
                    }
                    else {
                        Message message = new Message();
                        message.what = Constants.DISCONNECT_END_MSG;
                        message.obj = 255;
                        MenageDevice.handlerD.sendMessage(message);
                    }
                } catch (IOException ignored) {}

                try {
                    os = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    os = null;
                }
            }
            else{
                Log.i(Constants.TAG, "isConnected == true so go to else");
                try {
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(Constants.TAG, "Error btSocket.close ");
                }
            }
        }
    }

    public void startProgressDialogLogin(){
        progressDialogLogin = new ProgressDialog(mianView.getContext());
        progressDialogLogin.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialogLogin.setSecondaryProgress(ProgressDialog.STYLE_SPINNER);
        progressDialogLogin.setMessage( mianView.getContext().getString(R.string.login_in_progress) );
    }


    void callLoginDialog()
    {
        final Dialog loginDialog = new Dialog(mianView.getContext());
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle(R.string.log_in);
        loginDialog.setCancelable(true);
        startProgressDialogLogin();

        Button buttonLogin = (Button) loginDialog.findViewById(R.id.buttonLoginOk);
        final CheckBox checkBoxSaveLiftData = (CheckBox) loginDialog.findViewById(R.id.checkBoxSaveLiftData);

        editTextLoginName = (EditText) loginDialog.findViewById(R.id.loginEdit);
        editTextLoginPassword = (EditText) loginDialog.findViewById(R.id.passwordEdit);
        textViewLoginTitle = (TextView) loginDialog.findViewById(R.id.titleLogin);
        textViewPasswordTitle = (TextView) loginDialog.findViewById(R.id.titlePassword);


        final String addressDevice = "address1"; // dodaje przykladowy adres recznie do testow

        try {
            SQLiteOpenHelper loginDatabaseHelper = new LoginDB(mianView.getContext());
            SQLiteDatabase db = loginDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("LOGIN",
                    new String[] {"ADDRESS", "LOGIN", "PASSWORD"}, "ADDRESS = ?",
                    new String[] {addressDevice}, null, null, null);

            if (cursor.moveToFirst()) {
                String loginText = cursor.getString(1);
                String passwordText = cursor.getString(2);
                editTextLoginName.setText(loginText);
                editTextLoginPassword.setText(passwordText);
            }
            cursor.close();
            db.close();
        }
        catch (SQLiteException e) {
            Toast toast = Toast.makeText(mianView.getContext(), "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
            toast.show();
        }

        // ustaw nazwe jaka była wpisana wcześniej jeśli to kolejna próba logowania
        //editTextLoginName.setText(tempLogin);

        loginDialog.show();

        buttonLogin.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                String loginStr = editTextLoginName.getText().toString();
                String passwordStr = editTextLoginPassword.getText().toString();
                if (loginStr.length() <6 ){
                    textViewLoginTitle.setText("Nazwa jest za krótka");
                    textViewLoginTitle.setTextColor(Color.RED);
                    loginDataOk = false;
                }
                else {
                    loginDataOk = true;
                    textViewLoginTitle.setTextColor(Color.BLACK);
                    textViewLoginTitle.setText("Nazwa:");
                }

                if (passwordStr.length() <4 ){
                    textViewPasswordTitle.setText("Hasło jest za krótkie");
                    textViewPasswordTitle.setTextColor(Color.RED);
                    loginDataOk = false;
                }else{
                    textViewPasswordTitle.setTextColor(Color.BLACK);
                    textViewPasswordTitle.setText("Hasło:");
                }

                if(loginDataOk)
                {
                    tempLogin = loginStr;
                    String tempData = loginStr + ":" + passwordStr;
                    // send login data to uc
                    if (tempData.length()<32) {
                        btSend(Constants.LOGIN_PASSWORD_DATA_FUNC, tempData);
                        showProgressDialogLogin();
                        // Set timer ms error timeout
                        timerErrorLogin = new Timer();
                        timerErrorLogin.schedule(new timeOutLoginTask(), Constants.TIMEOUT_RECEIVE_LOGIN,
                                Constants.TIMEOUT_RECEIVE_LOGIN);
                        loginDialog.cancel();
                    }else{
                        // bad data
                    }
                    if (SaveLoginData) {
                        SQLiteOpenHelper loginDatabaseHelper = new LoginDB(mianView.getContext());
                        SQLiteDatabase db = loginDatabaseHelper.getWritableDatabase();
                        LoginDB.insertLoginData(db, addressDevice, loginStr, passwordStr);
                    }
                }

            }
        });

        checkBoxSaveLiftData.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                //TODO Save login and password to SQL Lite

                if(checkBoxSaveLiftData.isChecked()){
                    Toast.makeText(mianView.getContext(), R.string.next_time_login,
                            Toast.LENGTH_LONG).show();
                    SaveLoginData = true;
                } else {
                    SaveLoginData = false;
                }

            }
        });
    }

    public void hideProgressDialogLogin(){
        progressDialogLogin.dismiss();
    }

    public void showProgressDialogLogin(){
        progressDialogLogin.show();
    }

}

class timeOutLoginTask extends TimerTask {

    @Override
    public void run() {
        Message message = new Message();
        message.what = Constants.LOGIN_TIMEOUT_MSG;
        message.obj = 255;
        MainActivity.handler.sendMessage(message);
    }
}

class ReceiveBT extends Thread{

    private InputStream is = null;
    private Boolean dataRdy=false;
    //private static final int START_FRAME = ':', END_FRAME ='\n' , HEAD_POS=1, ADRESS_POS=4, VALUE_POS=6, LRC_POS =8;

    ReceiveBT(BluetoothSocket socket){

        try{
            is = socket.getInputStream();
        }catch (IOException ignored){

        }
    }

    public void run() {
        byte[] buffer = new byte[128];
        byte[] buff = new byte[128];
        int bytes;
        int[] frameData = new int[4];
        int bytesSum = 0;

        while(true){
            try{
                // prubuje odczytać jakieś dane z  bluetooth
                bytes = is.read(buffer);

                // jeśli odczytałem jakieś dane
                if( bytes>0 ){

                    // przepisuje kolejne bajty aby później przekonwertować tylko tyle co odebrałem
                    for( int i=0; i<bytes; i++ )
                        buff[i+bytesSum]=buffer[i];

                    bytesSum += bytes;

                    // Kiedy testowałem program to okazało sie, że czasem program zapisywał mi np połowe ramki. Albo tylko 1 znak.
                    // Przy połączeniu z PC-telefon nie było tego problemu ale z uc tak.
                    // Pewnie jest tak dlatego że procek wysyła mi znak po znaku w przerwaniu i po wysłaniu jednego jest jakiś odstęp czasu a
                    // dczytywanie  jest umieszczone w while w niezależnym wątku pewnie śmiga to na tyle szybko że przy komunikacji uc
                    // odbiera 1 znak a następnie wchodzi w IF. Dlatego sumuje odebrane znaki aż do odebrania 4 bajtó tak aby mieć całą ramkę.

                    // mam całą ramkę
                    if( bytesSum == 4 ) {
                        // zerujemy licznik odebranych danych
                        bytesSum = 0;

                        frameData[Constants.FUNC_POS] =  buff[Constants.FUNC_POS] & 0xFF;
                        frameData[Constants.CODE_POS] =  buff[Constants.CODE_POS] & 0xFF;
                        frameData[Constants.VAL_POS] = buff[Constants.VAL_POS] & 0xFF;
                        frameData[Constants.CRC_POS] = buff[Constants.CRC_POS] & 0xFF;

                        int tmpCRC = (buff[Constants.CRC_POS] & 0xFF);
                        int tmpCalculateCRC = ( calculateLRC(buff,3) & 0xFF);

                        if( tmpCRC != tmpCalculateCRC ){
                            dataRdy = false;
                            bytesSum=0;
                        }
                        else{
                            dataRdy = true;
                        }
                    }

                    if(dataRdy)
                    {
                        dataRdy =false;
                        Message message = new Message();

                        switch (frameData[Constants.FUNC_POS]){

                            case Constants.LOGIN_PASSWORD_DATA_FUNC:
                                message.what = Constants.LOGIN_PASSWORD_DATA_MSG;
                                message.obj = frameData[Constants.CODE_POS];
                                MainActivity.handler.sendMessage(message);
                                break;

                            case Constants.WRITE_FUNC:
                                message.what = Constants.WRITE_FUNC_MSG;
                                message.obj = frameData;
                                MainActivity.handler.sendMessage(message);
                                break;

                            case Constants.READ_FUNC:
                                message.what = Constants.READ_FUNC_MSG;
                                message.obj = frameData;
                                ParametersActivity.parametrsHandler.sendMessage(message);
                                break;

                            case Constants.PERMISSIONS_ERROR_FUNC:
                                message.what = Constants.PERMISSIONS_ERROR_MSG;
                                message.obj = frameData;
                                MainActivity.handler.sendMessage(message);
                                break;

                            case Constants.SET_STATUS:
                                message.what = Constants.REQUEST_SET_STATUS;
                                message.obj = frameData;
                                MainActivity.handler.sendMessage(message);
                                break;
                            default:

                                break;
                        }
                    }

                    if( bytesSum > 4 )
                    {
                        bytesSum=0;
                    }

                    // Może się też zadżyć że gdzieś zgubi się znak CRLF albo zostanie źle odczytany mało prawdopodobne ale jednak
                    // dlatego określam że jak otrzymam powyżej 64 znaków i nie będą zakończone CRLF to olewam je i zeruje licznik. Jak dostane
                    // np opłowę ramki w dlaszej częsci transmisji a starsza połowa była w tych danych bo na początku były jakieś śmieci
                    // to i tak kij z tym i tak jej nie odczytam bo poprawnosć sprawdzam wyżej.
                }

            }catch(IOException e){
                break;
            }
        }
    }

    private int calculateLRC(byte[] bytes, int number )
    {
        byte LRC = 0;

        for (int i = 0; i < number; i++)
        {
            LRC += bytes[i];
        }
        LRC = (byte)((-LRC) & 0xFF);

        return LRC;
    }


}


enum Parametr {
    NUMBER_OF_STOPS(1), STOP_PARKINGP(2), FIRE_STATION(3), DRIVER_TYPE(4), DOOR_CONTROL_FUNC(5),
    MOTOR_THERMISTOR_CONTROL(15), LIFT_GROUP_NUMBER(16), DOOR_SUPERVISION_FUNC(20),
    EMERGENCY_EXIT_FUNC(22), CONTROL_FUNC(23),SPEED_CONTROL(24), SPEED_CONTROL_RUN(25),
    SIGNALING_EO_EU(26), MAIN_PAGE_STOPS_1_8(27), HOME_PAGE_STOPS_9_16(28), END_OF_PAGE_STOPS_1_8(29),
    END_OF_PAGE_STOPS_9_16(30), SPECIAL_FUNC(31), VOICE_MESSAGES(32), TYPES_CONNECTORS_CABIN(33),
    TYPES_CONNECTORS_SUPERVISION(34);

    private int adress;

    Parametr(int adress) {
        this.adress = adress;

    }

    public int getAdress() {
        return adress;
    }

}