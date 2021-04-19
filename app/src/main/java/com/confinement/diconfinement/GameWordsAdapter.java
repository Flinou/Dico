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

public class GameWordsAdapter extends ArrayAdapter<SpannableString> {
    private final Context context;
    private final List<SpannableString> objects;

    public GameWordsAdapter(@NonNull Context context, @NonNull List<SpannableString> objects) {
        super(context, R.layout.savedwords_text_view, objects);
        this.context = context;
        this.objects=objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final GameWordsAdapter wsa = this;
        View rowView = inflater.inflate(R.layout.gamewords_text_view, parent, false);
        TextView textView = rowView.findViewById(R.id.gamewordstextview);
        textView.setText(objects.get(position));

        return rowView;
    }
}
