package br.com.bttest;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bartuso on 2017-06-22.
 */

public class ErrorListAdapter extends ArrayAdapter<Error> {

    private Context mContext;
    int mResource;

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

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView textViewErrorNumber = (TextView) convertView.findViewById(R.id.textViewErrorNumber);
        TextView textViewErrorDate = (TextView) convertView.findViewById(R.id.textViewErrorDate);
        TextView textViewErrorFloor = (TextView) convertView.findViewById(R.id.textViewErrorFloor);

        textViewErrorNumber.setText(number);
        textViewErrorDate.setText(date);
        textViewErrorFloor.setText(floor);

        return convertView;
    }
}
