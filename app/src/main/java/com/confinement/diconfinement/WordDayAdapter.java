package com.confinement.diconfinement;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class WordDayAdapter extends ArrayAdapter<SpannableString> {
    private final Context context;
    private final List<SpannableString> objects;

    public WordDayAdapter(@NonNull Context context, @NonNull List<SpannableString> objects) {
        super(context, R.layout.textview, objects);
        this.context = context;
        this.objects=objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final WordDayAdapter wsa = this;
        View rowView = inflater.inflate(R.layout.textview, parent, false);
        TextView textView = rowView.findViewById(R.id.textview);
        textView.setText(objects.get(position));

        return rowView;
    }
}
