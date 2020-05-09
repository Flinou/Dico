package com.confinement.diconfinement;

import android.content.Context;
import android.database.MatrixCursor;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class DisplayUtils {

    static void displayToast(Context context, String stringToDisplay) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, stringToDisplay, duration);
        toast.show();
    }

    static CharSequence trimTrailingWhitespace(CharSequence source) {
        if(source == null)
            return "";
        int i = source.length();

        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }
        return source.subSequence(0, i+1);
    }

     static void removeUnwantedCharacters(String[] stringArray, int cpt, Matcher m) {
        if (m.matches()){
            if (m.group(1) != null) {
                stringArray[cpt] = stringArray[cpt].replace(m.group(1), "<br><i>");
            }
            if (m.group(2) != null) {
                stringArray[cpt] = stringArray[cpt].replace(m.group(2), "</i>");
            }
        }
        stringArray[cpt] = stringArray[cpt].replace(";:", "");
        stringArray[cpt] = stringArray[cpt].replace("<li>", "");
        stringArray[cpt] = stringArray[cpt].replace("</li>", "");
    }


     static void addSuggestions(MatrixCursor cursor, ArrayList<String> suggestions) {
        Integer id = 1;
        for (String suggestion : suggestions) {
            String idInCursor = Integer.toString(id);
            cursor.addRow(new String[]{idInCursor, suggestion});
            id++;
        }
    }

    static void hideSearchBar(SearchView search) {
        search.setQuery("", false);
        search.setIconified(true);
    }
}
