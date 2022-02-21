package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Build;
import android.text.SpannableString;
import android.view.Menu;
import android.widget.SearchView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class AutoCompletion {

    private static volatile AutoCompletion instance = null;
    private AutoCompletion() {
        super();
    }

    public static final AutoCompletion getInstance() {
        if (AutoCompletion.instance == null) {
            synchronized(AutoCompletion.class) {
                if (AutoCompletion.instance == null) {
                    AutoCompletion.instance = new AutoCompletion();
                }
            }
        }
        return AutoCompletion.instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void autocomplete(String query, Menu menu, ContextWrapper ctx) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        String[] columns = {"_id", Globals.COLUMN_SUGGESTION};
        MatrixCursor cursor = new MatrixCursor(columns);
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(Globals.getDicoWords(ctx.getResources().openRawResource(R.raw.dico)), query);
        if (!suggestions.isEmpty()) {
            DisplayUtils.addSuggestions(cursor, suggestions);
        }
        autoCompleteRefresh(searchView, cursor, ctx);
    }

    private void autoCompleteRefresh(SearchView searchView, MatrixCursor cursor, ContextWrapper ctx) {
        //Notify search view adapter of changes in the suggestion field
        searchView.setSuggestionsAdapter(new AutoCompletionAdapter(ctx, cursor));
        searchView.getSuggestionsAdapter().notifyDataSetChanged();
    }

    Intent createIntent(SearchView searchView) {
        String seekedWord = searchView.getSuggestionsAdapter().getCursor().getString(1);
        DisplayUtils.hideSearchBar(searchView);
        return createSearchIntent(new SpannableString(seekedWord));
    }

    private Intent createSearchIntent(SpannableString savedWord) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,savedWord.toString());
        intent.setComponent(new ComponentName(Globals.PACKAGE_NAME, Globals.PACKAGE_NAME + ".SearchResultsActivity"));
        return intent;
    }


}
