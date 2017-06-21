package br.com.bttest;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

    // private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btSocket;
    private BluetoothAdapter myBluetooth = null;
    private BluetoothDevice mmDevice = null;
    private ReceiveBT reciveBT = null;
    private View mianView;

    // zmeinne do logowania

    Dialog loginDialog;

    EditText login_text;
    EditText password_text;
    TextView login_title;
    TextView password_title;
    Boolean loginDataOk =false;
    ProgressDialog progressDialogLogin;
    String tempLogin="";

    Timer errorTimer1;



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
        Toast.makeText(mianView.getContext(),R.string.discconnect, Toast.LENGTH_LONG).show();
        btIsConnected = false;

    }

    public void DisconnectDevice (){

        try {
            btSocket.close();
            btIsConnected = false;

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (myBluetooth.isDiscovering()) {
            myBluetooth.cancelDiscovery();
        }
    }

    public boolean btIsConnected (){

        return btIsConnected;

    }

    public void btConnect( final String adressSet) {

        if (!isEnabled()){

            showBtOn();
        }

        if (btIsConnected)
        {
            // try {
            //btSocket.close();
            // btIsConnected = false;

            // } catch (IOException e) {
            //     e.printStackTrace();
            //  }
        }

        if( ( adressSet.length() > 0 )  ) //&& (btIsConnected == false)
        {
            ThreadConnect threadConnect = new ThreadConnect(adressSet);
            threadConnect.start();
        }

    }
    Set<BluetoothDevice> getBondedBtDevice(){

        return myBluetooth.getBondedDevices();
    }


    void startReceiveBT(){

        reciveBT = new ReceiveBT(btSocket, MainActivity.handler);
        reciveBT.start();

    }


    void showBtOn(){

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
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setGravity(Gravity.LEFT);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.RIGHT);
    }

    public BluetoothAdapter getmyBluetooth() {
        return  BluetoothAdapter.getDefaultAdapter();
    }

    // wysyłanie wiadomości
    void btSend(int func, int parametrCode, int value) {

        // jeśli jestem połączony
        if(btIsConnected)
        {
            byte[] bytes = new byte[4];
            int crc=0;

            if ( (func == Constants.WRITE_FUNC) || (func == Constants.READ_FUNC)
                    || (func == Constants.LOGOUT_FUNC) )
            {
                bytes[Constants.FUNC_POS] = (byte) (func & 0xFF);
                bytes[Constants.CODE_POS] = (byte) (parametrCode & 0xFF);
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
            byte[] newbytes = new byte[counterData+Constants.CRC_POS+2];
            bytes = dataStr.getBytes();

            if (func == Constants.LOGIN_PASSWORD_DATA_FUNC)
            {
                newbytes[Constants.FUNC_POS] = (byte) (func & 0xFF);
                newbytes[Constants.CODE_POS] = (byte) (255 & 0xFF);
                newbytes[Constants.VAL_POS] = (byte) ((counterData+1) & 0xFF);

                crc = calculateLRC(newbytes,3);
                newbytes[Constants.CRC_POS] = (byte) (crc & 0xFF);

                for (int i=0; i<counterData; i++)
                {
                    newbytes[Constants.CRC_POS+i+1] = bytes[i];
                }

                crc = calculateLRC(bytes,counterData);
                //bytes[Constants.CRC_POS] = (byte) (crc & 0xFF);

                newbytes[Constants.CRC_POS+counterData+1] = (byte) (crc & 0xFF);

                try {
                    os.write(newbytes);
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

            //text2.setText("Adres:"+adressSet);

            if(btIsConnected == false){
                try {
                    Log.i(Constants.TAG, "Start connecting ");
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    mmDevice = myBluetooth.getRemoteDevice(addressSet);//connects to the device's address and checks if it's available
                    btSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

                    try {
                        btSocket.connect();//start connection
                    }
                    catch (Exception e) {
                        // no op
                        Log.e(Constants.TAG, "btSocket.connect failed: " + e);
                    }


                    if (btSocket.isConnected()) {
                        btIsConnected = true;
                        // BLUETOOTH_STATUS = BLUETOOTH_CONNECTED;

                        // Tworzymy nową wiadomość
                        Message wiadomosc = new Message();

                        wiadomosc.what = Constants.CONNECT_END_MSG;

                        // Dodajemy treść, używamy jednego z dostępnych pól w Message
                        wiadomosc.obj = 255;

                        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
                        //MainActivity.handler.sendMessage(wiadomosc);
                        MenageDevice.handlerD.sendMessage(wiadomosc);

                    }
                    else {

                        // Tworzymy nową wiadomość
                        Message wiadomosc = new Message();

                        wiadomosc.what = Constants.SET_DISCONNECT_MSG;

                        // Dodajemy treść, używamy jednego z dostępnych pól w Message
                        wiadomosc.obj = 255;

                        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
                        //MainActivity.handler.sendMessage(wiadomosc);
                        MenageDevice.handlerD.sendMessage(wiadomosc);
                    }
                } catch (IOException ignored) {}


                try {
                    //is = btSocket.getInputStream();
                    os = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                    os = null;
                    // is = null;
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
        progressDialogLogin.setMessage("Trwa Logowanie...");
        //progressDialogLogin.show();
    }


    void callLoginDialog()
    {


        final Dialog loginDialog = new Dialog(mianView.getContext());
        loginDialog.setContentView(R.layout.login_dialog);
        loginDialog.setTitle("Logowanie");
        loginDialog.setCancelable(true);
        loginDialog.show();
        startProgressDialogLogin();

        Button login = (Button) loginDialog.findViewById(R.id.okBt);

        login_text = (EditText) loginDialog.findViewById(R.id.loginEdit);
        password_text = (EditText) loginDialog.findViewById(R.id.passwordEdit);
        login_title = (TextView) loginDialog.findViewById(R.id.titleLogin);
        password_title = (TextView) loginDialog.findViewById(R.id.titlePassword);
        loginDialog.show();

        // ustaw nazwe jaka była wpisana wcześniej jeśli to kolejna próba logowania
        login_text.setText(tempLogin);

        login.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                String loginStr = login_text.getText().toString();
                String passwordStr = password_text.getText().toString();
                if (loginStr.length() <6 ){
                    login_title.setText("Nazwa jest za krótka");
                    login_title.setTextColor(Color.RED);
                    loginDataOk = false;
                }
                else {
                    loginDataOk = true;
                    login_title.setTextColor(Color.BLACK);
                    login_title.setText("Nazwa:");
                }

                if (passwordStr.length() <4 ){
                    password_title.setText("Hasło jest za krótkie");
                    password_title.setTextColor(Color.RED);
                    loginDataOk = false;
                }else{
                    password_title.setTextColor(Color.BLACK);
                    password_title.setText("Hasło:");
                }

                if(loginDataOk)
                {
                    tempLogin = loginStr;
                    String tempData = loginStr + ":" + passwordStr;
                    // tu wyslemy dane do mikrokontrolera
                    if (tempData.length()<32) {
                        btSend(Constants.LOGIN_PASSWORD_DATA_FUNC, tempData);
                        showProgressDialogLogin();


                        errorTimer1 = new Timer();
                        errorTimer1.schedule(new firstTask1(), Constants.TIMEOUT_RECEIVE_LOGIN,
                                Constants.TIMEOUT_RECEIVE_LOGIN);
                        loginDialog.cancel();
                    }else{
                        // zle dane
                    }
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

    public void hideLoginDialog()
    {
        loginDialog.dismiss();
    }

}

//tells handler to send a message
class firstTask1 extends TimerTask {

    @Override
    public void run() {

        Message wiadomosc = new Message();

        wiadomosc.what = Constants.LOGIN_TIMEOUT_MSG;

        // Dodajemy treść, używamy jednego z dostępnych pól w Message
        wiadomosc.obj = 255;

        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
        MainActivity.handler.sendMessage(wiadomosc);
    }
};

class ReceiveBT extends Thread{

    private InputStream is = null;
    private Boolean dataRdy=false;
    private Handler h1;

    //private static final int START_FRAME = ':', END_FRAME ='\n' , HEAD_POS=1, ADRESS_POS=4, VALUE_POS=6, LRC_POS =8;

    ReceiveBT(BluetoothSocket socket, Handler handler){

        try{
            is = socket.getInputStream();
            h1 = handler;
        }catch (IOException ignored){

        }

        // Log.d(TAG, "Create ConnectThread");
    }

    public void run() {
        // TODO Auto-generated method stub
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

                    //System.arraycopy(buff, 0, buffer, 0, bytes);

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

                        if (frameData[Constants.FUNC_POS] == Constants.LOGIN_PASSWORD_DATA_FUNC)
                        {
                            // Tworzymy nową wiadomość
                            Message wiadomosc = new Message();

                            // Dodajemy treść, używamy jednego z dostępnych pól w Message

                            wiadomosc.what = Constants.LOGIN_PASSWORD_DATA_MSG;

                            wiadomosc.obj = frameData[Constants.CODE_POS];

                            h1.sendMessage(wiadomosc);

                        }

                        if (frameData[Constants.FUNC_POS] == Constants.WRITE_FUNC)
                        {
                            // Tworzymy nową wiadomość
                            Message wiadomosc = new Message();

                            // Dodajemy treść, używamy jednego z dostępnych pól w Message

                            wiadomosc.what = Constants.WRITE_FUNC_MSG;

                            wiadomosc.obj = frameData;

                            h1.sendMessage(wiadomosc);
                        }

                        if (frameData[Constants.FUNC_POS] == Constants.READ_FUNC)
                        {
                            // Tworzymy nową wiadomość
                            Message wiadomosc = new Message();

                            // Dodajemy treść, używamy jednego z dostępnych pól w Message

                            wiadomosc.what = Constants.READ_FUNC_MSG;

                            wiadomosc.obj = frameData;

                            ParametersActivity.parametrsHandler.sendMessage(wiadomosc);
                        }

                        if (frameData[Constants.FUNC_POS] == Constants.PERMISSIONS_ERROR_FUNC)
                        {
                            // Tworzymy nową wiadomość
                            Message wiadomosc = new Message();

                            // Dodajemy treść, używamy jednego z dostępnych pól w Message

                            wiadomosc.what = Constants.PERMISSIONS_ERROR_MSG;

                            wiadomosc.obj = frameData;

                            h1.sendMessage(wiadomosc);
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



/*

przetwarzanie na tekst
while(true){
            try{

                bytes = is.read(buffer);

                // ramka [:][nagłówek][adres][wartość|[LRC][CR][LF]
                if(bytes>0){
                    byte[] newbuffer = new byte[bytes];
                    for(int i=0;i<bytes;i++)
                        newbuffer[i]=buffer[i];

                    data = new String(newbuffer, "US-ASCII");
                    tempData= tempData+data;
                    if(tempData.contains("\r\n")) {

                        outData = tempData;
                        tempData ="";
                        reciveStrCounter=0;
                        dataRdy=true;

                        // Tworzymy nową wiadomość
                        Message wiadomosc = new Message();

                        wiadomosc.what =2;

                        // Dodajemy treść, używamy jednego z dostępnych pól w Message
                        wiadomosc.obj = outData;



                        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
                        h1.sendMessage(wiadomosc);

                    }
                    else
                    {
                        reciveStrCounter++;
                        if(reciveStrCounter>32)
                        {
                            outData = "Brak zanku CRLF";
                            tempData ="";
                            reciveStrCounter=0;
                        }
                    }
                }

            }catch(IOException e){
                break;
            }
        }
 */
