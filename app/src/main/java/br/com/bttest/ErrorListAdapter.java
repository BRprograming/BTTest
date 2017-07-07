package br.com.bttest;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by bartuso on 2017-06-22.
 */

public class ErrorListAdapter extends ArrayAdapter<Error> implements View.OnClickListener {

    private Context mContext;
    int mResource;

    private static class ViewHolder {
        TextView textViewErrorNumber;
        TextView textViewErrorDate;
        TextView textViewErrorFloor;
        ImageView imageViewErrorInfo;
    }

    public ErrorListAdapter(Context context, int resource, ArrayList<Error> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the errors information
        String number = getItem(position).getNumber();
        String date = getItem(position).getDate();
        String floor = getItem(position).getFloor();

        Error error = new Error(number, date, floor);

        ViewHolder viewHolder;

        viewHolder = new ViewHolder();

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        viewHolder.textViewErrorNumber = (TextView) convertView.findViewById(R.id.textViewErrorNumber);
        viewHolder.textViewErrorDate = (TextView) convertView.findViewById(R.id.textViewErrorDate);
        viewHolder.textViewErrorFloor = (TextView) convertView.findViewById(R.id.textViewErrorFloor);
        viewHolder.imageViewErrorInfo = (ImageView) convertView.findViewById(R.id.imageButtonErrorInfo);

        convertView.setTag(viewHolder);

        viewHolder.textViewErrorNumber.setText(number);
        viewHolder.textViewErrorDate.setText(date);
        viewHolder.textViewErrorFloor.setText(floor);
        viewHolder.imageViewErrorInfo.setOnClickListener(this);
        viewHolder.imageViewErrorInfo.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Error error = (Error) object;



        switch (v.getId())
        {
            case R.id.imageButtonErrorInfo:
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext,
                        R.style.Theme_AppCompat_Light_Dialog_Alert);



                int errorNo = Integer.parseInt(error.getNumber());

                try {
                    SQLiteOpenHelper errorDatabaseHelper = new ErrorDB(mContext);
                    SQLiteDatabase db = errorDatabaseHelper.getReadableDatabase();
                    Cursor cursor = db.query("ERRORS",
                            new String[] {"NUMBER", "NAME", "DESCRIPTION", "TYPE"}, "NUMBER = ?",
                            new String[] {Integer.toString(errorNo)}, null, null, null);

                    if (cursor.moveToFirst()) {
                        String nameText = cursor.getString(1);
                        String descriptionText = cursor.getString(2);
                        String typeText = cursor.getString(3);

                        builder.setMessage(descriptionText + "\nTyp: " + typeText);
                        builder.setCancelable(true);
                        //builder.setIcon(R.drawable.ic_notifications_black_24dp);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        });
                        AlertDialog alert = builder.create();

                        alert.setTitle("Kod " + error.getNumber() + ": " + nameText);

                        alert.show();

                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        alert.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.RIGHT);
                    }
                    cursor.close();
                    db.close();
                }
                catch (SQLiteException e) {
                    Toast toast = Toast.makeText(mContext, "Baza danych jest niedostępna", Toast.LENGTH_SHORT);
                    toast.show();
                }



                break;
        }

    }

}
