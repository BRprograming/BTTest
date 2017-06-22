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


    ListView errorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorList = (ListView) findViewById(R.id.errorList);

        Error no1 = new Error("40","22.06.2017 18:22", "5");
        Error no2 = new Error("12","22.06.2017 18:24", "10");
        Error no3 = new Error("12","22.06.2017 18:24", "10");
        Error no4 = new Error("12","22.06.2017 18:24", "10");
        Error no5 = new Error("12","22.06.2017 18:24", "10");
        Error no6 = new Error("12","22.06.2017 18:24", "10");
        Error no7 = new Error("12","22.06.2017 18:24", "10");
        Error no8 = new Error("12","22.06.2017 18:24", "10");
        Error no9 = new Error("12","22.06.2017 18:24", "10");
        Error no10 = new Error("12","22.06.2017 18:24", "10");

        ArrayList<Error> arrayListError = new ArrayList<>();
        arrayListError.add(no1);
        arrayListError.add(no2);
        arrayListError.add(no3);
        arrayListError.add(no4);
        arrayListError.add(no5);
        arrayListError.add(no6);
        arrayListError.add(no7);
        arrayListError.add(no8);
        arrayListError.add(no9);
        arrayListError.add(no10);

        ErrorListAdapter adapter = new ErrorListAdapter(this, R.layout.error_list_layout, arrayListError);
        errorList.setAdapter(adapter);






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
