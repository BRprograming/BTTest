package br.com.bttest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ErrorActivity extends AppCompatActivity {


    ListView errorList;
    ArrayList<Error> arrayListError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorList = (ListView) findViewById(R.id.errorList);

        Error no1 = new Error("01","22.06.2017 18:22", "5");
        Error no2 = new Error("34","22.06.2017 18:24", "10");
        Error no3 = new Error("12","22.06.2017 18:24", "10");
        Error no4 = new Error("12","22.06.2017 18:24", "10");
        Error no5 = new Error("12","22.06.2017 18:24", "10");
        Error no6 = new Error("12","22.06.2017 18:24", "10");
        Error no7 = new Error("12","22.06.2017 18:24", "10");
        Error no8 = new Error("12","22.06.2017 18:24", "10");
        Error no9 = new Error("12","22.06.2017 18:24", "10");
        Error no10 = new Error("12","22.06.2017 18:24", "10");

        arrayListError = new ArrayList<>();

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

}
