package br.com.bttest;

public interface Constants {

    public static final String TAG ="TAG";

    public static final int REQUEST_ENABLE_BT = 10000 ;
    public static final int REQUEST_BT_PAIRING = 10001 ;

    public static final int STATE_READY= 		    1;
    public static final int STATE_CONNECTING= 		2;
    public static final int STATE_CONNECT_ERROR=    4;
    public static final int STATE_CONNECT= 		    8;
    public static final int STATE_LOGIN= 		    16;
    public static final int STATE_LOGIN_ERROR= 		32;
    public static final int STATE_ERROR= 		    64;
    public static final int STATE_LOGIN_ADMIN=      128;
    public static final int STATE_DISCONNECT =      256;

    public static final int TIMEOUT_RECEIVE_LOGIN = 3000 ;
    public static final int TIMEOUT_DISCONNECT =    8000 ;
    public static final int TIMEOUT_CONNECT =       8000 ;

    // Message types sent from the BluetoothChatService Handler
    public static final byte START_CONNECT_MSG = 1 ;
    public static final byte CONNECT_END_MSG = 2 ;
    public static final byte SET_DISCONNECT_MSG = 3 ;

    public static final int LOGIN_PASSWORD_DATA_MSG =10;
    public static final int PERMISSIONS_ERROR_MSG =11;
    public static final byte FRAME_DATA_MSG = 12 ;

    public static final int WRITE_FUNC_MSG = 21 ;
    public static final int READ_FUNC_MSG = 22 ;

    public static final int DISCONNECT_TIMEOUT_MSG = 31 ;
    public static final int CONNECT_TIMEOUT_MSG = 32 ;
    public static final int LOGIN_TIMEOUT_MSG = 36 ;

    public static  final int SEND_VAL_MSG   =41;
    public static  final int REQUEST_READ_VAL_MSG   =42;


    // struktura ramki
    public static final byte FUNC_POS = 0 ;
    public static final byte CODE_POS = 1 ;
    public static final byte VAL_POS = 2 ;
    public static final byte CRC_POS = 3 ;

    public static final int WRITE_FUNC = 1 ;
    public static final int READ_FUNC = 2 ;
    public static final int LOGIN_PASSWORD_DATA_FUNC = 5 ;
    public static final int LOGOUT_FUNC = 6 ;
    public static final int PERMISSIONS_ERROR_FUNC =10;




    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";


}
