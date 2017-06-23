package br.com.bttest;

import android.content.Context;
import android.content.DialogInterface;
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

                builder.setMessage("Zadziałanie termistora silnika napędu dźwigu. Dźwig zostaje odesłany na przystanek parkowania i zatrzymany do czasu opadnięcia temperatury silnika.\n" +
                        "Awaria układu kontroli temperatury silnika napędu bądź falownika.");

                //TODO dodać funkcję która dobierze opis do błędu

                builder.setCancelable(true);



                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });

                AlertDialog alert = builder.create();
                alert.setTitle("Kod usterki: " + error.getNumber() + "\nPrzegrzanie silnika napędu.");

                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.RIGHT);

                break;
        }

        }

    }
