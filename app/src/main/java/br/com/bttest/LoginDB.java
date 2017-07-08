package br.com.bttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bartuso on 2017-07-08.
 */

public class LoginDB extends SQLiteOpenHelper {

    //private static final String DB_NAME = "errorDB"; //nie podaje nazwy zeby sie nie instalowala na stałe
    private static final int DB_VERSION = 1;


    LoginDB(Context context){
        super(context, null, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //tworzenie tabeli
        db.execSQL("CREATE TABLE LOGIN (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ADDRESS TEXT, "
                + "LOGIN TEXT, "
                + "PASSWORD TEXT);");

        //dodawanie danych do tabeli
        insertLoginData(db, "address1", "loginSQL", "passwordSQL");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //metoda pobierająca dane do tabeli
    public static void insertLoginData(SQLiteDatabase db, String address, String login, String password) {
        ContentValues loginValues = new ContentValues();
        loginValues.put("ADDRESS", address);
        loginValues.put("LOGIN", login);
        loginValues.put("PASSWORD", password);
        db.insert("LOGIN", null, loginValues);
    }
}
