package com.confinement.diconfinement;

import android.content.Context;
import android.database.MatrixCursor;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DisplayUtils {

    private DisplayUtils() {
        throw new IllegalStateException("Utility class");
    }

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

     static String removeUnwantedCharacters(String string) {
        string = string.replace(";:", "");
        string = string.replace("<li>", "");
        string = string.replace("</li>", "");
        return string;
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

    public static List<SpannableString> createSpannableFromString(List<String> definition) {
        List<SpannableString> spanStrings = new ArrayList<>();
        for (String defPart : definition){
            defPart = DisplayUtils.removeUnwantedCharacters(defPart);
            spanStrings.add(new SpannableString(DisplayUtils.trimTrailingWhitespace(Html.fromHtml(defPart))));
        }
        return spanStrings;
    }

    static void hideHelpMenu(FragmentActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.help_game) != null) {
            toolbar.getMenu().findItem(R.id.help_game).setVisible(false);
        }
    }


    public static void displayHelpMenu(FragmentActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.help_game) != null) {
            toolbar.getMenu().findItem(R.id.help_game).setVisible(true);
        }
    }

    static void hideAddMenu(FragmentActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.add_word) != null) {
            toolbar.getMenu().findItem(R.id.add_word).setVisible(false);
        }
    }

    static void displayAddMenu(FragmentActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);

        if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.add_word) != null) {
            toolbar.getMenu().findItem(R.id.add_word).setVisible(true);
        }
    }

    static void setIconAlpha(boolean needsSave, Drawable icon) {
        if (needsSave) {
            icon.setAlpha(255);
        } else {
            icon.setAlpha(50);
        }
    }

    static void hideLoadingImage(Toolbar toolbar, View loadingImage, TabLayout tabLayout) {
        loadingImage.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
    }

    static void displayLoadingImage(Toolbar toolbar, View loadingImage, TabLayout tabLayout) {
        toolbar.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
        loadingImage.setVisibility(View.VISIBLE);
    }
}
