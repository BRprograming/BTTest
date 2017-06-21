package br.com.bttest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ParametersActivity extends AppCompatActivity {

    EditText valueSet;
    EditText adressSet;
    TextView funcRead;
    TextView codeRead;
    TextView valueRead;
    TextView crcRead;
    static Handler parametrsHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        valueSet = (EditText) findViewById(R.id.parameter_value_editText);
        adressSet = (EditText) findViewById(R.id.parameter_number_editText);
        funcRead = (TextView) findViewById(R.id.textViewFunc);
        codeRead = (TextView) findViewById(R.id.textViewCode);
        valueRead = (TextView) findViewById(R.id.textViewValue);
        crcRead = (TextView) findViewById(R.id.textViewCRC);

        parametrsHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg.what== Constants.READ_FUNC_MSG){

                    int[] frameData = (int[]) msg.obj;

                    Integer temp = frameData[Constants.FUNC_POS];
                    funcRead.setText( temp.toString() );

                    temp = frameData[Constants.CODE_POS];
                    codeRead.setText( temp.toString() );

                    temp = frameData[Constants.VAL_POS];
                    valueRead.setText( temp.toString() );

                    temp = frameData[Constants.CRC_POS];
                    crcRead.setText( temp.toString() );
                }
            }
        };

    }
    //TODO obsługa przycisku fizycznego cofnij

    public void SendVal (View view){

        int intAdress = Integer.parseInt(adressSet.getText().toString());
        int intValue = Integer.parseInt(valueSet.getText().toString());

        int[] adressAndValue = new  int[2];


        adressAndValue[0] = intAdress;
        adressAndValue[1] = intValue;

        Message wiadomosc = new Message();

        wiadomosc.what = Constants.SEND_VAL_MSG;

        // Dodajemy treść, używamy jednego z dostępnych pól w Message
        wiadomosc.obj = adressAndValue;

        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
        MainActivity.handler.sendMessage(wiadomosc);


    }

    public void ReadVal (View view){

        int intAdress = Integer.parseInt(adressSet.getText().toString());
        int intValue = Integer.parseInt(valueSet.getText().toString());

        int[] adressAndValue = new  int[2];;


        adressAndValue[0] = intAdress;
        adressAndValue[1] = intValue;

        Message wiadomosc = new Message();

        wiadomosc.what = Constants.REQUEST_READ_VAL_MSG;

        // Dodajemy treść, używamy jednego z dostępnych pól w Message
        wiadomosc.obj = adressAndValue;

        // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
        MainActivity.handler.sendMessage(wiadomosc);
    }
}
