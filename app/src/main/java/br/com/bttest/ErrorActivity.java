package br.com.bttest;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ErrorActivity extends AppCompatActivity {



    ArrayList arrayListError;
    ArrayAdapter arrayAdapterError;
    ListView errorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorList = (ListView) findViewById(R.id.errorList);

        arrayListError = new ArrayList();
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 1" + "\n" + "20.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 2" + "\n" + "21.11.2017"); //Get the device's name and the address
        arrayListError.add("ERROR 3" + "\n" + "22.11.2017"); //Get the device's name and the address

        arrayAdapterError = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListError);
        errorList.setAdapter(arrayAdapterError);
        errorList.setOnItemClickListener(myListClickListener2); //Method called when the device from the list is clicked



    }

    private AdapterView.OnItemClickListener myListClickListener2 = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            Toast.makeText(ErrorActivity.this, "Bład :  " + info.substring(0,7), Toast.LENGTH_SHORT).show();
            info.substring(info.length() - 17);

            AlertDialog.Builder builder = new AlertDialog.Builder(ErrorActivity.this,
                    R.style.Theme_AppCompat_Light_Dialog);

            builder.setMessage("Bład :  " + info.substring(0,7) + "\n"+ "tu opis błęda hdsgfidgyiadfsigdsg fdsafdsf dasgas");

            builder.setCancelable(true);



            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {


                }
            });

            AlertDialog alert = builder.create();
            alert.setTitle("Coś się psuje !!!");
            //alert.setIcon(R.drawable.err);

            alert.show();

            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.RIGHT);
        }


    };
}
