package com.confinement.diconfinement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class AutoCompletionAdapter extends CursorAdapter {
        private TextView text;

        AutoCompletionAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }

        private TextView getText() {
            return this.text;
        }

        private void setText(TextView textView) {
            this.text = textView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if (cursor != null) {
                text.setText(cursor.getString(cursor.getColumnIndex(Globals.columnSuggestion)));
            }
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                View view = inflater.inflate(R.layout.suggestion, parent, false);
                setText((TextView) view.findViewById(R.id.suggestion));
                return view;
            }
            return null;
        }

    }
