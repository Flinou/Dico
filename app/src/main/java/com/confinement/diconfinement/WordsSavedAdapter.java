package com.confinement.diconfinement;

import android.content.Context;
import android.os.Build;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.List;

public class WordsSavedAdapter extends ArrayAdapter<SpannableString> {
    private final Context context;
    private final List<SpannableString> objects;

    public WordsSavedAdapter(@NonNull Context context, @NonNull List<SpannableString> objects) {
        super(context, R.layout.savedwords_text_view, objects);
        this.context = context;
        this.objects=objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.savedwords_text_view, parent, false);
        TextView textView = rowView.findViewById(R.id.savedwordstextview);
        ImageView imageView = rowView.findViewById(R.id.logo);
        textView.setText(objects.get(position));
        imageView.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View view) {
                File filesDir = context.getFilesDir();
                FileUtils.removeFromFile(filesDir, objects.get(position).toString());
                ((MainActivity) context).refreshPage();
            }
        });

        return rowView;
    }
}
