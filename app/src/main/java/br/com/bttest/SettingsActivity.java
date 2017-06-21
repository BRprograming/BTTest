package br.com.bttest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dane_sterowania, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent_home = new Intent(this, MainActivity.class);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            intent_home.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);//wraca do aktywnosci a nie uruchamia jej na nowo
            startActivity(intent_home);
        }
        if (id == R.id.action_save) {
            Toast toast_save = Toast.makeText(getApplicationContext(), "Zapisano parametry", Toast.LENGTH_SHORT);
            toast_save.show();
            intent_home.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent_home);


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            int[] adressAndValue = new int[2];


            adressAndValue[0] = 255;
            adressAndValue[1]  =Integer.parseInt(sharedPreferences.getString("LCD_off","0")) ;



            Message wiadomosc = new Message();

            wiadomosc.what = Constants.SEND_VAL_MSG;

            // Dodajemy treść, używamy jednego z dostępnych pól w Message
            wiadomosc.obj = adressAndValue;

            // Oczywiście wysyłamy na końcu naszą wiadomość do Handlera
            MainActivity.handler.sendMessage(wiadomosc);

        }

        return super.onOptionsItemSelected(item);
    }


}