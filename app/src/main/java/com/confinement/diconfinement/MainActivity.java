package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
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


public class MainActivity extends AppCompatActivity {

    protected static final String columnSuggestion = "wordSuggestion";
    protected static final Integer suggestionNumbers = 3;
    Integer index, top;
    private Menu menu;
    ListView listView = null;

    public Menu getMenu() {
        return menu;
    }
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.savedWords_list);
        setSupportActionBar(toolbar);

        displaySavedWords(listView);
        final ImageView imageView = findViewById(R.id.logo);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = listView.getItemAtPosition(position);
                //Save list index and top when exiting activity
                index = listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displaySpinner(toolbar, listView, progressBar);
                    }
                });
                FileUtils.initFirstWordDicoHashMap(getApplicationContext());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSpinner(progressBar, toolbar, listView);
                    }
                });
            }
        }).start();
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
        intent.setComponent(new ComponentName(Globals.packageName, Globals.packageName + ".SearchResultsActivity"));
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        displaySavedWords((ListView) findViewById(R.id.savedWords_list));
        //Set saved list position when returning to activity
        if (index != null && top != null){
            listView.setSelectionFromTop(index, top);
        }
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

        setMenu(menu);
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
                AutoCompletion.getInstance().autocomplete(newText, getMenu(), (ContextWrapper) getApplicationContext());
                return true;
            }

        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                startActivity(AutoCompletion.getInstance().createIntent(searchView));
                return true;
            }
        });
        return true;
    }


}
