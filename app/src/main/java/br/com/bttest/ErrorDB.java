package br.com.bttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bartuso on 2017-07-07.
 */

public class ErrorDB extends SQLiteOpenHelper {

    //private static final String DB_NAME = "errorDB"; //nie podaje nazwy zeby sie nie instalowala na stałe
    private static final int DB_VERSION = 1;

    ErrorDB(Context context){
        super(context, null, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //tworzenie tabeli
        db.execSQL("CREATE TABLE ERRORS (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NUMBER INTEGER, "
                + "NAME TEXT, "
                + "DESCRIPTION TEXT, "
                + "TYPE TEXT);");

        //dodawanie danych do tabeli

        insertError(db, 01, "Przerwa w obwodzie bezpieczeństwa.", "Rozwarty łącznik bezpieczeństwa podczas jazdy normalnej bądź jazdy kontrolnej." +
                " Zarejestrowano przerwę w obwodzie bezpieczeństwa kontrolowanym przez wejścia EK, HK i SK. Jazda dźwigu" +
                "zostaje przerwana, aŜ do momentu zniknięcia przyczyny zatrzymania. Polecenia jazdy zostają skasowane.\n" +
                "Otwarty łącznik stop w podszybiu lub na kabinie, łącznik obciążki, łącznik krańcowy, łącznik" +
                "ogranicznika prędkości, łącznik chwytaczy lub łącznik zwisu lin.",
                "zdarzenie");

        insertError(db, 34, "Przekroczony łącznik krańcowy dźwigu hydraulicznego.", "Podczas jazdy normalnej został przekroczony łącznik końcowy (wejście EK) przez dźwig\n" +
                "hydrauliczny.\n" +
                "Awaria systemu odwzorowania połoŜenia. Zbyt mała odległość pomiędzy łącznikiem krańcowym\n" +
                "a poziomem przystanku.\n" +
                "Usterka wymaga interwencji konserwatora – dalsza jazda dźwigu nie jest moŜliwa nawet po" +
                "sprowadzeniu kabiny z łącznika krańcowego i ponownym uruchomieniu sterowania. Aby" +
                "przywrócić dźwig do pracy normalnej naleŜy wprowadzić znacznik (kod 0) na początek rejestru" +
                "usterek i ponownie uruchomić sterowanie..",
                "usterka");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //metoda pobierająca dane do tabeli
    private static void insertError(SQLiteDatabase db, int number, String name, String description, String type) {
        ContentValues errorValues = new ContentValues();
        errorValues.put("NUMBER", number);
        errorValues.put("NAME", name);
        errorValues.put("DESCRIPTION", description);
        errorValues.put("TYPE", type);
        db.insert("ERRORS", null, errorValues);
    }
}
