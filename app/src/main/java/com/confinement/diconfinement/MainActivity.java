package com.confinement.diconfinement;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.TreeSet;


public class MainActivity extends AppCompatActivity {
    private String packageName = "com.confinement.diconfinement";

    protected static final String columnSuggestion = "wordSuggestion";
    protected static final Integer suggestionNumbers = 3;

    private Menu menu;


    private TreeSet<String> dicoWords = null;
    private Handler handler = new Handler();


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ListView listView = findViewById(R.id.savedWords_list);
        displaySavedWords(listView);
        final ImageView imageView = findViewById(R.id.logo);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = listView.getItemAtPosition(position);
                SpannableString savedWord = (SpannableString) obj;
                if (savedWord != null) {
                    Intent intent = createSearchIntent(savedWord);
                    startActivity(intent);
                }
            }
        });
        final ProgressBar progressBar = findViewById(R.id.pBar);

        new Thread(new Runnable() {
            public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            System.out.println("ici");
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displaySpinner(toolbar, listView, progressBar);
                        }
                    });
                    setDicoWords(FileUtils.populateDicoWords(getApplicationContext().getResources().openRawResource(R.raw.dico)));
                    FileUtils.initializeDictionary(getApplicationContext());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideSpinner(progressBar, toolbar, listView);
                        }
                    });
                }
        }).start();
    }

    public void setDicoWords(TreeSet<String> dicoWords) {
        this.dicoWords = dicoWords;
    }

    public TreeSet<String> getDicoWords() {
        return dicoWords;
    }

    private void hideSpinner(ProgressBar progressBar, Toolbar toolbar, ListView listView) {
        progressBar.setVisibility(View.GONE);
        TextView loadingText = findViewById(R.id.loadingTextView);
        loadingText.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.VISIBLE);
        TextView textV =findViewById(R.id.vosmots);
        textV.setVisibility(View.VISIBLE);
    }

    private void displaySpinner(Toolbar toolbar, ListView listView, ProgressBar progressBar) {
        toolbar.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        TextView textV = findViewById(R.id.vosmots);
        textV.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        TextView loadingText = findViewById(R.id.loadingTextView);
        loadingText.setVisibility(View.VISIBLE);
    }


    private Intent createSearchIntent(SpannableString savedWord) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,savedWord.toString());
        intent.setComponent(new ComponentName(packageName, packageName + ".SearchResultsActivity"));
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        displaySavedWords((ListView) findViewById(R.id.savedWords_list));
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displaySavedWords(ListView listView) {
        ArrayList<SpannableString> savedWords = FileUtils.retrieveSavedWords(getApplicationContext());
        listView.setAdapter(new WordsSavedAdapter(this, savedWords));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.menu = menu;
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                DisplayUtils.hideSearchBar(searchView);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String newText) {
                autocomplete(newText);
                return true;
            }
        });

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void autocomplete(String query) {
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        String[] columns = {"_id", columnSuggestion};
        MatrixCursor cursor = new MatrixCursor(columns);
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(getDicoWords(), query);
            if (!suggestions.isEmpty()) {
                DisplayUtils.addSuggestions(cursor, suggestions);
                addListenersToSuggestions(searchView);
            }
        autoCompleteRefresh(searchView, cursor);
    }

    private void autoCompleteRefresh(SearchView searchView, MatrixCursor cursor) {
        //Notify search view adapter of changes in the suggestion field
        searchView.setSuggestionsAdapter(new AutoCompletionAdapter(this, cursor));
        searchView.getSuggestionsAdapter().notifyDataSetChanged();
    }

    private void addListenersToSuggestions(final SearchView searchView) {
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return getSuggestionDefinition(searchView);
            }
        });
    }

    private boolean getSuggestionDefinition(SearchView searchView) {
        String seekedWord = searchView.getSuggestionsAdapter().getCursor().getString(1);
        DisplayUtils.hideSearchBar(searchView);
        Intent intent = createSearchIntent(new SpannableString(seekedWord));
        startActivity(intent);
        return true;
    }

}
